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

package net.ormr.kommando.components

import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.followup.edit
import dev.kord.core.behavior.interaction.response.EphemeralMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.PublicMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.edit
import dev.kord.core.entity.Message
import dev.kord.core.entity.component.ActionRowComponent
import dev.kord.core.entity.component.UnknownComponent
import dev.kord.core.entity.interaction.followup.EphemeralFollowupMessage
import dev.kord.core.entity.interaction.followup.PublicFollowupMessage
import dev.kord.rest.builder.message.create.MessageCreateBuilder
import dev.kord.rest.builder.message.create.actionRow
import dev.kord.rest.builder.message.modify.MessageModifyBuilder
import dev.kord.rest.builder.message.modify.actionRow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.storage.minusAssign
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration

private const val MAX_ROW_WIDTH = 5
private const val MAX_ROWS = 5

public data class ComponentGroup(public val components: List<Component>) {
    public val executableComponents: Map<String, ExecutableComponent<*, *>> = components
        .filterIsInstance<ExecutableComponent<*, *>>()
        .associateBy { it.customId }

    public fun getExecutableComponent(id: String): ExecutableComponent<*, *> =
        executableComponents[id] ?: throw NoSuchElementException("No component with id '$id' found.")

    public val componentRows: ComponentRows by lazy {
        val rows = MutableList(MAX_ROWS) { ArrayList<Component>(MAX_ROW_WIDTH) }
        var currentWidth = 0
        var currentRow = 0
        for (component in components) {
            currentWidth += component.width
            if (currentWidth > MAX_ROW_WIDTH) {
                currentRow++
                currentWidth = component.width
            }
            if (currentRow >= MAX_ROWS) error("Max rows ($MAX_ROWS) exceeded with component $component.")
            rows[currentRow].add(component)
        }
        rows
    }
}

@KommandoDsl
public class ComponentGroupBuilder @PublishedApi internal constructor() {
    private val components = mutableListOf<Component>()

    /**
     * Adds all the components from [other] to this component group.
     */
    public fun from(other: ComponentGroup) {
        components.addAll(other.components)
    }

    @PublishedApi
    internal fun addComponent(component: Component) {
        components += component
    }

    @PublishedApi
    internal fun build(): ComponentGroup = ComponentGroup(components.toList())
}

@KommandoDsl
public inline fun components(builder: ComponentGroupBuilder.() -> Unit): ComponentGroup {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return ComponentGroupBuilder().apply(builder).build()
}

/**
 * Returns a copy of `this` component group where the only difference is that all
 * [components][ComponentGroup.components] have been [disabled][Component.isDisabled].
 */
public fun ComponentGroup.copyWithAllDisabled(): ComponentGroup {
    val disabledComponents = components.map {
        when (it) {
            is ButtonComponent -> it.copy(isDisabled = true)
            is SelectMenuComponent -> it.copy(isDisabled = true)
            is EnumSelectMenuComponent<*> -> it.copy(isDisabled = true)
            is LinkButtonComponent -> it.copy(isDisabled = true)
        }
    }
    return copy(components = disabledComponents)
}

public suspend fun Message.disableComponentsIn(
    kommando: Kommando,
    duration: Duration,
    components: ComponentGroup,
    shouldUnregister: Boolean = false,
): Job = kommando.kord.launch {
    delay(duration)
    edit {
        components.disableAllAndApplyToMessage(kommando, this, shouldUnregister = shouldUnregister)
    }
}

public suspend fun Message.disableComponentsIn(
    kommando: Kommando,
    duration: Duration,
    shouldUnregister: Boolean = true,
): Job = kommando.kord.launch {
    delay(duration)
    edit {
        for (row in actionRows) {
            actionRow {
                for (component in row.components) {
                    when (component) {
                        is KordButtonComponent -> when (val url = component.url) {
                            null -> interactionButton(component.style, component.customId!!) {
                                this.label = component.label
                                this.emoji = component.data.emoji.value
                                this.disabled = true
                            }
                            else -> linkButton(url) {
                                this.label = component.label
                                this.emoji = component.data.emoji.value
                                this.disabled = true
                            }
                        }
                        is KordSelectMenuComponent -> selectMenu(component.customId) {
                            this.placeholder = component.placeholder
                            this.allowedValues = component.minValues..component.maxValues
                            for (option in component.options) {
                                option(option.label, option.value) {
                                    this.description = option.description
                                    this.emoji = option.emoji
                                    this.default = option.default
                                }
                            }
                            this.disabled = true
                        }
                        is ActionRowComponent -> error("Nested ActionRowComponent in list of ActionRowComponents")
                        is KordTextInputComponent -> textInput(component.style, component.customId, component.label) {
                            this.allowedLength =
                                component.minLength?.let { min -> component.maxLength?.let { max -> min..max } }
                            this.placeholder = component.placeholder
                            this.required = component.required
                            this.disabled = true
                        }
                        is UnknownComponent -> continue
                    }
                }
            }
        }
    }
    if (shouldUnregister) {
        for (id in actionRows.getCustomIds()) kommando.componentStorage -= id
    }
}

