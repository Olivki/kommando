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

import dev.kord.core.entity.interaction.SelectMenuInteraction
import dev.kord.rest.builder.component.ActionRowBuilder
import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.internal.createUuidString
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public data class EnumSelectMenuComponent<T>(
    override val customId: String,
    public val options: List<T>,
    public val allowedValues: ClosedRange<Int>,
    public val placeholder: String?,
    override val isDisabled: Boolean,
    override val executor: ComponentExecutor<EnumSelectMenuComponentData<T>>,
) : ExecutableComponent<SelectMenuComponentEvent, EnumSelectMenuComponentData<T>>
        where T : Enum<T>,
              T : EnumSelectMenuAdapter {
    public val optionMappings: Map<String, T> = options.associateBy { it.name }

    override val width: Int
        get() = 5

    override fun ActionRowBuilder.buildComponent() {
        selectMenu(customId) {
            allowedValues = this@EnumSelectMenuComponent.allowedValues
            placeholder = this@EnumSelectMenuComponent.placeholder
            disabled = this@EnumSelectMenuComponent.isDisabled
            for (option in this@EnumSelectMenuComponent.options) {
                option(option.label, option.name) {
                    description = option.description
                    emoji = option.emoji
                    default = option.isDefault
                }
            }
        }
    }
}

public data class EnumSelectMenuComponentData<T>(
    override val kommando: Kommando,
    override val event: SelectMenuComponentEvent,
    private val optionMappings: Map<String, Enum<*>>,
) : ComponentData<SelectMenuComponentEvent>
        where T : Enum<T>,
              T : EnumSelectMenuAdapter {
    override val interaction: SelectMenuInteraction
        get() = event.interaction

    /**
     * A list of the values of all the selected menu options, will at minimum contain one entry.
     */
    @Suppress("UNCHECKED_CAST")
    public val values: List<T> = interaction.values.map { optionMappings.getValue(it) as T }

    /**
     * The first entry of [values].
     *
     * If the select menu one allows one option to be selected, this will be the only value.
     */
    public val value: T
        get() = values.first()
}

public class EnumSelectMenuComponentBuilder<T> @PublishedApi internal constructor(
    private val options: List<T>,
    private val customId: String,
) : SelectMenuBuilder<EnumSelectMenuComponentData<T>, EnumSelectMenuComponent<T>>()
        where T : Enum<T>,
              T : EnumSelectMenuAdapter {
    @PublishedApi
    override fun build(): EnumSelectMenuComponent<T> = EnumSelectMenuComponent(
        customId = customId,
        options = options.toList(),
        allowedValues = allowedValues,
        placeholder = placeholder,
        isDisabled = isDisabled,
        executor = getNonNullExecutor(),
    )
}

@KommandoDsl
public inline fun <reified T> ComponentGroupBuilder.enumSelectMenu(
    customId: String = createUuidString(),
    builder: EnumSelectMenuComponentBuilder<T>.() -> Unit,
) where T : Enum<T>,
        T : EnumSelectMenuAdapter {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    val options = enumValues<T>().toList()
    addComponent(EnumSelectMenuComponentBuilder(options, customId).apply(builder).build())
}