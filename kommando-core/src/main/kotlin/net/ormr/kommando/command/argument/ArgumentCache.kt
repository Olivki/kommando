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

import net.ormr.kommando.localization.Message
import java.util.concurrent.ConcurrentHashMap

internal class ArgumentCache {
    private val cache: MutableMap<Key, Data> = ConcurrentHashMap()

    fun getOrPut(key: Key, defaultValue: (Key) -> Data): Data = cache.computeIfAbsent(key, defaultValue)

    operator fun contains(key: Key): Boolean = key in cache

    data class Key(val name: String, val commandClass: Class<*>)

    data class Data(val key: String, val name: Message, val description: Message)
}