public suspend fun Message.removeComponentsIn(
    kommando: Kommando,
    duration: Duration,
    components: ComponentGroup,
    shouldUnregister: Boolean = false,
): Job = kommando.kord.launch {
    delay(duration)
    edit {
        this.components = mutableListOf()
    }
    if (shouldUnregister) kommando.componentStorage -= components
}

public suspend fun Message.removeComponentsIn(
    kommando: Kommando,
    duration: Duration,
    shouldUnregister: Boolean = true,
): Job = kommando.kord.launch {
    delay(duration)
    edit {
        this.components = mutableListOf()
    }
    if (shouldUnregister) {
        for (id in actionRows.getCustomIds()) kommando.componentStorage -= id
    }
}

private fun List<ActionRowComponent>.getCustomIds(): List<String> = flatMap { it.components }
    .mapNotNull {
        when (it) {
            is KordButtonComponent -> it.customId
            is ActionRowComponent -> error("Nested ActionRowComponent in list of ActionRowComponents")
            is KordSelectMenuComponent -> it.customId
            is KordTextInputComponent -> it.customId
            is UnknownComponent -> null
        }
    }

public suspend fun PublicFollowupMessage.disableComponentsIn(
    kommando: Kommando,
    duration: Duration,
    components: ComponentGroup,
    shouldUnregister: Boolean = false,
): Job = message.disableComponentsIn(kommando, duration, components, shouldUnregister)

public suspend fun PublicFollowupMessage.disableComponentsIn(
    kommando: Kommando,
    duration: Duration,
    shouldUnregister: Boolean = true,
): Job = message.disableComponentsIn(kommando, duration, shouldUnregister)

public suspend fun PublicFollowupMessage.removeComponentsIn(
    kommando: Kommando,
    duration: Duration,
    components: ComponentGroup,
    shouldUnregister: Boolean = false,
): Job = message.removeComponentsIn(kommando, duration, components, shouldUnregister)

public suspend fun PublicFollowupMessage.removeComponentsIn(
    kommando: Kommando,
    duration: Duration,
    shouldUnregister: Boolean = true,
): Job = message.removeComponentsIn(kommando, duration, shouldUnregister)

public suspend fun PublicMessageInteractionResponseBehavior.disableComponentsIn(
    kommando: Kommando,
    duration: Duration,
    components: ComponentGroup,
    shouldUnregister: Boolean = false,
): Job = kommando.kord.launch {
    delay(duration)
    edit {
        components.disableAllAndApplyToMessage(kommando, this, shouldUnregister = shouldUnregister)
    }
}

public suspend fun PublicMessageInteractionResponseBehavior.removeComponentsIn(
    kommando: Kommando,
    duration: Duration,
    components: ComponentGroup,
    shouldUnregister: Boolean = false,
): Job = kommando.kord.launch {
    delay(duration)
    edit {
        this.components = mutableListOf()
    }
    if (shouldUnregister) kommando.componentStorage -= components
}

public suspend fun EphemeralMessageInteractionResponseBehavior.disableComponentsIn(
    kommando: Kommando,
    duration: Duration,
    components: ComponentGroup,
    shouldUnregister: Boolean = false,
): Job = kommando.kord.launch {
    delay(duration)
    edit {
        components.disableAllAndApplyToMessage(kommando, this, shouldUnregister = shouldUnregister)
    }
}

public suspend fun EphemeralMessageInteractionResponseBehavior.removeComponentsIn(
    kommando: Kommando,
    duration: Duration,
    components: ComponentGroup,
    shouldUnregister: Boolean = false,
): Job = kommando.kord.launch {
    delay(duration)
    edit {
        this.components = mutableListOf()
    }
    if (shouldUnregister) kommando.componentStorage -= components
}

