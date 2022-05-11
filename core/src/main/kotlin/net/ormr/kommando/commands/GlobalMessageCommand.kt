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
import dev.kord.core.entity.interaction.MessageCommandInteraction
import dev.kord.core.event.interaction.MessageCommandInteractionCreateEvent
import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.commands.arguments.slash.MentionableSlashArgument
import net.ormr.kommando.commands.permissions.ApplicationCommandPermissions
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

private typealias GlobalMessageEvent = MessageCommandInteractionCreateEvent

public data class GlobalMessageCommand(
    override val category: String,
    override val name: String,
    override val defaultApplicationPermission: Boolean,
    override val applicationPermissions: ApplicationCommandPermissions?,
    override val executor: ContextCommandExecutor<Message, GlobalMessageEvent, GlobalMessageCommandData>,
) : ContextCommand<Message, GlobalMessageEvent, GlobalMessageCommandData>, GlobalApplicationCommand

public data class GlobalMessageCommandData(
    override val kommando: Kommando,
    override val event: GlobalMessageEvent,
) : SlashCommandData<GlobalMessageEvent, MessageCommandInteraction> {
    override val interaction: MessageCommandInteraction
        get() = event.interaction
}

@KommandoDsl
public class GlobalMessageCommandBuilder @PublishedApi internal constructor(private val name: String) :
    ContextCommandBuilder<GlobalMessageCommand, Message, GlobalMessageEvent, GlobalMessageCommandData>() {
    override fun getEmptyArgument(): MentionableSlashArgument = MentionableSlashArgument("", "")

    @PublishedApi
    override fun build(category: String): GlobalMessageCommand = GlobalMessageCommand(
        category = category,
        name = name,
        defaultApplicationPermission = defaultApplicationPermission,
        applicationPermissions = applicationPermissions,
        executor = getNonNullExecutor(),
    )
}

@KommandoDsl
public inline fun CommandContainerBuilder.globalMessageCommand(
    name: String,
    builder: GlobalMessageCommandBuilder.() -> Unit,
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    addCommand(GlobalMessageCommandBuilder(name).apply(builder).build(category))
}