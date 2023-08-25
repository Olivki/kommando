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
import net.ormr.kommando.kord.asString

/**
 * A message that may contain multiple locale mappings.
 *
 * @see [BasicMessage]
 * @see [LocalizedMessage]
 */
public sealed interface Message {
    /**
     * The default string for this message.
     *
     * This is used as a fallback if no localization is found for the given locale.
     */
    public val defaultString: String

    /**
     * Returns the string for the given [locale], or the [defaultString] if no such string exists.
     */
    public fun getStringOrDefault(locale: Locale): String

    /**
     * Returns `true` if this message contains a string for the given [locale].
     */
    public operator fun contains(locale: Locale): Boolean
}

/**
 * Returns a [MutableMap] containing all the locale mappings for this message, or `null` if this message has no locale
 * mappings.
 */
public fun Message.toMutableMapOrNull(): MutableMap<Locale, String>? = when (this) {
    is BasicMessage -> null
    is LocalizedMessage -> strings.toMutableMap()
}

public inline fun Message.map(mapper: (Locale?, String) -> String): Message = when (this) {
    is BasicMessage -> mapper(null, defaultString).let(::BasicMessage)
    is LocalizedMessage -> copy(
        strings = strings
            .asMap()
            .mapValues { (locale, string) -> mapper(locale, string) }
            .toLocalizedStrings()
    )
}

public inline fun Message.forEach(action: (Locale?, String) -> Unit): Unit = when (this) {
    is BasicMessage -> action(null, defaultString)
    is LocalizedMessage -> strings.asMap().forEach { (locale, string) -> action(locale, string) }
}

public inline fun Message.all(predicate: (Locale?, String) -> Boolean): Boolean = when (this) {
    is BasicMessage -> predicate(null, defaultString)
    is LocalizedMessage -> strings.asMap().all { (locale, string) -> predicate(locale, string) }
}

/**
 * A message that has no locale mappings.
 *
 * It's only value is the [defaultString].
 *
 * @property [defaultString] The only string for this message.
 */
public data class BasicMessage(override val defaultString: String) : Message {
    /**
     * Always returns the [defaultString], since this message has no locale mappings.
     */
    override fun getStringOrDefault(locale: Locale): String = defaultString

    /**
     * Returns `false` since this message has no locale mappings.
     */
    override fun contains(locale: Locale): Boolean = false

    override fun toString(): String = "BasicMessage(defaultString='$defaultString')"
}

/**
 * A message that has one or more locale mappings.
 *
 * @property [defaultLocale] The default locale for this message.
 * @property [strings] The locale mappings for this message.
 */
public data class LocalizedMessage(val defaultLocale: Locale, val strings: LocalizedStrings) : Message {
    init {
        require(defaultLocale in strings) { "defaultLocale (${defaultLocale.asString()}) missing from $strings" }
    }

    override val defaultString: String = strings.getValue(defaultLocale)

    /**
     * Returns the string for the given [locale], or the [defaultString] if no such string exists.
     */
    override fun getStringOrDefault(locale: Locale): String = strings[locale] ?: defaultString

    /**
     * Returns the string for the given [locale], or `null` if no such string exists.
     */
    public fun getStringOrNull(locale: Locale): String? = strings[locale]

    override fun contains(locale: Locale): Boolean = locale in strings
    override fun toString(): String =
        "LocalizedMessage(defaultLocale=${defaultLocale.asString()}, defaultString='$defaultString', strings=$strings)"
}

/**
 * Returns a new [LocalizedMessage] instance containing all [strings][LocalizedMessage.strings] from `this` instance and
 * the [other] instance.
 */
public inline operator fun LocalizedMessage.plus(other: LocalizedMessage): LocalizedMessage =
    copy(strings = strings + other.strings)