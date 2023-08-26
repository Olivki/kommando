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

public class DefaultArgument<Value, ArgValue, out ArgType>(
    private val delegate: Argument<Value, ArgValue, ArgType>,
    private val supplier: DefaultArgumentSupplier<Value>,
) : Argument<Value, ArgValue, ArgType> by delegate
        where ArgValue : Any,
              ArgType : ArgumentType<ArgValue> {
    private suspend fun getValueOrCreate(
        source: InteractionCommand,
        event: ChatInputCommandInteractionCreateEvent,
    ): Value = delegate.getValueOrNull(source, event) ?: with(event) { supplier.getValue() }

    override suspend fun getValue(
        source: InteractionCommand,
        event: ChatInputCommandInteractionCreateEvent,
    ): Value = getValueOrCreate(source, event)

    override suspend fun getValueOrNull(
        source: InteractionCommand,
        event: ChatInputCommandInteractionCreateEvent,
    ): Value? = getValueOrCreate(source, event)

    override fun convertArgumentValue(value: ArgValue): Nothing = noConversionSupport()

    override fun convertNullableArgumentValue(value: ArgValue?): Nothing = noNullableConversionSupport()

    context(ArgumentBuildContext, BaseInputChatBuilder)
    override fun buildArgument(isRequired: Boolean) {
        delegate.buildArgument(isRequired = false)
    }

    override fun toString(): String = "Default<$delegate>"
}

public fun interface DefaultArgumentSupplier<Value> {
    context(ChatInputCommandInteractionCreateEvent)
    public suspend fun getValue(): Value
}

/**
 * Returns a new argument that will use the [supplier] to get the value if the user did not provide one.
 */
context(CustomizableCommand<*>)
public infix fun <Value, ArgValue, Arg> ArgumentBuilder<Value, Arg>.default(
    supplier: DefaultArgumentSupplier<Value>,
): ArgumentBuilder<Value, DefaultArgument<Value, ArgValue, *>>
        where ArgValue : Any,
              Arg : Argument<Value, ArgValue, *> =
    ArgumentHelper.newBuilder(name, description) { key, name, desc ->
        DefaultArgument(createArgument(key, name, desc), supplier)
    }