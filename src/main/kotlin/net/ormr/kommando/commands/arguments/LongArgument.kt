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

package net.ormr.kommando.commands.arguments

import dev.kord.common.entity.optional.Optional
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.rest.builder.interaction.integer
import net.ormr.kommando.commands.Command
import net.ormr.kommando.commands.CustomizableCommand
import net.ormr.kommando.commands.delegates.ArgumentDelegateProvider
import net.ormr.kommando.commands.delegates.createDelegate
import net.ormr.kommando.localization.LocalizationResolver
import net.ormr.kommando.localization.LocalizedString

public class LongArgument(
    override val name: String,
    override val description: LocalizedString,
    override val min: Long? = null,
    override val max: Long? = null,
    override val autoComplete: AutoCompleteAction? = null,
) : ArgumentWithChoice<Long, Long, ArgumentType.INTEGER>(), RangedArgument<Long>, AutoCompletableArgument {
    override val type: ArgumentType.INTEGER get() = ArgumentType.INTEGER

    override fun getValue(value: Long): Long = value

    override fun getValueOrNull(value: Long?): Long? = value

    override fun convertChoiceValue(value: Long): Long = value

    override fun BaseInputChatBuilder.buildArgument(resolver: LocalizationResolver, isRequired: Boolean) {
        integer(name, resolver[description]) {
            this.autocomplete = autoComplete != null
            this.required = isRequired
            this.minValue = min
            this.maxValue = max
        }
    }

    override fun BaseInputChatBuilder.buildArgumentWithChoices(
        resolver: LocalizationResolver,
        choices: List<ArgumentChoice<Long>>,
        isRequired: Boolean,
    ) {
        integer(name, resolver[description]) {
            this.required = isRequired
            // TODO: do we add min & max for choice ones?
            this.minValue = min
            this.maxValue = max
            for ((name, value, entries) in choices) choice(name, value, Optional(entries))
        }
    }

    override fun toString(): String =
        "LongArgument(name='$name', description='${description.defaultString}', min=$min, max=$max)"
}

context(Cmd)
        public fun <Cmd> long(
    name: String? = null,
    description: LocalizedString,
    min: Long? = null,
    max: Long? = null,
    autoComplete: AutoCompleteAction? = null,
): ArgumentDelegateProvider<Cmd, Long, LongArgument>
        where Cmd : CustomizableCommand,
              Cmd : Command<*> = createDelegate(name) { LongArgument(it, description, min, max, autoComplete) }