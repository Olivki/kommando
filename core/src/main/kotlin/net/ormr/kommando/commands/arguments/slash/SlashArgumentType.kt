/*
 * MIT License
 *
 * Copyright (c) 2022 Oliver Berg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.ormr.kommando.commands.arguments.slash

import dev.kord.core.entity.Attachment
import dev.kord.core.entity.Entity
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.ResolvedChannel
import dev.kord.core.entity.interaction.InteractionCommand

public sealed class SlashArgumentType<T> {
    public fun getValue(command: InteractionCommand, name: String): T =
        getValueOrNull(command, name) ?: TODO("custom exception for not finding parameter with type and name")

    public abstract fun getValueOrNull(command: InteractionCommand, name: String): T?

    public object ATTACHMENT : SlashArgumentType<Attachment>() {
        override fun getValueOrNull(command: InteractionCommand, name: String): Attachment? = command.attachments[name]
    }

    public object BOOLEAN : SlashArgumentType<Boolean>() {
        override fun getValueOrNull(command: InteractionCommand, name: String): Boolean? = command.booleans[name]
    }

    public object CHANNEL : SlashArgumentType<ResolvedChannel>() {
        override fun getValueOrNull(command: InteractionCommand, name: String): ResolvedChannel? =
            command.channels[name]
    }

    public object DOUBLE : SlashArgumentType<Double>() {
        override fun getValueOrNull(command: InteractionCommand, name: String): Double? = command.numbers[name]
    }

    public object LONG : SlashArgumentType<Long>() {
        override fun getValueOrNull(command: InteractionCommand, name: String): Long? = command.integers[name]
    }

    public object MENTIONABLE : SlashArgumentType<Entity>() {
        override fun getValueOrNull(command: InteractionCommand, name: String): Entity? = command.mentionables[name]
    }

    public object ROLE : SlashArgumentType<Role>() {
        override fun getValueOrNull(command: InteractionCommand, name: String): Role? = command.roles[name]
    }

    public object STRING : SlashArgumentType<String>() {
        override fun getValueOrNull(command: InteractionCommand, name: String): String? = command.strings[name]
    }

    public object USER : SlashArgumentType<User>() {
        override fun getValueOrNull(command: InteractionCommand, name: String): User? = command.users[name]
    }
}