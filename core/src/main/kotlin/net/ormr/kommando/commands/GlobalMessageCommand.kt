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

import dev.kord.core.Kord
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.MessageCommandInteraction
import dev.kord.core.event.interaction.MessageCommandInteractionCreateEvent
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.commands.arguments.CommandExecutorArguments
import net.ormr.kommando.commands.arguments.slash.SlashArgument
import net.ormr.kommando.commands.arguments.slash.SlashMentionableArgument

private typealias GlobalMessageEvent = MessageCommandInteractionCreateEvent

public data class GlobalMessageCommand(
    override val category: String,
    override val name: String,
    override val executor: CommandExecutor<SlashArgument<*>, CommandExecutorArguments.Args1<Message>, GlobalMessageEvent, GlobalMessageCommandData>,
) : ApplicationCommand<GlobalMessageEvent, GlobalMessageCommandData>

public data class GlobalMessageCommandData(
    override val kord: Kord,
    override val event: GlobalMessageEvent,
) : SlashCommandData<GlobalMessageEvent, MessageCommandInteraction> {
    override val interaction: MessageCommandInteraction
        get() = event.interaction
}

@KommandoDsl
public fun CommandGroupBuilder.globalMessageCommand(
    name: String,
    execute: suspend GlobalMessageCommandData.(CommandExecutorArguments.Args1<Message>) -> Unit,
) {
    val executor = CommandExecutor(listOf(SlashMentionableArgument("", "")), execute)
    addCommand(GlobalMessageCommand(category, name, executor))
}