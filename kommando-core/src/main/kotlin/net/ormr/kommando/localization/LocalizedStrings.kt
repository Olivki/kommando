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

@file:Suppress("NOTHING_TO_INLINE")

package net.ormr.kommando.localization

import dev.kord.common.Locale
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentHashMap
import net.ormr.kommando.kord.asString
import net.ormr.kommando.util.toPersistentHashMap

/**
 * A mapping of [Locale]s to strings.
 */
@JvmInline
public value class LocalizedStrings internal constructor(private val delegate: PersistentMap<Locale, String>) {
    /**
     * Returns the string for the given [locale], or `null` if no such string exists.
     */
    public operator fun get(locale: Locale): String? = delegate[locale]

    /**
     * Returns `true` if a string is defined for the given [locale].
     */
    public operator fun contains(locale: Locale): Boolean = locale in delegate

    /**
     * Returns a new [LocalizedStrings] instance containing all entries from `this` instance and the [other] instance.
     *
     * If both instances contain the same locale, the value from [other] will be used.
     */
    public operator fun plus(other: LocalizedStrings): LocalizedStrings = LocalizedStrings(delegate + other.delegate)

    /**
     * Returns `true` if no mappings are defined.
     */
    public fun isEmpty(): Boolean = delegate.isEmpty()

    /**
     * Returns the backing [PersistentMap] instance.
     */
    public fun asMap(): PersistentMap<Locale, String> = delegate

    override fun toString(): String = delegate.entries.joinToString(prefix = "{", postfix = "}") { (locale, string) ->
        "${locale.asString()} -> '$string'"
    }
}

private val EMPTY_STRINGS: LocalizedStrings = LocalizedStrings(persistentHashMapOf())

/**
 * Returns an empty [LocalizedStrings] instance.
 */
public fun emptyLocalizedStrings(): LocalizedStrings = EMPTY_STRINGS

/**
 * Returns an empty [LocalizedStrings] instance.
 */
public fun localizedStringsOf(): LocalizedStrings = emptyLocalizedStrings()

/**
 * Returns a [LocalizedStrings] instance containing the given [mappings].
 */
public fun localizedStringsOf(vararg mappings: Pair<Locale, String>): LocalizedStrings =
    LocalizedStrings(mappings.toPersistentHashMap())

/**
 * Returns a [LocalizedStrings] instance containing the mappings in `this` map.
 */
public fun Map<Locale, String>.toLocalizedStrings(): LocalizedStrings = LocalizedStrings(toPersistentHashMap())

/**
 * Returns `true` if any mappings are defined.
 */
public inline fun LocalizedStrings.isNotEmpty(): Boolean = !isEmpty()

/**
 * Returns a new [MutableMap] containing the entries of `this` instance.
 */
public inline fun LocalizedStrings.toMutableMap(): MutableMap<Locale, String> = asMap().toMutableMap()

/**
 * Returns the string for the given [locale], or throws a [NoSuchElementException] if no such string exists.
 *
 * @throws [NoSuchElementException] if no string exists for the given [locale]
 */
public fun LocalizedStrings.getValue(locale: Locale): String =
    this[locale] ?: throw NoSuchElementException("No string found for locale ($locale)")