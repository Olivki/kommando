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
import net.ormr.kommando.KommandoBuilder
import net.ormr.kommando.KommandoDsl
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class LocalizationBuilder @PublishedApi internal constructor(
    @PublishedApi
    internal val defaultLocale: Locale = Locale.ENGLISH_UNITED_STATES,
) {
    @PublishedApi
    internal var messageBundle: MessageBundle = emptyMessageBundle()

    private var messageFinder: MessageFinder = DefaultMessageFinder

    @KommandoDsl
    public fun messageFinder(finder: MessageFinder) {
        this.messageFinder = finder
    }

    @PublishedApi
    internal fun build(): Localization = Localization(
        defaultLocale = defaultLocale,
        messageBundle = messageBundle,
        messageFinder = messageFinder,
    )
}

private data object DefaultMessageFinder : MessageFinder {
    override fun findMessage(bundle: MessageBundle, element: Element, path: ElementPath, key: String): Message? =
        bundle.getMessageOrNull(path, key)
}

@KommandoDsl
public inline fun KommandoBuilder.localization(
    defaultLocale: Locale = Locale.ENGLISH_UNITED_STATES,
    builder: LocalizationBuilder.() -> Unit,
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    localization = LocalizationBuilder(defaultLocale).apply(builder).build()
}