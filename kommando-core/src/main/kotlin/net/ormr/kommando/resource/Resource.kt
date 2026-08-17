/*
 * Copyright 2023-2026 Oliver Berg
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
 * A resource that has a `path` and can be loaded via [inputStream].
 */
public interface Resource {
    /**
     * The path to the resource.
     */
    public val path: String

    /**
     * Returns `true` if the resource exists.
     */
    public fun exists(): Boolean

    /**
     * Returns a new [InputStream] for the resource.
     *
     * @throws [ResourceNotFoundException] if an `InputStream` could not be opened for some reason
     */
    public fun inputStream(): InputStream

    public fun asString(): String
}