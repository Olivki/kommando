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
import net.ormr.kommando.localization.Message
import net.ormr.kommando.localization.MessageResolver

public interface Argument<Value, ArgValue, out ArgType>
        where ArgValue : Any,
              ArgType : ArgumentType<ArgValue> {
    public val key: String
    public val name: Message
    public val description: Message
    public val type: ArgType

    public suspend fun getValue(source: InteractionCommand, event: ChatInputCommandInteractionCreateEvent): Value =
        convertArgumentValue(type.getValue(source, defaultName))

    public suspend fun getValueOrNull(
        source: InteractionCommand,
        event: ChatInputCommandInteractionCreateEvent,
    ): Value? = convertNullableArgumentValue(type.getValueOrNull(source, defaultName))

    public fun convertArgumentValue(value: ArgValue): Value

    public fun convertNullableArgumentValue(value: ArgValue?): Value?

    context(ArgumentBuildContext, BaseInputChatBuilder)
    public fun buildArgument(resolver: MessageResolver, isRequired: Boolean)
}

public inline val Argument<*, *, *>.defaultName: String
    get() = name.defaultString