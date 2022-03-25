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

import dev.kord.core.event.Event
import dev.kord.core.event.message.MessageCreateEvent
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.commands.arguments.CommandExecutorArguments.*
import net.ormr.kommando.commands.arguments.chat.ChatArgument
import net.ormr.kommando.commands.arguments.slash.SlashArgument

// -- CHAT COMMANDS -- \\
@KommandoDsl
@JvmName("chatCommandExecute")
public fun <D : CommandData<MessageCreateEvent>> CommandBuilder<ChatCommand<D>, ChatArgument<*>, MessageCreateEvent, D>.execute(
    execute: suspend D.(Args0) -> Unit,
) {
    registerExecutor(CommandExecutor(listOf(), execute))
}

@KommandoDsl
public fun <D : CommandData<MessageCreateEvent>, N1> CommandBuilder<ChatCommand<D>, ChatArgument<*>, MessageCreateEvent, D>.execute(
    n1: ChatArgument<N1>,
    execute: suspend D.(Args1<N1>) -> Unit,
) {
    registerExecutor(CommandExecutor(listOf(n1), execute))
}

@KommandoDsl
public fun <D : CommandData<MessageCreateEvent>, N1, N2> CommandBuilder<ChatCommand<D>, ChatArgument<*>, MessageCreateEvent, D>.execute(
    n1: ChatArgument<N1>,
    n2: ChatArgument<N2>,
    execute: suspend D.(Args2<N1, N2>) -> Unit,
) {
    registerExecutor(CommandExecutor(listOf(n1, n2), execute))
}

@KommandoDsl
public fun <D : CommandData<MessageCreateEvent>, N1, N2, N3> CommandBuilder<ChatCommand<D>, ChatArgument<*>, MessageCreateEvent, D>.execute(
    n1: ChatArgument<N1>,
    n2: ChatArgument<N2>,
    n3: ChatArgument<N3>,
    execute: suspend D.(Args3<N1, N2, N3>) -> Unit,
) {
    registerExecutor(CommandExecutor(listOf(n1, n2, n3), execute))
}

@KommandoDsl
public fun <D : CommandData<MessageCreateEvent>, N1, N2, N3, N4> CommandBuilder<ChatCommand<D>, ChatArgument<*>, MessageCreateEvent, D>.execute(
    n1: ChatArgument<N1>,
    n2: ChatArgument<N2>,
    n3: ChatArgument<N3>,
    n4: ChatArgument<N4>,
    execute: suspend D.(Args4<N1, N2, N3, N4>) -> Unit,
) {
    registerExecutor(CommandExecutor(listOf(n1, n2, n3, n4), execute))
}

@KommandoDsl
public fun <D : CommandData<MessageCreateEvent>, N1, N2, N3, N4, N5> CommandBuilder<ChatCommand<D>, ChatArgument<*>, MessageCreateEvent, D>.execute(
    n1: ChatArgument<N1>,
    n2: ChatArgument<N2>,
    n3: ChatArgument<N3>,
    n4: ChatArgument<N4>,
    n5: ChatArgument<N5>,
    execute: suspend D.(Args5<N1, N2, N3, N4, N5>) -> Unit,
) {
    registerExecutor(CommandExecutor(listOf(n1, n2, n3, n4, n5), execute))
}

// -- SLASH COMMANDS -- \\
// TODO: verify that non optional commands are placed before optional commands
@KommandoDsl
public fun <E : Event, D : CommandData<E>> CommandBuilder<ApplicationCommand<E, D>, SlashArgument<*>, E, D>.execute(
    execute: suspend D.(Args0) -> Unit,
) {
    registerExecutor(CommandExecutor(listOf(), execute))
}

@KommandoDsl
public fun <E : Event, D : CommandData<E>, N1> CommandBuilder<ApplicationCommand<E, D>, SlashArgument<*>, E, D>.execute(
    n1: SlashArgument<N1>,
    execute: suspend D.(Args1<N1>) -> Unit,
) {
    registerExecutor(CommandExecutor(listOf(n1), execute))
}

@KommandoDsl
public fun <E : Event, D : CommandData<E>, N1, N2> CommandBuilder<ApplicationCommand<E, D>, SlashArgument<*>, E, D>.execute(
    n1: SlashArgument<N1>,
    n2: SlashArgument<N2>,
    execute: suspend D.(Args2<N1, N2>) -> Unit,
) {
    registerExecutor(CommandExecutor(listOf(n1, n2), execute))
}

@KommandoDsl
public fun <E : Event, D : CommandData<E>, N1, N2, N3> CommandBuilder<ApplicationCommand<E, D>, SlashArgument<*>, E, D>.execute(
    n1: SlashArgument<N1>,
    n2: SlashArgument<N2>,
    n3: SlashArgument<N3>,
    execute: suspend D.(Args3<N1, N2, N3>) -> Unit,
) {
    registerExecutor(CommandExecutor(listOf(n1, n2, n3), execute))
}

@KommandoDsl
public fun <E : Event, D : CommandData<E>, N1, N2, N3, N4> CommandBuilder<ApplicationCommand<E, D>, SlashArgument<*>, E, D>.execute(
    n1: SlashArgument<N1>,
    n2: SlashArgument<N2>,
    n3: SlashArgument<N3>,
    n4: SlashArgument<N4>,
    execute: suspend D.(Args4<N1, N2, N3, N4>) -> Unit,
) {
    registerExecutor(CommandExecutor(listOf(n1, n2, n3, n4), execute))
}

@KommandoDsl
public fun <E : Event, D : CommandData<E>, N1, N2, N3, N4, N5> CommandBuilder<ApplicationCommand<E, D>, SlashArgument<*>, E, D>.execute(
    n1: SlashArgument<N1>,
    n2: SlashArgument<N2>,
    n3: SlashArgument<N3>,
    n4: SlashArgument<N4>,
    n5: SlashArgument<N5>,
    execute: suspend D.(Args5<N1, N2, N3, N4, N5>) -> Unit,
) {
    registerExecutor(CommandExecutor(listOf(n1, n2, n3, n4, n5), execute))
}