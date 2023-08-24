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
import net.ormr.kommando.kord.asString
import net.ormr.kommando.resource.Resource
import net.ormr.kommando.resource.getClassResource
import net.ormr.kommando.util.buildPersistentHashMap

public data class LocalizedResource(
    public val defaultLocale: Locale,
    public val resources: PersistentMap<Locale, Resource>,
) {
    init {
        require(resources.isNotEmpty()) { "LocalizedResource should not be empty" }
    }

    public operator fun get(locale: Locale): Resource? = resources[locale]

    public fun contains(locale: Locale): Boolean = locale in resources

    public fun isEmpty(): Boolean = resources.isEmpty()

    public fun asMap(): Map<Locale, Resource> = resources

    public companion object {
        // TODO: explain that this does not check if the resource actually exists
        public fun fromClass(
            clz: Class<*>,
            path: String,
            extension: String,
            defaultLocale: Locale,
            locales: List<Locale> = Locale.ALL,
        ): LocalizedResource = LocalizedResource(
            defaultLocale = defaultLocale,
            resources = buildPersistentHashMap {
                for (locale in locales) {
                    put(locale, clz.getClassResource("$path.${locale.asString()}.$extension"))
                }
            },
        )
    }
}

public operator fun LocalizedResource.plus(other: LocalizedResource): LocalizedResource =
    copy(resources = resources + other.resources)