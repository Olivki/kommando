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
import net.ormr.kommando.KommandoDsl
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KommandoDsl
public class MessageBundleBuilder @PublishedApi internal constructor(public val defaultLocale: Locale) {
    private val bundles = mutableListOf<MessageBundle>()
    
    public operator fun MessageBundle.unaryPlus() {
        bundles += this
    }

    @PublishedApi
    internal fun build(): MessageBundle = when {
        bundles.isEmpty() -> emptyMessageBundle()
        else -> bundles.reduce { acc, bundle -> acc + bundle }
    }
}

@KommandoDsl
public inline fun LocalizationBuilder.messageBundle(builder: MessageBundleBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    messageBundle = MessageBundleBuilder(defaultLocale).apply(builder).build()
}