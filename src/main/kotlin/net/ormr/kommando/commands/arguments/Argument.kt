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
import net.ormr.kommando.localization.LocalizationResolver
import net.ormr.kommando.localization.LocalizedString

public sealed class Argument<Value, ArgValue : Any> {
    public abstract val name: String
    public abstract val description: LocalizedString
    public abstract val type: ArgumentType<ArgValue>

    public open fun getValue(
        command: InteractionCommand,
        event: ChatInputCommandInteractionCreateEvent,
    ): Value = getValue(type.getValue(command, name))

    public open fun getValueOrNull(
        command: InteractionCommand,
        event: ChatInputCommandInteractionCreateEvent,
    ): Value? = getValueOrNull(type.getValueOrNull(command, name))

    protected abstract fun getValue(value: ArgValue): Value

    protected abstract fun getValueOrNull(value: ArgValue?): Value?

    // TODO: pass this along some way of resolving localization
    public abstract fun BaseInputChatBuilder.buildArgument(
        resolver: LocalizationResolver,
        isRequired: Boolean,
    )

    public abstract override fun toString(): String
}