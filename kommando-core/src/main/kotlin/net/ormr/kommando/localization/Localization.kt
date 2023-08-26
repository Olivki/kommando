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
import net.ormr.kommando.Element
import net.ormr.kommando.ElementPath

public data class Localization(
    public val defaultLocale: Locale,
    public val messageBundle: MessageBundle,
    public val messageFinder: MessageFinder,
) {
    public fun getMessage(element: Element, path: ElementPath, key: String): Message =
        getMessageOrNull(element, path, key)
            ?: throw NoSuchElementException("No message found for '$path/$key'")

    public fun getMessageOrNull(element: Element, path: ElementPath, key: String): Message? =
        messageFinder.findMessage(messageBundle, element, path, key)
}