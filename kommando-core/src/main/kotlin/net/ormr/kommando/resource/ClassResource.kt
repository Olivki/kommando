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

package net.ormr.kommando.resource

import java.io.InputStream

/**
 * A resource that is loaded from the classpath.
 *
 * @property [clz] The class to load the resource from.
 */
public data class ClassResource(override val path: String, val clz: Class<*>) : Resource {
    override fun exists(): Boolean = clz.getResource(path) != null

    override fun inputStream(): InputStream =
        clz.getResourceAsStream(path)
            ?: throw ResourceNotFoundException("Could not open resource stream for '$path' @ $clz")

    override fun asString(): String = "jar:$path"
}

/**
 * Creates a new [ClassResource] from the given [path] and `this`.
 */
public fun Class<*>.getClassResource(path: String): ClassResource = ClassResource(path, this)