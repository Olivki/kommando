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

package net.ormr.kommando.modal.component

import dev.kord.core.entity.interaction.ModalSubmitInteraction
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.rest.builder.component.ActionRowBuilder
import net.ormr.kommando.modal.Modal

public interface ModalComponent<Value, CompValue, out CompType>
        where CompValue : Any,
              CompType : ModalComponentType<CompValue> {
    public val type: CompType

    /**
     * The unique identifier of the component.
     *
     * This only needs to be unique to the [Modal] that it belongs to.
     */
    public val customId: String

    /**
     * Whether the component is disabled or not.
     *
     * A disabled component can't be interacted with and is also rendered differently.
     */
    public val isDisabled: Boolean

    public suspend fun getValue(
        source: ModalSubmitInteraction,
        event: ModalSubmitInteractionCreateEvent,
    ): Value = convertComponentValue(type.getValue(source, customId))

    public suspend fun getValueOrNull(
        source: ModalSubmitInteraction,
        event: ModalSubmitInteractionCreateEvent,
    ): Value? = convertNullableComponentValue(type.getValueOrNull(source, customId))

    public fun convertComponentValue(value: CompValue): Value

    public fun convertNullableComponentValue(value: CompValue?): Value?

    context(ActionRowBuilder)
    public fun buildComponent()
}