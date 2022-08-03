/*
 * Copyright 2022 Oliver Berg
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

package net.ormr.kommando.commands

import net.ormr.kommando.commands.arguments.Argument

internal object CommandArgumentCache {
    private val cachedCommands = hashMapOf<CommandCacheKey, Entry>()

    fun entry(key: CommandCacheKey) {
        if (!isCached(key)) cachedCommands[key] = Entry()
    }

    fun hydrate(key: CommandCacheKey) {
        getEntry(key).isHydrated = true
    }

    fun isCached(key: CommandCacheKey): Boolean = key in cachedCommands

    fun addArgument(key: CommandCacheKey, argument: Argument<*, *>) {
        val entry = getEntry(key)

        if (entry.isHydrated) {
            throw UnsupportedOperationException("Cache for '$key' is hydrated, no more arguments can be cached for it")
        }

        entry.arguments[argument.name] = argument
    }

    fun getArguments(key: CommandCacheKey): Map<String, Argument<*, *>> = getEntry(key).arguments

    fun getArgument(key: CommandCacheKey, name: String): Argument<*, *> = getEntry(key).arguments.getValue(name)

    fun isHydrated(key: CommandCacheKey): Boolean = cachedCommands[key]?.isHydrated ?: false

    private fun getEntry(key: CommandCacheKey): Entry =
        cachedCommands[key] ?: throw NoSuchElementException("$key is not a cached a command")

    private class Entry {
        val arguments: MutableMap<String, Argument<*, *>> = linkedMapOf()
        var isHydrated: Boolean = false
    }
}

internal typealias CommandCacheKey = Class<out Command<*>>