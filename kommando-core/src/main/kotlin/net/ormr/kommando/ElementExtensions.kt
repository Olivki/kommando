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

@file:Suppress("FunctionName", "NOTHING_TO_INLINE")

package net.ormr.kommando

import net.ormr.kommando.localization.Localization
import net.ormr.kommando.localization.Message

public inline val Element.localization: Localization
    get() = kommando.localization

public inline val DescribableElement.defaultComponentDescription: String
    get() = elementDescription.defaultString

/**
 * Returns the [fullElementPath][ComposableElement.fullElementPath] of this element, or the
 * [elementPath][Element.elementPath] if this element is not a [ComposableElement].
 */
// TODO: better name
public fun Element.findFullElementPath(): ElementPath = when (this) {
    is ComposableElement -> fullElementPath
    else -> elementPath
}

context(Element)
public inline fun Localization.getMessage(key: String): Message = getMessage(findFullElementPath(), key)

context(Element)
public inline fun Localization.getMessage(path: ElementPath, key: String): Message =
    getMessage(this@Element, path, key)

context(Element)
public inline fun Localization.getMessageOrNull(key: String): Message? =
    getMessageOrNull(findFullElementPath(), key)

context(Element)
public inline fun Localization.getMessageOrNull(path: ElementPath, key: String): Message? =
    getMessageOrNull(this@Element, path, key)