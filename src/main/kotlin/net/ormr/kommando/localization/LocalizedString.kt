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

public data class LocalizedString(
    public val entries: LocalizedEntries,
    public val defaultLocale: Locale,
) {
    init {
        require(defaultLocale in entries) { "'entries' is missing defaultLocale" }
    }

    public val defaultString: String by lazy { entries.getValue(defaultLocale) }

    public fun getStringOrDefault(locale: Locale): String = entries[locale] ?: defaultString

    public fun getStringOrNull(locale: Locale): String? = entries[locale]

    public fun addEntries(entries: LocalizedEntries): LocalizedString = copy(entries = entries + entries)

    public fun toMutableMap(): MutableMap<Locale, String> = entries.toMutableMap()
}

context(KommandoComponent)
        public fun LocalizedString(entries: LocalizedEntries): LocalizedString =
    LocalizedString(entries, localization.defaultLocale)

context(KommandoComponent)
        public fun LocalizedString(value: String): LocalizedString {
    val locale = localization.defaultLocale
    return LocalizedString(mapOf(locale to value), locale)
}