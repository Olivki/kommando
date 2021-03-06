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
import dev.kord.core.event.interaction.SelectMenuInteractionCreateEvent
import dev.kord.rest.builder.component.ActionRowBuilder
import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.internal.createUuidString
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal typealias SelectMenuComponentEvent = SelectMenuInteractionCreateEvent

public data class SelectMenuComponent(
    override val customId: String,
    public val options: List<SelectMenuOptionComponent>,
    public val allowedValues: ClosedRange<Int>,
    public val placeholder: String?,
    override val isDisabled: Boolean,
    override val executor: ComponentExecutor<SelectMenuComponentData>,
) : ExecutableComponent<SelectMenuComponentEvent, SelectMenuComponentData> {
    override val width: Int
        get() = 5

    override fun ActionRowBuilder.buildComponent() {
        selectMenu(customId) {
            allowedValues = this@SelectMenuComponent.allowedValues
            placeholder = this@SelectMenuComponent.placeholder
            disabled = this@SelectMenuComponent.isDisabled
            for (option in this@SelectMenuComponent.options) {
                option(option.label, option.value) {
                    description = option.description
                    emoji = option.emoji
                    default = option.isDefault
                }
            }
        }
    }
}

public data class SelectMenuComponentData(
    override val kommando: Kommando,
    override val event: SelectMenuComponentEvent,
) : ComponentData<SelectMenuComponentEvent> {
    override val interaction: SelectMenuInteraction
        get() = event.interaction

    /**
     * A list of the values of all the selected menu options, will at minimum contain one entry.
     */
    public val values: List<String>
        get() = interaction.values

    /**
     * The first entry of [values].
     *
     * If the select menu one allows one option to be selected, this will be the only value.
     */
    public val value: String
        get() = values.first()
}

public class SelectMenuComponentBuilder @PublishedApi internal constructor(private val customId: String) :
    SelectMenuBuilder<SelectMenuComponentData, SelectMenuComponent>() {
    private val options = mutableListOf<SelectMenuOptionComponent>()

    @PublishedApi
    internal fun addOption(option: SelectMenuOptionComponent) {
        options += option
    }

    @PublishedApi
    override fun build(): SelectMenuComponent = SelectMenuComponent(
        customId = customId,
        options = options.toList(),
        allowedValues = allowedValues,
        placeholder = placeholder,
        isDisabled = isDisabled,
        executor = getNonNullExecutor(),
    )
}

@KommandoDsl
public inline fun ComponentGroupBuilder.selectMenu(
    customId: String = createUuidString(),
    builder: SelectMenuComponentBuilder.() -> Unit,
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    addComponent(SelectMenuComponentBuilder(customId).apply(builder).build())
}