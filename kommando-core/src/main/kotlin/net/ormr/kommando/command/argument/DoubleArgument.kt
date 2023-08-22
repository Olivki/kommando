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

package net.ormr.kommando.command.argument

import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.rest.builder.interaction.number
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.command.CustomizableCommand
import net.ormr.kommando.localization.*

public class DoubleArgument(
    override val key: String,
    override val name: Message,
    override val description: Message,
    override val min: Double?,
    override val max: Double?,
    override val autoComplete: AutoCompleteAction?,
) : AutoCompletableArgumentWithChoices<Double, Double, ArgumentType.Number>,
    ArgumentWithRange<Double, Double, Double, ArgumentType.Number> {
    override val type: ArgumentType.Number
        get() = ArgumentType.Number

    override fun convertChoiceValue(value: Double): Double = value

    override fun convertNullableArgumentValue(value: Double?): Double? = value

    override fun convertArgumentValue(value: Double): Double = value

    context(ArgumentBuildContext, BaseInputChatBuilder)
    override fun buildArgument(resolver: MessageResolver, isRequired: Boolean) {
        number(resolver[name], resolver[description]) {
            registerLocalizations()
            this.autocomplete = autoComplete != null
            this.required = isRequired
            this.minValue = min
            this.maxValue = max
        }
    }

    context(ArgumentBuildContext, BaseInputChatBuilder)
    override fun buildArgumentWithChoices(
        resolver: MessageResolver,
        choices: List<ArgumentChoice<Double>>,
        isRequired: Boolean,
    ) {
        number(resolver[name], resolver[description]) {
            registerLocalizations()
            this.required = isRequired
            // TODO: do we add min & max for choice ones?
            this.minValue = min
            this.maxValue = max
            addChoices(choices)
        }
    }

    override fun toString(): String =
        "DoubleArgument(key='$key', name='${name.defaultString}', description='${description.defaultString}', min=$min, max=$max)"
}

context(Cmd)
@KommandoDsl
public fun <Cmd> double(
    name: Message? = null,
    description: String,
    min: Double? = null,
    max: Double? = null,
    autoComplete: AutoCompleteAction? = null,
): ArgumentBuilder<Cmd, Double, DoubleArgument>
        where Cmd : CustomizableCommand<*> =
    ArgumentHelper.newBuilder(name, BasicMessage(description)) { key, resolvedName, desc ->
        DoubleArgument(key, resolvedName, desc, min, max, autoComplete)
    }

context(Cmd)
@KommandoDsl
public fun <Cmd> double(
    name: Message? = null,
    description: LocalizedMessage? = null,
    min: Double? = null,
    max: Double? = null,
    autoComplete: AutoCompleteAction? = null,
): ArgumentBuilder<Cmd, Double, DoubleArgument>
        where Cmd : CustomizableCommand<*> =
    ArgumentHelper.newBuilder(name, description) { key, resolvedName, desc ->
        DoubleArgument(key, resolvedName, desc, min, max, autoComplete)
    }