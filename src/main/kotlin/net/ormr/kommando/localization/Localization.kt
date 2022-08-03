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

package net.ormr.kommando.localization

import dev.kord.common.Locale
import net.ormr.kommando.KommandoComponent

public data class Localization(
    public val defaultLocale: Locale,
    public val container: LocalizationContainer,
    public val pathResolver: LocalizationPathResolver,
) {
    public val defaultResolver: LocalizationResolver = LocalizationResolver.forLocale(defaultLocale)

    public fun resolve(component: KommandoComponent, extra: String? = null): LocalizedString =
        resolveOrNull(component, extra)
            ?: throw NoSuchElementException("Could not find localization string for path: ${component.componentPath}${extra?.let { "/$it" } ?: ""}")

    public fun resolveOrNull(component: KommandoComponent, extra: String? = null): LocalizedString? =
        pathResolver(component, container, extra)

    /*public operator fun get(path: String): LocalizedString = container[path]

    public operator fun get(locale: Locale, path: String): String = container[path].getStringOrDefault(locale)

    public operator fun contains(path: String): Boolean = path in container*/
}

public const val LOCALIZATION_PATH_SEPARATOR: Char = '/'