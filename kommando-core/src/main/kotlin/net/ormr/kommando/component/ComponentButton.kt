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

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.rest.builder.component.ActionRowBuilder
import net.ormr.kommando.KommandoDsl

private typealias ButtonComponentCallback = ComponentCallback<ButtonExecutionContext>

public data class ComponentButton(
    override val id: String,
    override val interactionId: String,
    override val label: String?,
    override val emoji: DiscordPartialEmoji?,
    public val style: ButtonStyle,
    override val isDisabled: Boolean,
    val onClick: ButtonComponentCallback,
) : ExecutableComponent<ComponentButton, ButtonExecutionContext>, LabeledComponent<ComponentButton>,
    EmojiComponent<ComponentButton> {
    override val width: ComponentWidth
        get() = ComponentWidth.ONE

    context(ButtonExecutionContext)
    override suspend fun execute() {
        onClick.execute()
    }

    override fun withDisabled(isDisabled: Boolean): ComponentButton = copy(isDisabled = isDisabled)

    context(ActionRowBuilder)
    override fun buildComponent() {
        val self = this
        interactionButton(style = style, customId = interactionId) {
            label = self.label
            emoji = self.emoji
            disabled = self.isDisabled
        }
    }
}

public interface ButtonExecutionContext : ComponentExecutionContext<ButtonInteraction>

// Primary

@KommandoDsl
public fun VisibleComponentContainerBuilder.primaryButton(
    id: String,
    label: String,
    emoji: DiscordPartialEmoji,
    isDisabled: Boolean = false,
    onClick: ButtonComponentCallback,
) {
    button(
        style = ButtonStyle.Primary,
        id = id,
        label = label,
        emoji = emoji,
        isDisabled = isDisabled,
        onClick = onClick,
    )
}

@KommandoDsl
public fun VisibleComponentContainerBuilder.primaryButton(
    id: String,
    label: String,
    isDisabled: Boolean = false,
    onClick: ButtonComponentCallback,
) {
    button(
        style = ButtonStyle.Primary,
        id = id,
        label = label,
        isDisabled = isDisabled,
        onClick = onClick,
    )
}

@KommandoDsl
public fun VisibleComponentContainerBuilder.primaryButton(
    id: String,
    emoji: DiscordPartialEmoji,
    isDisabled: Boolean = false,
    onClick: ButtonComponentCallback,
) {
    button(
        style = ButtonStyle.Primary,
        id = id,
        emoji = emoji,
        isDisabled = isDisabled,
        onClick = onClick,
    )
}

// Secondary

@KommandoDsl
public fun VisibleComponentContainerBuilder.secondaryButton(
    id: String,
    label: String,
    emoji: DiscordPartialEmoji,
    isDisabled: Boolean = false,
    onClick: ButtonComponentCallback,
) {
    button(
        style = ButtonStyle.Secondary,
        id = id,
        label = label,
        emoji = emoji,
        isDisabled = isDisabled,
        onClick = onClick,
    )
}

@KommandoDsl
public fun VisibleComponentContainerBuilder.secondaryButton(
    id: String,
    label: String,
    isDisabled: Boolean = false,
    onClick: ButtonComponentCallback,
) {
    button(
        style = ButtonStyle.Secondary,
        id = id,
        label = label,
        isDisabled = isDisabled,
        onClick = onClick,
    )
}

@KommandoDsl
public fun VisibleComponentContainerBuilder.secondaryButton(
    id: String,
    emoji: DiscordPartialEmoji,
    isDisabled: Boolean = false,
    onClick: ButtonComponentCallback,
) {
    button(
        style = ButtonStyle.Secondary,
        id = id,
        emoji = emoji,
        isDisabled = isDisabled,
        onClick = onClick,
    )
}

// Success

