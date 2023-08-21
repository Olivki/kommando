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
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.plus

@JvmInline
public value class LocalizedStrings internal constructor(private val entries: PersistentMap<Locale, String>) {
    public operator fun get(locale: Locale): String? = entries[locale]

    public operator fun contains(locale: Locale): Boolean = locale in entries

    public operator fun plus(other: LocalizedStrings): LocalizedStrings = LocalizedStrings(entries + other.entries)

    public fun isEmpty(): Boolean = entries.isEmpty()

    public fun isNotEmpty(): Boolean = entries.isNotEmpty()

    public fun toMutableMap(): MutableMap<Locale, String> = entries.toMutableMap()
}

public fun LocalizedStrings.getValue(locale: Locale): String =
    this[locale] ?: throw NoSuchElementException("No string found for locale '$locale'")