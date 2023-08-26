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

package net.ormr.kommando.internal

import dev.kord.core.entity.interaction.ModalSubmitInteraction
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import dev.kord.core.on
import net.ormr.kommando.Kommando
import net.ormr.kommando.modal.Modal
import net.ormr.kommando.modal.ModalContext
import net.ormr.kommando.modal.ModalResult

context(Kommando)
@Suppress("UNCHECKED_CAST")
internal suspend fun handleModals() {
    kord.on<ModalSubmitInteractionCreateEvent> {
        val id = interaction.modalId
        val modal = (modalStorage.get(id) ?: return@on) as Modal<Any>
        val context = ModalContextImpl(interaction)
        populateModal(modal, interaction, this)
        val value = with(context) { modal.execute() }
        modal.modalResponse.emit(ModalResult(value, interaction))
        modalStorage.removeModal(modal)
    }
}

private suspend fun populateModal(
    modal: Modal<*>,
    interaction: ModalSubmitInteraction,
    event: ModalSubmitInteractionCreateEvent,
) {
    val registry = modal.modalRegistry
    val components = registry
        .asMap()
        .mapValuesTo(hashMapOf()) { (_, component) -> component.getValue(interaction, event) }
    registry.populate(components)
}

private data class ModalContextImpl(override val interaction: ModalSubmitInteraction) : ModalContext