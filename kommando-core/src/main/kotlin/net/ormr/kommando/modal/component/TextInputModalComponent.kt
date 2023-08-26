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

import dev.kord.common.entity.TextInputStyle
import dev.kord.rest.builder.component.ActionRowBuilder
import net.ormr.kommando.modal.Modal

public data class TextInputModalComponent(
    public val style: TextInputStyle,
    override val customId: String,
    override val label: String,
    public val allowedLength: ClosedRange<Int>?,
    public val placeholder: String?,
    public val defaultValue: String?,
    public val isRequired: Boolean,
    override val isDisabled: Boolean,
) : LabeledModalComponent<String, String, ModalComponentType.TextInput> {
    override val type: ModalComponentType.TextInput
        get() = ModalComponentType.TextInput

    override fun convertComponentValue(value: String): String = value

    override fun convertNullableComponentValue(value: String?): String? = value

    context(ActionRowBuilder)
    override fun buildComponent() {
        val self = this
        textInput(style, customId, label) {
            allowedLength = self.allowedLength
            placeholder = self.placeholder
            value = self.defaultValue
            required = self.isRequired
            disabled = self.isDisabled
        }
    }
}

/**
 * Registers a new text input component.
 *
 * @param [customId] the unique identifier of the component, max `100` characters.
 * @param [label] the label shown above the input field, max `45` characters.
 * @param [allowedLength] the range of lengths of inputs that the text input can accept, min `0` and max `4000`.
 * @param [placeholder] the placeholder text shown in the input field when it's empty, max `100` characters.
 * @param [defaultValue] the initial value of the input field, max `4000` characters.
 * @param [isRequired] whether the component is required or not, `true` by default.
 * @param [isDisabled] whether the component is disabled or not, `false` by default.
 *
 * @see [paragraphTextInput]
 * @see [textInput]
 */
context(Modal<*>)
public fun shortTextInput(
    customId: String? = null,
    label: String,
    allowedLength: ClosedRange<Int>? = null,
    placeholder: String? = null,
    defaultValue: String? = null,
    isRequired: Boolean = true,
    isDisabled: Boolean = false,
): ModalComponentBuilder<String, TextInputModalComponent> = textInput(
    customId = customId,
    style = TextInputStyle.Short,
    label = label,
    allowedLength = allowedLength,
    placeholder = placeholder,
    defaultValue = defaultValue,
    isRequired = isRequired,
    isDisabled = isDisabled,
)

/**
 * Registers a new text input component.
 *
 * @param [customId] the unique identifier of the component, max `100` characters.
 * @param [label] the label shown above the input field, max `45` characters.
 * @param [allowedLength] the range of lengths of inputs that the text input can accept, min `0` and max `4000`.
 * @param [placeholder] the placeholder text shown in the input field when it's empty, max `100` characters.
 * @param [defaultValue] the initial value of the input field, max `4000` characters.
 * @param [isRequired] whether the component is required or not, `true` by default.
 * @param [isDisabled] whether the component is disabled or not, `false` by default.
 *
 * @see [shortTextInput]
 * @see [textInput]
 */
context(Modal<*>)
public fun paragraphTextInput(
    customId: String? = null,
    label: String,
    allowedLength: ClosedRange<Int>? = null,
    placeholder: String? = null,
    defaultValue: String? = null,
    isRequired: Boolean = true,
    isDisabled: Boolean = false,
): ModalComponentBuilder<String, TextInputModalComponent> = textInput(
    customId = customId,
    style = TextInputStyle.Paragraph,
    label = label,
    allowedLength = allowedLength,
    placeholder = placeholder,
    defaultValue = defaultValue,
    isRequired = isRequired,
    isDisabled = isDisabled,
)

/**
 * Registers a new text input component.
 *
 * @param [customId] the unique identifier of the component, max `100` characters.
 * @param [style] the style of the text input.
 * @param [label] the label shown above the input field, max `45` characters.
 * @param [allowedLength] the range of lengths of inputs that the text input can accept, min `0` and max `4000`.
 * @param [placeholder] the placeholder text shown in the input field when it's empty, max `100` characters.
 * @param [defaultValue] the initial value of the input field, max `4000` characters.
 * @param [isRequired] whether the component is required or not, `true` by default.
 * @param [isDisabled] whether the component is disabled or not, `false` by default.
 *
 * @see [shortTextInput]
 * @see [paragraphTextInput]
 */
context(Modal<*>)
public fun textInput(
    customId: String? = null,
    style: TextInputStyle,
    label: String,
    allowedLength: ClosedRange<Int>? = null,
    placeholder: String? = null,
    defaultValue: String? = null,
    isRequired: Boolean = true,
    isDisabled: Boolean = false,
): ModalComponentBuilder<String, TextInputModalComponent> = ModalComponentHelper.newBuilder(customId) { id ->
    TextInputModalComponent(
        style = style,
        customId = id,
        label = label,
        allowedLength = allowedLength,
        placeholder = placeholder,
        defaultValue = defaultValue,
        isRequired = isRequired,
        isDisabled = isDisabled,
    )
}