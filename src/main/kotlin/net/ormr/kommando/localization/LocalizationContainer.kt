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

// TODO: better name
public interface LocalizationContainer {
    public companion object {
        public fun empty(): LocalizationContainer = EmptyLocalizationContainer
    }

    public fun getString(key: String): LocalizedString

    public fun getStringOrNull(key: String): LocalizedString?

    public fun hasString(key: String): Boolean
}

public operator fun LocalizationContainer.get(key: String): LocalizedString = getString(key)

public operator fun LocalizationContainer.contains(key: String): Boolean = hasString(key)