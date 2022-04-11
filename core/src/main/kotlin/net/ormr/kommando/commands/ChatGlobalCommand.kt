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

import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.message.MessageCreateEvent
import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.commands.arguments.chat.ChatArgument

public data class ChatGlobalCommand(
    override val category: String,
    override val name: String,
    override val description: String,
    override val executor: CommandExecutor<ChatArgument<*>, *, MessageCreateEvent, ChatGlobalCommandData>,
    override val aliases: Set<String>,
) : ChatCommand<ChatGlobalCommandData>

public data class ChatGlobalCommandData(
    override val kommando: Kommando,
    override val event: MessageCreateEvent,
) : CommandData<MessageCreateEvent> {
    public val message: Message
        get() = event.message

    public val author: User
        get() = message.author ?: error("Message author is not a user.")

    public val channel: MessageChannelBehavior
        get() = message.channel

    public suspend fun getChannel(): MessageChannel = message.getChannel()
}

@KommandoDsl
public class ChatGlobalCommandBuilder @PublishedApi internal constructor(
    private val name: String,
    private val description: String,
) : ChatCommandBuilder<ChatGlobalCommand, ChatGlobalCommandData>() {
    @PublishedApi
    override fun build(category: String): ChatGlobalCommand = ChatGlobalCommand(
        category = category,
        name = name,
        description = description,
        executor = getNonNullExecutor(),
        aliases = aliases.toSet(),
    )
}

@KommandoDsl
public inline fun CommandGroupBuilder.chatGlobalCommand(
    name: String,
    description: String,
    builder: ChatGlobalCommandBuilder.() -> Unit,
) {
    addCommand(ChatGlobalCommandBuilder(name, description).apply(builder).build(category))
}

@KommandoDsl
public inline fun CommandGroupBuilder.chatCommand(
    name: String,
    description: String,
    builder: ChatGlobalCommandBuilder.() -> Unit,
) {
    chatGlobalCommand(name, description, builder)
}