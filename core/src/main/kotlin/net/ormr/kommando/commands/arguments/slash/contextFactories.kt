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

@file:Suppress("NOTHING_TO_INLINE", "unused")

package net.ormr.kommando.commands.arguments.slash

import net.ormr.kommando.commands.ApplicationCommandBuilder
import net.ormr.kommando.commands.arguments.EnumChoiceArgumentAdapter

private typealias SlashContext = ApplicationCommandBuilder<*, *, *>

// TODO: replace receiver parameter with context receiver once they're actually usable in a library

public inline fun SlashContext.attachment(name: String, description: String): AttachmentSlashArgument =
    AttachmentSlashArgument(name, description)

public inline fun SlashContext.boolean(name: String, description: String): BooleanSlashArgument =
    BooleanSlashArgument(name, description)

public fun SlashContext.channel(name: String, description: String): ChannelSlashArgument =
    ChannelSlashArgument(name, description)

public inline fun SlashContext.double(name: String, description: String): DoubleSlashArgument =
    DoubleSlashArgument(name, description)

public inline fun <reified T> SlashContext.enumChoice(name: String, description: String): EnumChoiceSlashArgument<T>
        where T : Enum<T>,
              T : EnumChoiceArgumentAdapter = EnumChoiceSlashArgument(name, description)

public inline fun SlashContext.long(name: String, description: String): LongSlashArgument =
    LongSlashArgument(name, description)

public inline fun SlashContext.mentionable(name: String, description: String): MentionableSlashArgument =
    MentionableSlashArgument(name, description)

public inline fun SlashContext.role(name: String, description: String): RoleSlashArgument =
    RoleSlashArgument(name, description)

public inline fun SlashContext.string(name: String, description: String): StringSlashArgument =
    StringSlashArgument(name, description)

public inline fun SlashContext.user(name: String, description: String): UserSlashArgument =
    UserSlashArgument(name, description)