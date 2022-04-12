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

import dev.kord.common.entity.TextInputStyle
import dev.kord.rest.builder.component.ActionRowBuilder
import net.ormr.kommando.KommandoDsl
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Represents a [text input](https://discord.com/developers/docs/interactions/message-components#text-inputs) component.
 *
 * @property style The [style][TextInputStyle] of the component.
 * @property allowedLength The range of lengths of inputs that the text input can accept.
 * @property placeholder A placeholder value to show if no value has been entered.
 * @property value A value to fill out the text input with.
 * @property isRequired Whether this component needs to be filled out.
 */
public data class TextInputModalComponent(
    override val customId: String,
    override val label: String,
    public val style: TextInputStyle,
    public val allowedLength: ClosedRange<Int>?,
    public val placeholder: String?,
    public val value: String?,
    public val isRequired: Boolean,
    override val isDisabled: Boolean,
) : ModalComponent, LabeledModalComponent {
    override fun ActionRowBuilder.buildComponent() {
        textInput(style, customId = customId, label = label) {
            allowedLength = this@TextInputModalComponent.allowedLength
            placeholder = this@TextInputModalComponent.placeholder
            value = this@TextInputModalComponent.value
            required = this@TextInputModalComponent.isRequired
            disabled = this@TextInputModalComponent.isDisabled
        }
    }
}

@KommandoDsl
public class TextInputModalComponentBuilder @PublishedApi internal constructor(
    private val customId: String,
    private val label: String,
    private val style: TextInputStyle,
) : ModalComponentBuilder<TextInputModalComponent>() {
    /**
     * The range of lengths of inputs that the text input can accept.
     *
     * Range should be in `0..4000`.
     */
    public var allowedLength: ClosedRange<Int>? = null

    /**
     * A placeholder value to show if no value has been entered.
     *
     * Can be 100 characters max.
     */
    public var placeholder: String? = null

    /**
     * A value to fill out the text input with.
     *
     * Can be 4000 characters max.
     */
    public var value: String? = null

    /**
     * Whether this component needs to be filled out, `true` by default.
     */
    public var isRequired: Boolean = true

    @PublishedApi
    override fun build(): TextInputModalComponent = TextInputModalComponent(
        customId = customId,
        label = label,
        style = style,
        allowedLength = allowedLength,
        placeholder = placeholder,
        value = value,
        isRequired = isRequired,
        isDisabled = isDisabled
    )
}

/**
 * Creates a new [TextInputModalComponent] configured by [builder] and adds it this builder.
 */
@KommandoDsl
public inline fun ModalBuilder.textInput(
    style: TextInputStyle,
    customId: String,
    label: String,
    builder: TextInputModalComponentBuilder.() -> Unit = {},
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    addComponent(TextInputModalComponentBuilder(customId, label, style).apply(builder).build())
}

/**
 * Creates a new [TextInputModalComponent] with [style][TextInputModalComponent.style] set to [short][TextInputStyle.Short],
 * configured by [builder] and adds it this builder.
 */
@KommandoDsl
public inline fun ModalBuilder.shortTextInput(
    customId: String,
    label: String,
    builder: TextInputModalComponentBuilder.() -> Unit = {},
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    textInput(TextInputStyle.Short, customId = customId, label = label, builder)
}

/**
 * Creates a new [TextInputModalComponent] with [style][TextInputModalComponent.style] set to [paragraph][TextInputStyle.Paragraph],
 * configured by [builder] and adds it this builder.
 */
@KommandoDsl
public inline fun ModalBuilder.paragraphTextInput(
    customId: String,
    label: String,
    builder: TextInputModalComponentBuilder.() -> Unit = {},
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    textInput(TextInputStyle.Paragraph, customId = customId, label = label, builder)
}