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

public class LocalizationResolver private constructor(public val locale: Locale) {
    public companion object {
        private val cache = Locale.ALL.associateWithTo(hashMapOf()) { LocalizationResolver(it) }

        public fun forLocale(locale: Locale): LocalizationResolver =
            cache.getOrPut(locale) { LocalizationResolver(locale) }
    }

    public operator fun get(string: LocalizedString): String = resolveOrDefault(string)

    public fun resolveOrDefault(string: LocalizedString): String = string.getStringOrDefault(locale)

    public fun resolveOrNull(string: LocalizedString): String? = string.getStringOrNull(locale)

    override fun toString(): String = "LocalizationResolver(locale=$locale)"

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is LocalizationResolver -> false
        locale != other.locale -> false
        else -> true
    }

    override fun hashCode(): Int = locale.hashCode()
}