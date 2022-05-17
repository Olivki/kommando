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
import dev.kord.core.entity.Guild
import dev.kord.core.entity.User
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.commands.permissions.GuildCommandPermission
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal typealias GuildSlashEvent = GuildChatInputCommandInteractionCreateEvent
public typealias GuildSlashInteraction = GuildChatInputCommandInteraction

public data class GuildSlashCommand(
    override val name: String,
    override val description: String,
    override val permission: GuildCommandPermission?,
    override val executor: ApplicationCommandExecutor<GuildSlashEvent, GuildSlashCommandData>?,
    override val groups: Map<String, GuildSlashCommandGroup>,
    override val subCommands: Map<String, GuildSlashSubCommand>,
    override val guildId: Snowflake,
) : SlashCommand<GuildSlashEvent, GuildSlashCommandData, GuildSlashSubCommand, GuildCommandPermission>,
    TopLevelGuildApplicationCommand

public data class GuildSlashCommandData(
    override val kommando: Kommando,
    override val event: GuildSlashEvent,
) : SlashCommandData<GuildSlashEvent, GuildSlashInteraction> {
    override val interaction: GuildSlashInteraction
        get() = event.interaction

    public val user: User
        get() = interaction.user

    public suspend fun getGuild(): Guild = interaction.getGuild()
}

@KommandoDsl
public class GuildSlashCommandBuilder @PublishedApi internal constructor(
    private val name: String,
    private val description: String,
    @PublishedApi internal val guildId: Snowflake,
) : SlashCommandBuilder<GuildSlashCommand, GuildSlashSubCommand, GuildSlashCommandGroup, GuildSlashEvent, GuildSlashCommandData, GuildCommandPermission>() {
    @PublishedApi
    override fun build(): GuildSlashCommand = GuildSlashCommand(
        name = name,
        description = description,
        permission = permission,
        executor = getExecutorSafe(),
        groups = groups.toMap(),
        subCommands = subCommands,
        guildId = guildId,
    )
}

@KommandoDsl
public inline fun CommandContainerBuilder.guildSlashCommand(
    name: String,
    description: String,
    guildId: Snowflake,
    builder: GuildSlashCommandBuilder.() -> Unit,
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    addCommand(GuildSlashCommandBuilder(name, description, guildId).apply(builder).build())
}