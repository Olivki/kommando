/*
 * Copyright 2022 Oliver Berg
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

@file:Suppress("MemberVisibilityCanBePrivate")

package net.ormr.kommando.localization

import dev.kord.common.Locale
import net.ormr.kommando.KommandoBuilder
import net.ormr.kommando.KommandoDsl
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KommandoDsl
public class LocalizationBuilder @PublishedApi internal constructor(origin: Localization) {
    public var defaultLocale: Locale = origin.defaultLocale

    public var container: LocalizationContainer = origin.container

    public var pathResolver: LocalizationPathResolver = origin.pathResolver

    @PublishedApi
    internal fun build(): Localization = Localization(
        defaultLocale = defaultLocale,
        container = container,
        pathResolver = pathResolver,
    )
}

context(KommandoBuilder)
        public inline fun localization(builder: LocalizationBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    localization = LocalizationBuilder(localization).apply(builder).build()
}