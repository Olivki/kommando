/*
 * MIT License
 *
 * Copyright (c) 2022 Oliver Berg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.ormr.kommando.modals

import dev.kord.core.behavior.interaction.ModalParentInteractionBehavior
import dev.kord.core.behavior.interaction.modal
import dev.kord.core.entity.component.TextInputComponent
import dev.kord.core.entity.interaction.ModalSubmitInteraction
import dev.kord.core.event.interaction.ModalSubmitInteractionCreateEvent
import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoAware
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.internal.createUuidString
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Represents a [Popup Modal](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-modal).
 *
 * @property title The title of the popup modal.
 * @property customId The id of the modal, unless manually created this will most likely be an automatically generated
 * UUID.
 * @property components The components belonging to this modal, there should always be at least one component, and at
 * max 5.
 * @property executor The function that will be invoked once the modal has been submitted by a user.
 */
public data class Modal(
    public val title: String,
    public val customId: String,
    public val components: List<ModalComponent>,
    public val executor: ModalExecutor,
)

/**
 * Represents data about the submitted modal interaction.
 */
public data class ModalData(
    override val kommando: Kommando,
    public val event: ModalSubmitInteractionCreateEvent,
) : KommandoAware {
    public val interaction: ModalSubmitInteraction
        get() = event.interaction

    /**
     * The [TextInputComponent]s of the modal.
     *
     * @see [ModalSubmitInteraction.textInputs]
     */
    public val textInputs: Map<String, TextInputComponent> = interaction.textInputs

    /**
     * Returns the [value][TextInputComponent.value] of the text input with the given [customId], or throws a
     * [NoSuchElementException] if none is found.
     *
     * @throws NoSuchElementException if [customId] does not match any text inputs
     */
    public fun getTextInputValue(customId: String): String? = textInputs.getValue(customId).value
}

@KommandoDsl
public class ModalBuilder @PublishedApi internal constructor(
    private val title: String,
    private val customId: String,
) {
    private val components = mutableListOf<ModalComponent>()
    private var executor: ModalExecutor? = null

    @PublishedApi
    internal fun addComponent(component: ModalComponent) {
        components += component
    }

    /**
     * Sets the [executor][Modal.executor] of the modal to the given [executor].
     *
     * There can only exist one `execute` block per modal.
     *
     * @throws IllegalArgumentException if there already exists an `execute` block
     */
    @KommandoDsl
    public fun execute(executor: ModalExecutor) {
        require(this.executor == null) { "Only one 'execute' block can exist per modal." }
        this.executor = executor
    }

    private fun validate() {
        if (components.isEmpty()) error("Modals need at least one component.")
        if (components.size > 5) error("Modals can't have more than five components.")
    }

    @PublishedApi
    internal fun build(): Modal {
        validate()
        return Modal(
            title = title,
            customId = customId,
            components = components.toList(),
            executor = executor ?: error("Missing required 'executor' block.")
        )
    }
}

/**
 * Responds to the interaction with a [popup modal][Modal] configured by [builder].
 */
// TODO: we can't make this 'inline' because it generates faulty bytecode and crashes at runtime
//context(KommandoAware)
@KommandoDsl
public suspend fun ModalParentInteractionBehavior.openModal(
    kommando: Kommando,
    title: String,
    builder: ModalBuilder.() -> Unit,
): ModalResponse {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    val customId = createUuidString()
    val modal = ModalBuilder(title, customId).apply(builder).build()
    kommando.modalStorage += modal
    val response = modal(title, customId) {
        for (component in modal.components) {
            actionRow {
                with(component) { buildComponent() }
            }
        }
    }

    return ModalResponse(modal, response)
}