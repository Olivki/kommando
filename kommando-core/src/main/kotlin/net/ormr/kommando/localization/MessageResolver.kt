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

// TODO: better name
public class MessageResolver private constructor(public val locale: Locale) {
    /**
     * Returns the string for the given [message], or the [defaultString][Message.defaultString] if no such string
     * exists.
     *
     * The `locale` used is the one that this [MessageResolver] was created with.
     *
     * @see [getStringOrNull]
     */
    public fun getStringOrDefault(message: Message): String = message.getStringOrDefault(locale)

    /**
     * Returns the string for the given [message], or `null` if no such string exists, or
     * [defaultString][Message.defaultString] if `message` is a [BasicMessage].
     *
     * The `locale` used is the one that this [MessageResolver] was created with.
     *
     * @see [getStringOrDefault]
     */
    public fun getStringOrNull(message: Message): String? = when (message) {
        is BasicMessage -> message.defaultString
        is LocalizedMessage -> message.getStringOrNull(locale)
    }

    override fun toString(): String = "MessageResolver(locale=${locale.asString()})"

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is MessageResolver -> false
        locale != other.locale -> false
        else -> true
    }

    override fun hashCode(): Int = locale.hashCode()

    public companion object {
        private val KNOWN_LOCALES = Locale.ALL.associateWithTo(hashMapOf(), ::MessageResolver)

        public fun of(locale: Locale): MessageResolver = KNOWN_LOCALES[locale] ?: MessageResolver(locale)
    }
}

/**
 * Returns the string for the given [message], or the [defaultString][Message.defaultString] if no such string
 * exists.
 *
 * The `locale` used is the one that this [MessageResolver] was created with.
 */
public inline operator fun MessageResolver.get(message: Message): String = getStringOrDefault(message)