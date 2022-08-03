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
import kotlinx.coroutines.runBlocking
import net.ormr.kommando.commands.Command
import net.ormr.kommando.commands.CustomizableCommand
import net.ormr.kommando.commands.delegates.ArgumentDelegateProvider
import net.ormr.kommando.commands.delegates.createDelegate
import net.ormr.kommando.localization.LocalizationResolver
import net.ormr.kommando.localization.LocalizedString

public typealias DefaultArgumentSupplier<T> = suspend ChatInputCommandInteractionCreateEvent.() -> T

public class DefaultArgument<Value, ArgValue : Any>(
    private val argument: Argument<Value, ArgValue>,
    private val defaultSupplier: DefaultArgumentSupplier<Value>,
) : Argument<Value, ArgValue>() {
    override val name: String
        get() = argument.name

    override val description: LocalizedString
        get() = argument.description

    override val type: ArgumentType<ArgValue>
        get() = argument.type

    private fun getValueOrCreate(
        command: InteractionCommand,
        event: ChatInputCommandInteractionCreateEvent,
    ): Value = argument.getValueOrNull(command, event) ?: runBlocking { defaultSupplier(event) }

    override fun getValue(
        command: InteractionCommand,
        event: ChatInputCommandInteractionCreateEvent,
    ): Value = getValueOrCreate(command, event)

    override fun getValueOrNull(
        command: InteractionCommand,
        event: ChatInputCommandInteractionCreateEvent,
    ): Value = getValueOrCreate(command, event)

    override fun getValue(value: ArgValue): Nothing =
        throw UnsupportedOperationException("'getValue' for ArgValue should never be called by DefaultArgument")

    override fun getValueOrNull(value: ArgValue?): Nothing =
        throw UnsupportedOperationException("'getValueOrNull' for ArgValue? should never be called by DefaultArgument")

    override fun BaseInputChatBuilder.buildArgument(resolver: LocalizationResolver, isRequired: Boolean) {
        with(argument) {
            buildArgument(resolver, isRequired = false)
        }
    }

    override fun toString(): String = "Default<$argument>"
}

context(Cmd)
        public infix fun <
        Cmd,
        Value,
        ArgValue : Any,
        Arg : Argument<Value, ArgValue>,
        > ArgumentDelegateProvider<Cmd, Value, Arg>.default(
    supplier: DefaultArgumentSupplier<Value>,
): ArgumentDelegateProvider<Cmd, Value, DefaultArgument<Value, ArgValue>>
        where Cmd : CustomizableCommand,
              Cmd : Command<*> = createDelegate(name) { DefaultArgument(argumentCreator(it), supplier) }
