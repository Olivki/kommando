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
import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.entity.User
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.commands.arguments.slash.SlashArgument

private typealias GuildSlashEvent = GuildChatInputCommandInteractionCreateEvent

public data class GuildSlashCommand(
    override val category: String,
    override val name: String,
    override val description: String,
    override val executor: CommandExecutor<SlashArgument<*>, *, GuildSlashEvent, GuildSlashCommandData>,
    public val guildId: Snowflake,
) : SlashCommand<GuildSlashEvent, GuildSlashCommandData>

public data class GuildSlashCommandData(
    override val kord: Kord,
    override val event: GuildSlashEvent,
) : SlashCommandData<GuildSlashEvent, GuildChatInputCommandInteraction> {
    override val interaction: GuildChatInputCommandInteraction
        get() = event.interaction

    public val user: User
        get() = interaction.user

    public suspend fun getGuild(): Guild = interaction.getGuild()
}

@KommandoDsl
public class GuildSlashCommandBuilder @PublishedApi internal constructor(
    private val name: String,
    private val description: String,
    private val guildId: Snowflake,
) : CommandBuilder<GuildSlashCommand, SlashArgument<*>, GuildSlashEvent, GuildSlashCommandData>() {
    @PublishedApi
    override fun build(category: String): GuildSlashCommand = GuildSlashCommand(
        category = category,
        name = name,
        description = description,
        executor = getExecutor(),
        guildId = guildId,
    )
}

@KommandoDsl
public inline fun CommandGroupBuilder.guildSlashCommand(
    name: String,
    description: String,
    guildId: Snowflake,
    builder: GuildSlashCommandBuilder.() -> Unit,
) {
    addCommand(GuildSlashCommandBuilder(name, description, guildId).apply(builder).build(category))
}