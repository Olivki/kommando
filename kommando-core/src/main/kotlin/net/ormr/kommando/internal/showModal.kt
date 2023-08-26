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

import dev.kord.core.behavior.interaction.ModalParentInteractionBehavior
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.behavior.interaction.response.PopupInteractionResponseBehavior
import net.ormr.kommando.Component
import net.ormr.kommando.modal.Modal
import net.ormr.kommando.modal.ModalResult
import net.ormr.kommando.modal.ModalStorage
import kotlin.time.Duration

internal typealias ModalResponseCallback = suspend (PopupInteractionResponseBehavior) -> Unit

context(Component)
@PublishedApi
internal suspend inline fun <Value> showModal0(
    modal: Modal<Value>,
    timeout: Duration?,
    onResponse: ModalResponseCallback,
    interaction: ModalParentInteractionBehavior,
): ModalResult<Value>? {
    val storage = kommando.modalStorage
    val response = showModal1(storage, modal, interaction)
    onResponse(response)
    return modal.modalResponse.await(timeout ?: storage.timeout)
}

@PublishedApi
internal suspend fun showModal1(
    storage: ModalStorage,
    modal: Modal<*>,
    interaction: ModalParentInteractionBehavior,
): PopupInteractionResponseBehavior {
    val components = modal.modalRegistry.asMap()
    require(components.isNotEmpty()) { "Modal (${modal::class.qualifiedName}) must have at least one component" }
    require(components.size <= 5) { "Modal (${modal::class.qualifiedName}) can only have up to 5 components" }
    storage.addModal(modal)
    return interaction.modal(modal.modalTitle, modal.modalId) {
        for ((_, component) in components) {
            actionRow {
                component.buildComponent()
            }
        }
    }
}