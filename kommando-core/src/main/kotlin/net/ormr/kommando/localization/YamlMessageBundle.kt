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

package net.ormr.kommando.localization

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.github.michaelbull.logging.InlineLogger
import dev.kord.common.Locale
import net.ormr.kommando.ComponentPath
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.forEach
import net.ormr.kommando.util.toPersistentHashMap

private class YamlMessageBundle(private val resource: LocalizedResource) : MessageBundle {
    private val defaultLocale get() = resource.defaultLocale
    private val objects = resource
        .asMap()
        .entries
        .filter { (_, resource) -> resource.exists() }
        .map { (locale, resource) ->
            logger.debug { "Reading file: ${resource.asString()}" }
            resource.inputStream().use { YamlObject(locale, mapper.readTree(it)) }
        }

    override fun getMessageOrNull(path: ComponentPath, key: String): Message? {
        val strings = objects
            .asSequence()
            .mapNotNull { obj -> obj.findString(path, key)?.let { obj.locale to it } }
            .toPersistentHashMap()
            .ifEmpty { return null }
            .toLocalizedStrings()
        return LocalizedMessage(defaultLocale, strings)
    }

    override fun isEmpty(): Boolean = resource.isEmpty()

    private companion object {
        private val logger = InlineLogger()
        private val mapper = YAMLMapper()
    }
}

private data class YamlObject(val locale: Locale, private val root: JsonNode) {
    fun findString(path: ComponentPath, key: String): String? {
        val node = walkToEndOf(path)?.get(key) ?: return null
        return node.textValue()
    }

    private fun walkToEndOf(path: ComponentPath): JsonNode? {
        var current = root
        path.forEach { component ->
            current = current[component] ?: return null
        }
        return current
    }
}

@KommandoDsl
public fun MessageBundleBuilder.yaml(resource: LocalizedResource) {
    require(resource.defaultLocale == defaultLocale) { "Resource default locale (${resource.defaultLocale}) != builder default locale ($defaultLocale)" }
    +YamlMessageBundle(resource)
}

@KommandoDsl
public fun MessageBundleBuilder.yamlFromClassPath(
    path: String,
    extension: String = "yaml",
    clz: Class<*> = javaClass,
) {
    yaml(LocalizedResource.fromClass(clz, path, extension, defaultLocale))
}

public fun loadYamlMessageBundle(resource: LocalizedResource): MessageBundle = YamlMessageBundle(resource)