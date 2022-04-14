/*
 * MIT License
 *
 * Copyright (c) 2022 Oliver Berg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.ormr.kommando.processor

import com.google.devtools.ksp.*
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSDefaultVisitor
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.writeTo
import net.ormr.kommando.KommandoBuilder
import net.ormr.kommando.commands.CommandGroup
import net.ormr.kommando.structures.CommandPrecondition
import net.ormr.kommando.structures.EventListener
import net.ormr.kommando.structures.MessageFilter
import org.kodein.di.DI
import kotlin.reflect.KClass

private val DI_MAIN_BUILDER = DI.MainBuilder::class.asClassName()
private val KOMMANDO_BUILDER = KommandoBuilder::class.asClassName()
private val BIND_SINGLETON = ClassName("org.kodein.di", "bindSingleton")

private enum class KommandoType(val clazz: KClass<*>, val collectionName: String) {
    EVENT_LISTENER(EventListener::class, "eventListeners"),
    MESSAGE_FILTER(MessageFilter::class, "messageFilters"),
    COMMAND_PRECONDITION(CommandPrecondition::class, "commandPreconditions"),
    COMMAND_GROUP(CommandGroup::class, "commands");

    val qualifiedName: String by lazy { clazz.qualifiedName ?: error("$clazz has no qualified name!") }

    companion object {
        private val registry: Map<String, KommandoType> = values().associateBy { it.qualifiedName }

        fun getTypeOrNull(qualifiedName: String): KommandoType? = registry[qualifiedName]

        fun isKnownType(qualifiedName: String): Boolean = qualifiedName in registry
    }
}

// TODO: injection might be a bit sus for things that have generics?
internal class KommandoProcessor(
    private val packageName: String,
    private val fileName: String,
    private val autoSearch: Boolean,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    @OptIn(KotlinPoetKspPreview::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val modules = findModules(resolver)

        if (modules.isEmpty()) return emptyList()

        val (toInject, kommandoTypes) = segregateTypes(modules)
        val file = FileSpec.builder(packageName, fileName)
            .addImport("org.kodein.di", "instance")
            .addFunction(buildKodeinSetup(toInject))
            .addFunction(buildKommandoBuilder(kommandoTypes))
            .build()
        file.writeTo(codeGenerator, Dependencies(aggregating = true, *modules.map { it.file }.toTypedArray()))

        return emptyList()
    }

    private fun buildKodeinSetup(
        toInject: List<TypedNode>,
    ): FunSpec = FunSpec.builder("setupDI").addModifiers(KModifier.INTERNAL).receiver(DI_MAIN_BUILDER).apply {
        toInject.forEach { it.node.accept(KodeinSingletonStatementBuilder, this) }
    }.build()

    private fun buildKommandoBuilder(
        types: Map<KommandoType, List<TypedNode>>,
    ): FunSpec = FunSpec.builder("setupKommando").addModifiers(KModifier.INTERNAL).receiver(KOMMANDO_BUILDER).apply {
        for ((type, nodes) in types) {
            addCode("${type.collectionName} += listOf(")
            nodes.forEach { it.node.accept(KommandoStatementBuilder, this) }
            addStatement(")")
        }
    }.build()

    private fun segregateTypes(modules: List<ModuleFile>): Pair<List<TypedNode>, Map<KommandoType, List<TypedNode>>> {
        val forInjection = mutableListOf<TypedNode>()
        val forBuilder = buildMap<KommandoType, MutableList<TypedNode>> {
            for ((_, nodes) in modules) {
                for (node in nodes) {
                    when (val type = node.returnType.toKommandoTypeOrNull()) {
                        null -> forInjection.add(node)
                        else -> getOrPut(type) { mutableListOf() }.add(node)
                    }
                }
            }
        }
        return forInjection to forBuilder
    }

    private fun findModules(resolver: Resolver): List<ModuleFile> = buildList {
        addAll(findModuleFileBindings(resolver))
        addAll(findStandaloneBindings(resolver))
        if (autoSearch) addAll(findAutoSearchedBindings(resolver))
    }

    private fun findModuleFileBindings(resolver: Resolver): Sequence<ModuleFile> =
        resolver.getSymbolsWithAnnotation(MODULE)
            .filterIsInstance<KSFile>()
            .map { file ->
                val nodes = file.declarations
                    .filter { it.isBindingsNode() }
                    .filter { it.isVisibleForInjection() }
                    .filterNot { it.shouldBeExcluded() }
                    .mapToTypedNodes()
                    .filter { (type, _) -> type.isKommandoType() }
                    .filter { (_, node) -> node.accept(BindingsSanityVerifier, logger) }
                    .toList()
                ModuleFile(file, nodes)
            }
            .onEach { if (it.nodes.isEmpty()) logger.warn("File marked as module, but no bindings found.", it.file) }
            .filter { it.nodes.isNotEmpty() }

    private fun findStandaloneBindings(resolver: Resolver): List<ModuleFile> {
        data class MergeWrapper(val file: KSFile, val nodes: MutableList<TypedNode>)

        val mergedFiles = hashMapOf<String, MergeWrapper>()
        resolver.getSymbolsWithAnnotation(INCLUDE)
            .filter { it.accept(StandaloneBindingsVerifier, logger) }
            .filter { it.accept(BindingsSanityVerifier, logger) }
            .filterIsInstance<KSDeclaration>()
            .mapToTypedNodes()
            .onEach { (type, node) ->
                if (node is KSPropertyDeclaration && !type.isKommandoType()) {
                    logger.warn("Try to avoid @Include on properties for injection purposes.", node)
                }
            }
            .mapNotNull { it.node.containingFile?.let { file -> file to it } }
            .filter { (file, node) -> if (file.isModuleFile()) !node.returnType.isKommandoType() else true }
            .forEach { (file, node) ->
                // unsure how caching works for KSFile instances, so using 'filePath' for safety
                val merged = mergedFiles.getOrPut(file.filePath) { MergeWrapper(file, mutableListOf()) }
                merged.nodes.add(node)
            }

        return mergedFiles.values.map { (file, nodes) -> ModuleFile(file, nodes.toList()) }
    }

    private fun findAutoSearchedBindings(resolver: Resolver): Sequence<ModuleFile> = resolver.getNewFiles()
        .filterNot { it.isModuleFile() }
        .map { file ->
            val nodes = file.declarations
                .filter { it.isVisibleForInjection() }
                .filter { it.isBindingsNode() }
                .filterNot { it.shouldBeExcluded() }
                .filterNot { it.isMarkedForInclusion() }
                .mapToTypedNodes()
                .filter { (type, _) -> type.isKommandoType() }
                .filter { (_, node) -> node.accept(BindingsSanityVerifier, logger) }
                .onEach { (_, node) -> logger.info("Found node via auto-search.", node) }
                .toList()
            ModuleFile(file, nodes)
        }
        .filter { it.nodes.isNotEmpty() }

    private fun Sequence<KSDeclaration>.mapToTypedNodes(): Sequence<TypedNode> =
        mapNotNull { node -> node.accept(ReturnTypeVisitor, logger)?.let { TypedNode(it, node) } }

    private data class ModuleFile(val file: KSFile, val nodes: List<TypedNode>)

    private data class TypedNode(val returnType: KSType, val node: KSDeclaration)
}

private fun FunSpec.Builder.addInstanceParameters(parameters: List<KSValueParameter>) {
    for (parameter in parameters) {
        when (val tag = parameter.getTagOrSubstituteOrNull()) {
            null -> addCode("instance(),")
            else -> addCode("instance(tag = %S),", tag)
        }
    }
}

private fun KSDeclaration.asClassName(): ClassName = ClassName(packageName.asString(), simpleName.asString())

@OptIn(KspExperimental::class)
private fun KSAnnotated.getBindingTagOrNull(): String? = getAnnotationsByType(BindingTag::class).firstOrNull()?.value

@OptIn(KspExperimental::class)
private fun KSAnnotated.getTagOrNull(): String? = getAnnotationsByType(Tag::class).firstOrNull()?.value

// we verify that only sound elements are let through at earlier points, so this *should* be a safe operation
private fun KSValueParameter.getTagOrSubstituteOrNull(): String? = getTagOrNull()?.ifEmpty { name!!.asString() }

@OptIn(KspExperimental::class)
private fun KSFile.isModuleFile(): Boolean = isAnnotationPresent(Module::class)

private fun KSDeclaration.isInModuleFile(): Boolean = containingFile?.isModuleFile() ?: false

private fun KSDeclaration.isBindingsNode(): Boolean = when (this) {
    is KSFunctionDeclaration -> extensionReceiver == null
    is KSPropertyDeclaration -> setter == null && extensionReceiver == null
    else -> false
}

private fun KSDeclaration.isVisibleForInjection(): Boolean = isPublic() || isInternal()

@OptIn(KspExperimental::class)
private fun KSDeclaration.isMarkedForInclusion(): Boolean = isAnnotationPresent(Include::class)

@OptIn(KspExperimental::class)
private fun KSDeclaration.shouldBeExcluded(): Boolean = isAnnotationPresent(Exclude::class)

private fun KSType.isKommandoType(): Boolean =
    declaration.qualifiedName?.asString()?.let { KommandoType.isKnownType(it) } ?: false

private fun KSType.toKommandoTypeOrNull(): KommandoType? =
    declaration.qualifiedName?.asString()?.let { KommandoType.getTypeOrNull(it) }

private object KodeinSingletonStatementBuilder : KSDefaultVisitor<FunSpec.Builder, Unit>() {
    override fun defaultHandler(node: KSNode, data: FunSpec.Builder) {
        error("Can't build kodein statement for node $node (${node::class}).")
    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: FunSpec.Builder) {
        val type = function.asClassName()
        when (val tag = function.getBindingTagOrNull()) {
            null -> {
                data.addCode("%T { %T(", BIND_SINGLETON, type)
                data.addInstanceParameters(function.parameters)
                data.addStatement(") }")
            }
            else -> {
                data.addCode("%T(tag = %S) { %T(", BIND_SINGLETON, tag, type)
                data.addInstanceParameters(function.parameters)
                data.addStatement(") }")
            }
        }
    }

    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: FunSpec.Builder) {
        val type = property.asClassName()
        when (val tag = property.getBindingTagOrNull()) {
            null -> data.addStatement("%T { %T }", BIND_SINGLETON, type)
            else -> data.addStatement("%T(tag = %S) { %T }", BIND_SINGLETON, tag, type)
        }
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: FunSpec.Builder) {
        val type = classDeclaration.asClassName()
        val constructor = classDeclaration.primaryConstructor ?: error("Class binding has no primary constructor.")
        when (val tag = classDeclaration.getBindingTagOrNull()) {
            null -> {
                data.addCode("%T { %T(", BIND_SINGLETON, type)
                data.addInstanceParameters(constructor.parameters)
                data.addStatement(") }")
            }
            else -> {
                data.addCode("%T(tag = %S) { %T(", BIND_SINGLETON, tag, type)
                data.addInstanceParameters(constructor.parameters)
                data.addStatement(") }")
            }
        }
    }
}

private object KommandoStatementBuilder : KSDefaultVisitor<FunSpec.Builder, Unit>() {
    override fun defaultHandler(node: KSNode, data: FunSpec.Builder) {
        error("Can't build kommando statement for node $node (${node::class}).")
    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: FunSpec.Builder) {
        data.addCode("%T(", function.asClassName())
        data.addInstanceParameters(function.parameters)
        data.addCode("),")
    }

    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: FunSpec.Builder) {
        val propertyType = property.asClassName()
        data.addCode("%T,", propertyType)
    }
}

private object StandaloneBindingsVerifier : KSDefaultVisitor<KSPLogger, Boolean>() {
    override fun defaultHandler(node: KSNode, data: KSPLogger): Boolean = false

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: KSPLogger): Boolean {
        var isValid = true
        if (!function.isVisibleForInjection()) {
            data.error("Function bindings must be public or internal.", function)
            isValid = false
        }
        // TODO: handle context receivers whenever we can do that
        if (function.extensionReceiver != null) {
            data.error("Function bindings can not have receivers.", function)
            isValid = false
        }
        return isValid
    }

    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: KSPLogger): Boolean {
        var isValid = true
        if (property.setter != null) {
            data.error("Property bindings must not be mutable.", property)
            isValid = false
        }
        if (!property.isVisibleForInjection()) {
            data.error("Property bindings must be public or internal.", property)
            isValid = false
        }
        // TODO: handle context receivers whenever we can do that
        if (property.extensionReceiver != null) {
            data.error("Property bindings can not have receivers.", property)
            isValid = false
        }
        return isValid
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: KSPLogger): Boolean {
        var isValid = true
        if (classDeclaration.classKind != ClassKind.CLASS) {
            data.error("Class bindings can only be 'class', was '${classDeclaration.classKind}'.", classDeclaration)
            isValid = false
        }
        if (!classDeclaration.isVisibleForInjection()) {
            data.error("Class bindings must be public or internal.", classDeclaration)
            isValid = false
        }
        if (classDeclaration.isAbstract()) {
            data.error("Class bindings must not be abstract.", classDeclaration)
            isValid = false
        }
        if (classDeclaration.isCompanionObject) {
            data.error("Class bindings are not allowed to be companion objects.", classDeclaration)
            isValid = false
        }
        when (val constructor = classDeclaration.primaryConstructor) {
            null -> {
                data.error("Class bindings must have a primary constructor.", classDeclaration)
                isValid = false
            }
            else -> {
                if (!constructor.isVisibleForInjection()) {
                    data.error("Class bindings constructor must be public or internal.", constructor)
                    isValid = false
                }

                for (parameter in constructor.parameters) {
                    if (parameter.hasDefault) {
                        data.warn("Default parameters of class bindings constructor will always be filled.", parameter)
                    }
                }
            }
        }
        return isValid
    }
}

private object BindingsSanityVerifier : KSDefaultVisitor<KSPLogger, Boolean>() {
    override fun defaultHandler(node: KSNode, data: KSPLogger): Boolean = false

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: KSPLogger): Boolean {
        var isValid = true
        for (parameter in function.parameters) {
            val tag = parameter.getTagOrNull()
            if (tag != null && tag.isEmpty() && parameter.name == null) {
                data.error("'value' on @Tag needs to be defined when parameter name is missing.", parameter)
                isValid = false
            }
        }
        return isValid
    }

    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: KSPLogger): Boolean = true

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: KSPLogger): Boolean {
        var isValid = true
        // this verifier should never be run before 'StandAloneBindingsVerifier' and a non-standalone bindings shouldn't
        // be able to be a class, so this should be safe to do unless something has gone wrong
        for (parameter in classDeclaration.primaryConstructor!!.parameters) {
            val tag = parameter.getTagOrNull()
            if (tag != null && tag.isEmpty() && parameter.name == null) {
                data.error("'value' on @Tag needs to be defined when parameter name is missing.", parameter)
                isValid = false
            }
        }
        return isValid
    }
}

private object ReturnTypeVisitor : KSDefaultVisitor<KSPLogger, KSType?>() {
    override fun defaultHandler(node: KSNode, data: KSPLogger): KSType? = null

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: KSPLogger): KSType? {
        val resolvedType = function.returnType?.resolve()

        if (resolvedType == null)
            data.warn("Could not resolve function type.", function)

        return resolvedType
    }

    override fun visitPropertyDeclaration(
        property: KSPropertyDeclaration,
        data: KSPLogger,
    ): KSType = property.type.resolve()

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: KSPLogger): KSType =
        classDeclaration.asStarProjectedType()
}