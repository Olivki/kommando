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

package net.ormr.kommando.component

import dev.kord.rest.builder.component.ActionRowBuilder

public sealed interface VisibleComponent<Self> : Component
        where Self : VisibleComponent<Self> {
    /**
     * The unique identifier of the component.
     *
     * This is used to identify the component for Kommando systems, and should not be used for anything else. It does
     * not need to be unique across all components, but it does need to be unique across all components of the same
     * [collection][ComponentGroup].
     */
    public val id: String

    /**
     * Whether the component is disabled or not.
     *
     * A disabled component can't be interacted with and is also rendered differently.
     */
    public val isDisabled: Boolean

    /**
     * Returns a copy of the component with the [isDisabled][VisibleComponent.isDisabled] property set to
     * [isDisabled].
     */
    public fun withDisabled(isDisabled: Boolean): Self

    context(ActionRowBuilder)
    public fun buildComponent()
}