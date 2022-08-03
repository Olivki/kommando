/*
 * Copyright 2022 Oliver Berg
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

import dev.kord.core.entity.interaction.InteractionCommand
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import net.ormr.kommando.commands.Command
import net.ormr.kommando.commands.CustomizableCommand
import net.ormr.kommando.commands.delegates.ArgumentDelegateProvider
import net.ormr.kommando.commands.delegates.createDelegate
import net.ormr.kommando.localization.LocalizationResolver
import net.ormr.kommando.localization.LocalizedString

public class ChoiceArgument<Value : Any, ArgValue : Any, ArgType : ChoiceArgumentType<ArgValue>>(
    private val argument: ArgumentWithChoice<Value, ArgValue, ArgType>,
    choices: List<ArgumentChoice<Value>>,
) : Argument<Value, ArgValue>() { // sadly only interfaces can be delegated to
    private val choices: List<ArgumentChoice<ArgValue>> =
        // 'copy' on data classes doesn't change the returned generics, so we can't use it for this
        choices.map { ArgumentChoice(it.name, argument.convertChoiceValue(it.value), it.localizedEntries) }

    override val name: String
        get() = argument.name

    override val description: LocalizedString
        get() = argument.description

    override val type: ArgumentType<ArgValue>
        get() = argument.type

    override fun getValue(
        command: InteractionCommand,
        event: ChatInputCommandInteractionCreateEvent,
    ): Value = argument.getValue(command, event)

    override fun getValueOrNull(
        command: InteractionCommand,
        event: ChatInputCommandInteractionCreateEvent,
    ): Value? = argument.getValueOrNull(command, event)

    override fun getValue(value: ArgValue): Nothing =
        throw UnsupportedOperationException("'getValue' for ArgValue should never be called by ChoiceArgument")

    override fun getValueOrNull(value: ArgValue?): Nothing =
        throw UnsupportedOperationException("'getValueOrNull' for ArgValue? should never be called by ChoiceArgument")

    override fun BaseInputChatBuilder.buildArgument(resolver: LocalizationResolver, isRequired: Boolean) {
        with(argument) {
            buildArgumentWithChoices(resolver, choices, isRequired)
        }
    }

    override fun toString(): String = "WithChoices<$argument, $choices>"
}

context(Cmd)
        public fun <
        Cmd,
        Value : Any,
        ArgValue : Any,
        ArgType : ChoiceArgumentType<ArgValue>,
        Arg : ArgumentWithChoice<Value, ArgValue, ArgType>,
        > ArgumentDelegateProvider<Cmd, Value, Arg>.choices(
    choice: ArgumentChoice<Value>,
    vararg moreChoices: ArgumentChoice<Value>,
): ArgumentDelegateProvider<Cmd, Value, ChoiceArgument<Value, ArgValue, ArgType>>
        where Cmd : CustomizableCommand,
              Cmd : Command<*> {
    val choices = buildList(moreChoices.size + 1) {
        add(choice)
        addAll(moreChoices)
    }
    return createDelegate(name) { ChoiceArgument(argumentCreator(it), choices) }
}
