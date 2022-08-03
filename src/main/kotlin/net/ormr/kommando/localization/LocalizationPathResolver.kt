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

import net.ormr.kommando.KommandoComponent

// TODO: better name
public typealias LocalizationPathResolver = (component: KommandoComponent, container: LocalizationContainer, extra: String?) -> LocalizedString?

internal val DEFAULT_PATH_RESOLVER: LocalizationPathResolver =
    { component, container, extra -> container.getStringOrNull(component.componentPath.format() + (extra ?: "")) }