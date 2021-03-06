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

package net.ormr.kommando.commands

import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.event.Event
import dev.kord.core.event.message.MessageCreateEvent
import net.ormr.kommando.commands.arguments.CommandArgument
import net.ormr.kommando.commands.arguments.CommandExecutorArguments
import net.ormr.kommando.commands.arguments.CommandExecutorArguments.Args1
import net.ormr.kommando.commands.arguments.chat.ChatArgument
import net.ormr.kommando.commands.arguments.slash.SlashArgument

public class CommandExecutor<out A : CommandArgument<*>, EA : CommandExecutorArguments, E : Event, D : CommandData<E>>(
    public val arguments: List<A>,
    internal val executable: suspend D.(EA) -> Unit,
) {
    @Suppress("UNCHECKED_CAST")
    internal suspend fun execute(data: D, arguments: CommandExecutorArguments) {
        executable(data, arguments as EA)
    }
}

public typealias ApplicationCommandExecutor<E, D> = CommandExecutor<SlashArgument<*>, *, E, D>
public typealias ContextCommandExecutor<T, E, D> = CommandExecutor<SlashArgument<*>, Args1<T>, E, D>
// we can't do 'ContextCommandExecutor<Message, E, D>' as for some reason that causes the typealias to be expanded
// incorrectly in functions, while it works like it should on class properties.
// For example, trying to use 'MessageCommandExecutor<GuildMessageEvent, GuildMessageCommandData>' would result in a
// type arguments out of bounds error, stating that 'GuildMessageEvent' was not a 'CommandExecutorArguments'.
// The same behavior applies to 'UserCommandExecutor'.
public typealias MessageCommandExecutor<E, D> = CommandExecutor<SlashArgument<*>, Args1<Message>, E, D>
public typealias UserCommandExecutor<E, D> = CommandExecutor<SlashArgument<*>, Args1<User>, E, D>
public typealias ChatCommandExecutor<D> = CommandExecutor<ChatArgument<*>, *, MessageCreateEvent, D>