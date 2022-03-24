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
import dev.kord.core.entity.User
import dev.kord.core.entity.interaction.GuildUserCommandInteraction
import dev.kord.core.event.interaction.GuildUserCommandInteractionCreateEvent
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.commands.arguments.CommandExecutorArguments.Args1
import net.ormr.kommando.commands.arguments.slash.SlashArgument
import net.ormr.kommando.commands.arguments.slash.SlashUserArgument

private typealias GuildUserEvent = GuildUserCommandInteractionCreateEvent

public data class GuildUserCommand(
    override val category: String,
    override val name: String,
    override val executor: CommandExecutor<SlashArgument<*>, Args1<User>, GuildUserEvent, GuildUserCommandData>,
    public val guildId: Snowflake,
) : ApplicationCommand<GuildUserEvent, GuildUserCommandData>

public data class GuildUserCommandData(
    override val kord: Kord,
    override val event: GuildUserEvent,
) : SlashCommandData<GuildUserEvent, GuildUserCommandInteraction> {
    override val interaction: GuildUserCommandInteraction
        get() = event.interaction
}

@KommandoDsl
public fun CommandGroupBuilder.guildUserCommand(
    name: String,
    guildId: Snowflake,
    execute: suspend GuildUserCommandData.(Args1<User>) -> Unit,
) {
    val executor = CommandExecutor(listOf(SlashUserArgument("", "")), execute)
    addCommand(GuildUserCommand(category, name, executor, guildId))
}