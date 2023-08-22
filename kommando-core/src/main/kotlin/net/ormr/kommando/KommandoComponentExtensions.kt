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

import net.ormr.kommando.localization.LocaleBundle
import net.ormr.kommando.localization.Message

public inline val KommandoComponent.localeBundle: LocaleBundle
    get() = kommando.localeBundle

context(KommandoComponent)
public inline fun LocaleBundle.getMessage(key: String): Message = getMessage(componentPath, key)

context(KommandoComponent)
public inline fun LocaleBundle.getMessage(path: KommandoComponentPath, key: String): Message =
    getMessage(this@KommandoComponent, path, key)

context(KommandoComponent)
public inline fun LocaleBundle.getMessageOrNull(key: String): Message? =
    getMessageOrNull(componentPath, key)

context(KommandoComponent)
public inline fun LocaleBundle.getMessageOrNull(path: KommandoComponentPath, key: String): Message? =
    getMessageOrNull(this@KommandoComponent, path, key)