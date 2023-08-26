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
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import dev.kord.common.Locale
import net.ormr.kommando.ElementPath
import net.ormr.kommando.forEach
import net.ormr.kommando.kord.asString
import net.ormr.kommando.util.toPersistentHashMap

/**
 * Helper class for [MessageBundle] implementations that delegate to Jackson.
 */
internal class JacksonMessageBundleHelper(private val mapper: ObjectMapper, resource: LocalizedResource) {
    private val nodes = resource
        .asMap()
        .entries
        .filter { (_, resource) -> resource.exists() }
        .map { (locale, resource) ->
            logger.debug { "Reading locale file (${locale.asString()}): ${resource.asString()}" }
            resource.inputStream().use { LocalizedNode(locale, mapper.readTree(it)) }
        }

    fun findMessage(defaultLocale: Locale, path: ElementPath, key: String): Message? {
        val strings = nodes
            .asSequence()
            .mapNotNull { obj -> obj.findString(path, key)?.let { obj.locale to it } }
            .toPersistentHashMap()
            .ifEmpty { return null }
            .toLocalizedStrings()
        return LocalizedMessage(defaultLocale, strings)
    }

    private data class LocalizedNode(val locale: Locale, private val root: JsonNode) {
        fun findString(path: ElementPath, key: String): String? {
            val node = walkToEndOf(path)?.get(key) ?: return null
            return node.textValue()
        }

        private fun walkToEndOf(path: ElementPath): JsonNode? {
            var current = root
            path.forEach { component ->
                current = current[component] ?: return null
            }
            return current
        }
    }

    private companion object {
        private val logger = InlineLogger()
    }
}