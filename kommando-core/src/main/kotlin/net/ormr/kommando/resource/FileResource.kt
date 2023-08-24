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

import java.io.IOException
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.pathString

/**
 * A [Path] backed [Resource].
 */
public data class FileResource(public val file: Path) : Resource {
    override val path: String
        get() = file.pathString

    override fun exists(): Boolean = file.exists()

    override fun inputStream(): InputStream = try {
        file.inputStream()
    } catch (e: IOException) {
        throw ResourceNotFoundException("Could not find resource at path '$path'", e)
    }

    override fun asString(): String = file.pathString
}

/**
 * Creates a new [FileResource] from `this`.
 */
public fun Path.asResource(): FileResource = FileResource(this)