@KommandoDsl
public fun VisibleComponentContainerBuilder.successButton(
    id: String,
    label: String,
    emoji: DiscordPartialEmoji,
    isDisabled: Boolean = false,
    onClick: ButtonComponentCallback,
) {
    button(
        style = ButtonStyle.Success,
        id = id,
        label = label,
        emoji = emoji,
        isDisabled = isDisabled,
        onClick = onClick,
    )
}

@KommandoDsl
public fun VisibleComponentContainerBuilder.successButton(
    id: String,
    label: String,
    isDisabled: Boolean = false,
    onClick: ButtonComponentCallback,
) {
    button(
        style = ButtonStyle.Success,
        id = id,
        label = label,
        isDisabled = isDisabled,
        onClick = onClick,
    )
}

@KommandoDsl
public fun VisibleComponentContainerBuilder.successButton(
    id: String,
    emoji: DiscordPartialEmoji,
    isDisabled: Boolean = false,
    onClick: ButtonComponentCallback,
) {
    button(
        style = ButtonStyle.Success,
        id = id,
        emoji = emoji,
        isDisabled = isDisabled,
        onClick = onClick,
    )
}

// Danger

@KommandoDsl
public fun VisibleComponentContainerBuilder.dangerButton(
    id: String,
    label: String,
    emoji: DiscordPartialEmoji,
    isDisabled: Boolean = false,
    onClick: ButtonComponentCallback,
) {
    button(
        style = ButtonStyle.Danger,
        id = id,
        label = label,
        emoji = emoji,
        isDisabled = isDisabled,
        onClick = onClick,
    )
}

@KommandoDsl
public fun VisibleComponentContainerBuilder.dangerButton(
    id: String,
    label: String,
    isDisabled: Boolean = false,
    onClick: ButtonComponentCallback,
) {
    button(
        style = ButtonStyle.Danger,
        id = id,
        label = label,
        isDisabled = isDisabled,
        onClick = onClick,
    )
}

@KommandoDsl
public fun VisibleComponentContainerBuilder.dangerButton(
    id: String,
    emoji: DiscordPartialEmoji,
    isDisabled: Boolean = false,
    onClick: ButtonComponentCallback,
) {
    button(
        style = ButtonStyle.Danger,
        id = id,
        emoji = emoji,
        isDisabled = isDisabled,
        onClick = onClick,
    )
}

// Button

@KommandoDsl
public fun VisibleComponentContainerBuilder.button(
    style: ButtonStyle,
    id: String,
    label: String,
    emoji: DiscordPartialEmoji,
    isDisabled: Boolean = false,
    onClick: ButtonComponentCallback,
) {
    button0(
        style = style,
        id = id,
        label = label,
        emoji = emoji,
        isDisabled = isDisabled,
        onClick = onClick,
    )
}

@KommandoDsl
public fun VisibleComponentContainerBuilder.button(
    style: ButtonStyle,
    id: String,
    label: String,
    isDisabled: Boolean = false,
    onClick: ButtonComponentCallback,
) {
    button0(
        style = style,
        id = id,
        label = label,
        emoji = null,
        isDisabled = isDisabled,
        onClick = onClick,
    )
}

@KommandoDsl
public fun VisibleComponentContainerBuilder.button(
    style: ButtonStyle,
    id: String,
    emoji: DiscordPartialEmoji,
    isDisabled: Boolean = false,
    onClick: ButtonComponentCallback,
) {
    button0(
        style = style,
        id = id,
        label = null,
        emoji = emoji,
        isDisabled = isDisabled,
        onClick = onClick,
    )
}

@KommandoDsl
private fun VisibleComponentContainerBuilder.button0(
    style: ButtonStyle,
    id: String,
    label: String? = null,
    emoji: DiscordPartialEmoji? = null,
    isDisabled: Boolean = false,
    onClick: ButtonComponentCallback,
) {
    addExecutable(id) { interactionId ->
        ComponentButton(
            id = id,
            interactionId = interactionId,
            label = label,
            emoji = emoji,
            style = style,
            isDisabled = isDisabled,
            onClick = onClick,
        )
    }
}