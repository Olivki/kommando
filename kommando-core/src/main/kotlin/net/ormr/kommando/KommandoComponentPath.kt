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

package net.ormr.kommando

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus

internal const val PATH_SEP_CHAR = '/'
internal const val PATH_SEP_STR = "/"

@JvmInline
public value class KommandoComponentPath internal constructor(private val parts: PersistentList<String>) {
    init {
        require(parts.isNotEmpty()) { "Path must not be empty" }
        require(parts.all { it.isNotEmpty() }) { "Path must not contain empty parts" }
        require(parts.all { PATH_SEP_CHAR !in it }) { "Path must not contain '$PATH_SEP_CHAR'" }
    }

    public operator fun div(other: KommandoComponentPath): KommandoComponentPath =
        KommandoComponentPath(parts + other.parts)

    public operator fun div(other: String): KommandoComponentPath = KommandoComponentPath(parts + other)

    public operator fun get(index: Int): String = parts[index]

    public fun asString(): String = parts.joinToString(PATH_SEP_STR)

    public fun asList(): List<String> = parts

    public operator fun iterator(): Iterator<String> = parts.iterator()
}

public inline fun KommandoComponentPath.forEach(action: (String) -> Unit): Unit = asList().forEach(action)

public fun KommandoComponentPath(first: String, vararg rest: String): KommandoComponentPath = KommandoComponentPath(
    parts = persistentListOf<String>().mutate {
        it.add(first)
        it.addAll(rest)
    }
)