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

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.GuildMessageCommandInteraction
import dev.kord.core.event.interaction.GuildMessageCommandInteractionCreateEvent
import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.commands.arguments.slash.MentionableSlashArgument
import net.ormr.kommando.commands.permissions.GuildCommandPermission
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

private typealias GuildMessageEvent = GuildMessageCommandInteractionCreateEvent

public data class GuildMessageCommand(
    override val name: String,
    override val permission: GuildCommandPermission?,
    override val executor: MessageCommandExecutor<GuildMessageEvent, GuildMessageCommandData>,
    override val guildId: Snowflake,
) : ContextCommand<Message, GuildMessageEvent, GuildMessageCommandData, GuildCommandPermission>,
    TopLevelGuildApplicationCommand

public data class GuildMessageCommandData(
    override val kommando: Kommando,
    override val event: GuildMessageEvent,
) : SlashCommandData<GuildMessageEvent, GuildMessageCommandInteraction> {
    override val interaction: GuildMessageCommandInteraction
        get() = event.interaction
}

@KommandoDsl
public class GuildMessageCommandBuilder @PublishedApi internal constructor(
    private val name: String,
    private val guildId: Snowflake,
) : ContextCommandBuilder<GuildMessageCommand, Message, GuildMessageEvent, GuildMessageCommandData, GuildCommandPermission>() {
    override fun getEmptyArgument(): MentionableSlashArgument = MentionableSlashArgument("", "")

    @PublishedApi
    override fun build(): GuildMessageCommand = GuildMessageCommand(
        name = name,
        permission = permission,
        executor = getNonNullExecutor(),
        guildId = guildId,
    )
}

@KommandoDsl
public inline fun CommandContainerBuilder.guildMessageCommand(
    name: String,
    guildId: Snowflake,
    builder: GuildMessageCommandBuilder.() -> Unit,
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    addCommand(GuildMessageCommandBuilder(name, guildId).apply(builder).build())
}