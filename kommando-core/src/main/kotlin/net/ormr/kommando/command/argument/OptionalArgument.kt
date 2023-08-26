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

import dev.kord.core.entity.interaction.InteractionCommand
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import net.ormr.kommando.command.CustomizableCommand
import net.ormr.kommando.localization.Message

public class OptionalArgument<Value, ArgValue, out ArgType>(
    private val delegate: Argument<Value, ArgValue, ArgType>,
) : Argument<Value?, ArgValue, ArgType> // can't delegate because 'Value?' != 'Value'
        where ArgValue : Any,
              ArgType : ArgumentType<ArgValue> {
    override val key: String
        get() = delegate.key

    override val name: Message
        get() = delegate.name

    override val description: Message
        get() = delegate.description

    override val type: ArgType
        get() = delegate.type

    override suspend fun getValue(
        source: InteractionCommand,
        event: ChatInputCommandInteractionCreateEvent,
    ): Value? = delegate.getValueOrNull(source, event)

    override suspend fun getValueOrNull(
        source: InteractionCommand,
        event: ChatInputCommandInteractionCreateEvent,
    ): Value? = delegate.getValueOrNull(source, event)

    override fun convertArgumentValue(value: ArgValue): Nothing = noConversionSupport()

    override fun convertNullableArgumentValue(value: ArgValue?): Nothing = noNullableConversionSupport()

    context(ArgumentBuildContext, BaseInputChatBuilder)
    override fun buildArgument(isRequired: Boolean) {
        delegate.buildArgument(isRequired = false)
    }

    override fun toString(): String = "Optional<$delegate>"
}

/**
 * Returns a new argument that will return `null` if the user did not provide a value.
 */
context(Cmd)
public fun <Cmd, Value, ArgValue, Arg> ArgumentBuilder<Cmd, Value, Arg>.optional(): ArgumentBuilder<Cmd, Value?, OptionalArgument<Value, ArgValue, *>>
        where Value : Any,
              ArgValue : Any,
              Cmd : CustomizableCommand<*>,
              Arg : Argument<Value, ArgValue, *> =
    ArgumentHelper.newBuilder(name, description) { key, name, desc ->
        OptionalArgument(createArgument(key, name, desc))
    }