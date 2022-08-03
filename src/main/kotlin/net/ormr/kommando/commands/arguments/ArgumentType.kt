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

import dev.kord.core.entity.Attachment
import dev.kord.core.entity.Entity
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.ResolvedChannel
import dev.kord.core.entity.interaction.InteractionCommand

public sealed class ArgumentType<T : Any> {
    public val name: String by lazy { javaClass.simpleName }

    public fun getValue(
        command: InteractionCommand,
        name: String,
    ): T = getValueOrNull(command, name) ?: throw NoSuchArgumentWithTypeException(name, this)

    public abstract fun getValueOrNull(command: InteractionCommand, name: String): T?

    public object ATTACHMENT : ArgumentType<Attachment>() {
        override fun getValueOrNull(command: InteractionCommand, name: String): Attachment? = command.attachments[name]
    }

    public object BOOLEAN : ArgumentType<Boolean>() {
        override fun getValueOrNull(command: InteractionCommand, name: String): Boolean? = command.booleans[name]
    }

    public object CHANNEL : ArgumentType<ResolvedChannel>() {
        override fun getValueOrNull(command: InteractionCommand, name: String): ResolvedChannel? =
            command.channels[name]
    }

    public object NUMBER : ChoiceArgumentType<Double>() {
        override fun getValueOrNull(command: InteractionCommand, name: String): Double? = command.numbers[name]
    }

    public object INTEGER : ChoiceArgumentType<Long>() {
        override fun getValueOrNull(command: InteractionCommand, name: String): Long? = command.integers[name]
    }

    public object MENTIONABLE : ArgumentType<Entity>() {
        override fun getValueOrNull(command: InteractionCommand, name: String): Entity? = command.mentionables[name]
    }

    public object ROLE : ArgumentType<Role>() {
        override fun getValueOrNull(command: InteractionCommand, name: String): Role? = command.roles[name]
    }

    public object STRING : ChoiceArgumentType<String>() {
        override fun getValueOrNull(command: InteractionCommand, name: String): String? = command.strings[name]
    }

    public object USER : ArgumentType<User>() {
        override fun getValueOrNull(command: InteractionCommand, name: String): User? = command.users[name]
    }


}

public sealed class ChoiceArgumentType<T : Any> : ArgumentType<T>()