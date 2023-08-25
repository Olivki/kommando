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

public inline val Component.localization: Localization
    get() = kommando.localization

public inline val DescribableComponent.defaultComponentDescription: String
    get() = componentDescription.defaultString

/**
 * Returns the [fullComponentPath][ComposableComponent.fullComponentPath] of this component, or the
 * [componentPath][Component.componentPath] if this component is not a [ComposableComponent].
 */
// TODO: better name
public fun Component.findFullComponentPath(): ComponentPath = when (this) {
    is ComposableComponent -> fullComponentPath
    else -> componentPath
}

context(Component)
public inline fun Localization.getMessage(key: String): Message = getMessage(findFullComponentPath(), key)

context(Component)
public inline fun Localization.getMessage(path: ComponentPath, key: String): Message =
    getMessage(this@Component, path, key)

context(Component)
public inline fun Localization.getMessageOrNull(key: String): Message? =
    getMessageOrNull(findFullComponentPath(), key)

context(Component)
public inline fun Localization.getMessageOrNull(path: ComponentPath, key: String): Message? =
    getMessageOrNull(this@Component, path, key)