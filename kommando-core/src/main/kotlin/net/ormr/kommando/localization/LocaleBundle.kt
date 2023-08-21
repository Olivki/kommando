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

import dev.kord.common.Locale
import net.ormr.kommando.KommandoComponent

public data class LocaleBundle(
    public val defaultLocale: Locale,
    private val messageBundle: MessageBundle,
    private val finder: MessageFinder,
) {
    public fun getMessage(component: KommandoComponent, key: String): Message =
        getMessageOrNull(component, key)
            ?: throw NoSuchElementException("No message found for '${component.componentPath.asString()}/$key'")

    public fun getMessageOrNull(component: KommandoComponent, key: String): Message? =
        finder.findMessage(messageBundle, component, key)
}