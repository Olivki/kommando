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
import dev.kord.core.entity.channel.DmChannel
import dev.kord.core.event.message.MessageCreateEvent
import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoDsl

public data class ChatDmCommand(
    override val category: String,
    override val name: String,
    override val description: String,
    override val executor: ChatCommandExecutor<ChatDmCommandData>,
    override val aliases: Set<String>,
) : ChatCommand<ChatDmCommandData>

public data class ChatDmCommandData(
    override val kommando: Kommando,
    override val event: MessageCreateEvent,
) : ChatCommandData {
    public val message: Message
        get() = event.message

    public val author: User
        get() = message.author ?: error("Message author is not a user.")

    public val channel: MessageChannelBehavior
        get() = message.channel

    public suspend fun getChannel(): DmChannel =
        message.getChannel() as? DmChannel ?: error("Channel is not DM channel.")
}

@KommandoDsl
public class ChatDmCommandBuilder @PublishedApi internal constructor(
    private val name: String,
    private val description: String,
) : ChatCommandBuilder<ChatDmCommand, ChatDmCommandData>() {
    @PublishedApi
    override fun build(category: String): ChatDmCommand = ChatDmCommand(
        category = category,
        name = name,
        description = description,
        executor = getNonNullExecutor(),
        aliases = aliases.toSet(),
    )
}

@KommandoDsl
public inline fun CommandGroupBuilder.chatDmCommand(
    name: String,
    description: String,
    builder: ChatDmCommandBuilder.() -> Unit,
) {
    addCommand(ChatDmCommandBuilder(name, description).apply(builder).build(category))
}