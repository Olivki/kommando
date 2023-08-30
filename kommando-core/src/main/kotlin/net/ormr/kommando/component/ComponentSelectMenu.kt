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

import dev.kord.core.entity.interaction.SelectMenuInteraction
import dev.kord.rest.builder.component.ActionRowBuilder
import net.ormr.kommando.KommandoDsl

// TODO: add the other select menu types

private typealias SelectMenuComponentCallback = ComponentCallback<SelectMenuExecutionContext>

/**
 * A component that allows the user to select option(s) from a dropdown menu.
 *
 * TODO: property docs
 */
public data class ComponentSelectMenu(
    override val id: String,
    override val interactionId: String,
    val options: List<ComponentSelectMenuOption>,
    val allowedValues: ClosedRange<Int>,
    val placeholder: String?,
    override val isDisabled: Boolean,
    val onSelect: SelectMenuComponentCallback,
) : ExecutableComponent<ComponentSelectMenu, SelectMenuExecutionContext> {
    init {
        require(options.isNotEmpty()) { "Select menu must have at least one option" }
    }

    override val width: ComponentWidth
        get() = ComponentWidth.FIVE

    context(SelectMenuExecutionContext)
    override suspend fun execute() {
        onSelect.execute()
    }

    override fun withDisabled(isDisabled: Boolean): ComponentSelectMenu = copy(isDisabled = isDisabled)

    context(ActionRowBuilder)
    override fun buildComponent() {
        val self = this
        stringSelect(customId = interactionId) {
            allowedValues = self.allowedValues
            placeholder = self.placeholder
            disabled = self.isDisabled
            for (option in self.options) option.buildOption()
        }
    }
}

public interface SelectMenuExecutionContext : ComponentExecutionContext<SelectMenuInteraction>

/**
 * A list of the values of all the selected menu options, will at minimum contain one entry.
 */
public val SelectMenuExecutionContext.values: List<String>
    get() = interaction.values

/**
 * The first entry of [values].
 *
 * If the select menu one allows one option to be selected, this will be the only value.
 */
public val SelectMenuExecutionContext.firstValue: String
    get() = values.first()

@KommandoDsl
public class ComponentSelectMenuOptionsBuilder @PublishedApi internal constructor() {
    internal val options: MutableList<ComponentSelectMenuOption> = mutableListOf()

    @PublishedApi
    internal fun build(): List<ComponentSelectMenuOption> = options.toList()
}

@KommandoDsl
public inline fun VisibleComponentContainerBuilder.stringSelect(
    id: String,
    options: ComponentSelectMenuOptionsBuilder.() -> Unit,
    allowedValues: ClosedRange<Int> = 1..1,
    placeholder: String? = null,
    isDisabled: Boolean = false,
    onSelect: SelectMenuComponentCallback,
) {
    val builtOptions = ComponentSelectMenuOptionsBuilder().apply(options).build()
    addExecutable(id) { interactionId ->
        ComponentSelectMenu(
            id = id,
            interactionId = interactionId,
            options = builtOptions,
            allowedValues = allowedValues,
            placeholder = placeholder,
            isDisabled = isDisabled,
            onSelect = onSelect,
        )
    }
}