public suspend fun EphemeralFollowupMessage.disableComponentsIn(
    kommando: Kommando,
    duration: Duration,
    components: ComponentGroup,
    shouldUnregister: Boolean = false,
): Job = kommando.kord.launch {
    delay(duration)
    edit {
        components.disableAllAndApplyToMessage(kommando, this, shouldUnregister = shouldUnregister)
    }
}

public suspend fun EphemeralFollowupMessage.removeComponentsIn(
    kommando: Kommando,
    duration: Duration,
    components: ComponentGroup,
    shouldUnregister: Boolean = false,
): Job = kommando.kord.launch {
    delay(duration)
    edit {
        this.components = mutableListOf()
    }
    if (shouldUnregister) kommando.componentStorage -= components
}

public suspend fun ComponentGroup.disableAllAndApplyToMessage(
    kommando: Kommando,
    message: Message,
    shouldUnregister: Boolean = false,
): ComponentGroup {
    val newGroup = copyWithAllDisabled().applyToMessage(message)
    if (shouldUnregister) kommando.componentStorage -= this
    return newGroup
}

public suspend fun ComponentGroup.disableAllAndApplyToEphemeralMessage(
    kommando: Kommando,
    message: EphemeralMessageInteractionResponseBehavior,
    shouldUnregister: Boolean = false,
): ComponentGroup {
    val newGroup = copyWithAllDisabled().applyToEphemeralMessage(message)
    if (shouldUnregister) kommando.componentStorage -= this
    return newGroup
}

public fun ComponentGroup.disableAllAndApplyToMessage(
    kommando: Kommando,
    message: MessageCreateBuilder,
    shouldUnregister: Boolean = false,
): ComponentGroup {
    val newGroup = copyWithAllDisabled().applyToMessage(message)
    if (shouldUnregister) kommando.componentStorage -= this
    return newGroup
}

public fun ComponentGroup.disableAllAndApplyToMessage(
    kommando: Kommando,
    message: MessageModifyBuilder,
    shouldUnregister: Boolean = false,
): ComponentGroup {
    val newGroup = copyWithAllDisabled().applyToMessage(message)
    if (shouldUnregister) kommando.componentStorage -= this
    return newGroup
}

public fun ComponentGroup.applyToMessageAndRegister(
    kommando: Kommando,
    message: MessageCreateBuilder,
): ComponentGroup = apply {
    message.components.clear()
    kommando.componentStorage += this
    applyToMessage(message)
}

public fun ComponentGroup.applyToMessageAndRegister(
    kommando: Kommando,
    message: MessageModifyBuilder,
): ComponentGroup = apply {
    message.components = mutableListOf()
    kommando.componentStorage += this
    applyToMessage(message)
}

public suspend fun ComponentGroup.applyToEphemeralMessage(
    message: EphemeralMessageInteractionResponseBehavior,
): ComponentGroup = apply {
    message.edit {
        for (row in componentRows) {
            if (row.isEmpty()) continue
            actionRow {
                for (component in row) {
                    with(component) { buildComponent() }
                }
            }
        }
    }
}

public suspend fun ComponentGroup.applyToMessage(message: Message): ComponentGroup = apply {
    message.edit {
        for (row in componentRows) {
            if (row.isEmpty()) continue
            actionRow {
                for (component in row) {
                    with(component) { buildComponent() }
                }
            }
        }
    }
}

public fun ComponentGroup.applyToMessage(message: MessageCreateBuilder): ComponentGroup = apply {
    for (row in componentRows) {
        if (row.isEmpty()) continue
        message.actionRow {
            for (component in row) {
                with(component) { buildComponent() }
            }
        }
    }
}

public fun ComponentGroup.applyToMessage(message: MessageModifyBuilder): ComponentGroup = apply {
    for (row in componentRows) {
        if (row.isEmpty()) continue
        message.actionRow {
            for (component in row) {
                with(component) { buildComponent() }
            }
        }
    }
}

@KommandoDsl
public inline fun MessageCreateBuilder.components(
    kommando: Kommando,
    builder: ComponentGroupBuilder.() -> Unit,
): ComponentGroup {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    val group = ComponentGroupBuilder().apply(builder).build()
    group.applyToMessageAndRegister(kommando, this)
    return group
}

@KommandoDsl
public inline fun MessageModifyBuilder.components(
    kommando: Kommando,
    builder: ComponentGroupBuilder.() -> Unit,
): ComponentGroup {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    val group = ComponentGroupBuilder().apply(builder).build()
    group.applyToMessageAndRegister(kommando, this)
    return group
}