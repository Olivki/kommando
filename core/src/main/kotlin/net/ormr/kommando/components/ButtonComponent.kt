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

@file:JvmName("ButtonComponentKt")

package net.ormr.kommando.components

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.rest.builder.component.ActionRowBuilder
import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.internal.createUuidString
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal typealias ButtonComponentEvent = ButtonInteractionCreateEvent

public data class ButtonComponent(
    override val customId: String,
    override val label: String?,
    override val emoji: DiscordPartialEmoji?,
    public val style: ButtonStyle,
    override val isDisabled: Boolean,
    override val executor: ComponentExecutor<ButtonComponentData>,
) : ExecutableComponent<ButtonComponentEvent, ButtonComponentData>, EmojiComponent, LabeledComponent {
    override val width: Int
        get() = 1

    override fun ActionRowBuilder.buildComponent() {
        interactionButton(style, customId) {
            label = this@ButtonComponent.label
            emoji = this@ButtonComponent.emoji
            disabled = isDisabled
        }
    }
}

public data class ButtonComponentData(
    override val kommando: Kommando,
    override val event: ButtonComponentEvent,
) : ComponentData<ButtonComponentEvent> {
    override val interaction: ButtonInteraction
        get() = event.interaction
}

public class ButtonComponentBuilder @PublishedApi internal constructor(
    private val customId: String,
    private val style: ButtonStyle,
) : ExecutableComponentBuilder<ButtonComponentEvent, ButtonComponentData, ButtonComponent>(), EmojiBuilder,
    LabelBuilder {
    override var label: String? = null
    override var emoji: DiscordPartialEmoji? = null

    @PublishedApi
    override fun build(): ButtonComponent {
        checkLabelAndEmoji()
        return ButtonComponent(
            customId = customId,
            label = label,
            emoji = emoji,
            style = style,
            isDisabled = isDisabled,
            executor = getNonNullExecutor(),
        )
    }
}

@KommandoDsl
public inline fun ComponentGroupBuilder.button(
    style: ButtonStyle = ButtonStyle.Primary,
    customId: String = createUuidString(),
    builder: ButtonComponentBuilder.() -> Unit,
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    addComponent(ButtonComponentBuilder(customId, style).apply(builder).build())
}