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
import dev.kord.core.entity.component.Component as KordComponent

public sealed interface ModalComponentType<Value>
        where Value : Any {
    public fun getValue(source: ModalSubmitInteraction, id: String): Value = getValueOrNull(source, id)
        ?: throw IllegalArgumentException("No component with name '$id' of type '$this' was found") // TODO: custom error?

    public fun getValueOrNull(source: ModalSubmitInteraction, id: String): Value?


    // discord only support text input at the moment
    public data object TextInput : ModalComponentType<String> {
        override fun getValueOrNull(source: ModalSubmitInteraction, id: String): String? = source.textInputs[id]?.value
    }
}

private inline fun <reified T : KordComponent> ModalSubmitInteraction.componentsOfType(): Sequence<T> =
    actionRows
        .asSequence()
        .flatMap { it.components }
        .filterIsInstance<T>()