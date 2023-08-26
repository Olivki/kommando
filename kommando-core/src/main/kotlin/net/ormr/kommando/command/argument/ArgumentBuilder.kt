/*
 * Copyright 2023 Oliver Berg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ormr.kommando.command.argument

import com.github.michaelbull.logging.InlineLogger
import net.ormr.kommando.command.CustomizableCommand
import net.ormr.kommando.internal.findRegistry
import net.ormr.kommando.kord.asString
import net.ormr.kommando.localization
import net.ormr.kommando.localization.BasicMessage
import net.ormr.kommando.localization.Message
import net.ormr.kommando.localization.forEach
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private typealias ArgumentProperty<Value> = ReadOnlyProperty<CustomizableCommand<*>, Value>

public class ArgumentBuilder<Value, Arg>(
    public val name: String?,
    public val description: String,
    private val argumentFactory: ArgumentFactory<Value, Arg>,
) : PropertyDelegateProvider<CustomizableCommand<*>, ArgumentProperty<Value>>
        where Arg : Argument<Value, *, *> {
    public fun createArgument(key: String, name: Message, description: Message): Arg =
        argumentFactory.create(key, name, description)

    override fun provideDelegate(thisRef: CustomizableCommand<*>, property: KProperty<*>): ArgumentProperty<Value> {
        // TODO: verify that argument name is valid
        //       https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-naming
        val commands = thisRef.kommando.commands
        val cache = commands.argumentCache
        val nameConverter = commands.nameConverters.forArgument
        val cacheKey = ArgumentCache.Key(property.name, thisRef::class.java)
        val isFirstRun = cacheKey !in cache
        val (key, name, description) = cache.getOrPut(cacheKey) {
            val key = nameConverter.convert(property.name)
            val bundle = thisRef.localization
            val selfPath = thisRef.elementPath
            val firstPath = cache.pathStack.firstOrNull()?.let { it / selfPath } ?: selfPath
            val argPath = firstPath / "arguments" / key
            val name = bundle.getMessageOrNull(thisRef, argPath, "name") ?: BasicMessage(this.name ?: key)
            name.forEach { locale, s ->
                require(s.isNotBlank()) { "Argument name must not be blank (${locale?.asString()})" }
                if (s.length !in 1..32) {
                    // TODO: custom exception
                    throw IllegalArgumentException("Argument name ($s) length (${s.length}) !in 1..32 (${locale?.asString()})")
                }
            }
            val description = bundle.getMessageOrNull(thisRef, argPath, "description") ?: BasicMessage(this.description)
            ArgumentCache.Data(key, name, description)
        }
        val argument = argumentFactory.create(key, name, description)
        if (isFirstRun) {
            logger.info { "Built argument: $argument" }
        }
        thisRef.findRegistry().registerArgument(key, argument)
        return ArgumentPropertyDelegate(argument)
    }

    private companion object {
        private val logger = InlineLogger()
    }
}