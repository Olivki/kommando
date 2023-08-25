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
import dev.kord.rest.builder.interaction.integer
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.command.CustomizableCommand
import net.ormr.kommando.localization.Message

public class LongArgument(
    override val key: String,
    override val name: Message,
    override val description: Message,
    override val min: Long?,
    override val max: Long?,
    override val autoComplete: AutoCompleteAction?,
) : AutoCompletableArgumentWithChoices<Long, Long, ArgumentType.Integer>,
    ArgumentWithRange<Long, Long, Long, ArgumentType.Integer> {
    override val type: ArgumentType.Integer
        get() = ArgumentType.Integer

    override fun convertChoiceValue(value: Long): Long = value

    override fun convertNullableArgumentValue(value: Long?): Long? = value

    override fun convertArgumentValue(value: Long): Long = value

    context(ArgumentBuildContext, BaseInputChatBuilder)
    override fun buildArgument(isRequired: Boolean) {
        integer(defaultName, defaultDescription) {
            registerLocalizations()
            this.autocomplete = autoComplete != null
            this.required = isRequired
            this.minValue = min
            this.maxValue = max
        }
    }

    context(ArgumentBuildContext, BaseInputChatBuilder)
    override fun buildArgumentWithChoices(
        choices: List<ArgumentChoice<Long>>,
        isRequired: Boolean,
    ) {
        integer(defaultName, defaultDescription) {
            registerLocalizations()
            this.required = isRequired
            // TODO: do we add min & max for choice ones?
            this.minValue = min
            this.maxValue = max
            addChoices(choices)
        }
    }

    override fun toString(): String =
        "LongArgument(key='$key', name='${name.defaultString}', description='${description.defaultString}', min=$min, max=$max)"
}

context(Cmd)
@KommandoDsl
public fun <Cmd> long(
    name: String? = null,
    description: String,
    min: Long? = null,
    max: Long? = null,
    autoComplete: AutoCompleteAction? = null,
): ArgumentBuilder<Cmd, Long, LongArgument>
        where Cmd : CustomizableCommand<*> =
    ArgumentHelper.newBuilder(name, description) { key, resolvedName, desc ->
        LongArgument(key, resolvedName, desc, min, max, autoComplete)
    }