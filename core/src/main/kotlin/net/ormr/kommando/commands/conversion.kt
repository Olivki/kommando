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
import net.ormr.kommando.commands.arguments.CommandExecutorArguments
import net.ormr.kommando.commands.permissions.toGuildCommandPermission

public fun GlobalSlashCommand.toGuildSlashCommand(guildId: Snowflake): GuildSlashCommand {
    val executor = this.executor?.let { executor ->
        ApplicationCommandExecutor<GuildSlashEvent, GuildSlashCommandData>(executor.arguments) {
            val data = this.toGlobalSlashCommandData()
            executor.execute(data, it as CommandExecutorArguments)
        }
    }
    return GuildSlashCommand(
        name = name,
        description = description,
        permission = permission?.toGuildCommandPermission(),
        executor = executor,
        groups = groups.mapValues { it.value.toGuildSlashCommandGroup(guildId) },
        subCommands = subCommands.mapValues { it.value.toGuildSlashSubCommand(guildId) },
        guildId = guildId,
    )
}

public fun GlobalSlashCommandGroup.toGuildSlashCommandGroup(guildId: Snowflake): GuildSlashCommandGroup =
    GuildSlashCommandGroup(
        name = name,
        description = description,
        subCommands = subCommands.mapValues { it.value.toGuildSlashSubCommand(guildId) },
        guildId = guildId,
    )

public fun GlobalSlashSubCommand.toGuildSlashSubCommand(guildId: Snowflake): GuildSlashSubCommand {
    val executor = ApplicationCommandExecutor<GuildSlashEvent, GuildSlashSubCommandData>(this.executor.arguments) {
        val data = this.toGlobalSlashSubCommandData()
        executor.execute(data, it as CommandExecutorArguments)
    }
    return GuildSlashSubCommand(
        name = name,
        description = description,
        executor = executor,
        guildId = guildId,
    )
}

public fun GlobalMessageCommand.toGuildMessageCommand(guildId: Snowflake): GuildMessageCommand {
    val executor = MessageCommandExecutor<GuildMessageEvent, GuildMessageCommandData>(this.executor.arguments) {
        val data = this.toGlobalMessageCommandData()
        executor.execute(data, it)
    }
    return GuildMessageCommand(
        name = name,
        permission = permission?.toGuildCommandPermission(),
        executor = executor,
        guildId = guildId,
    )
}

public fun GlobalUserCommand.toGuildUserCommand(guildId: Snowflake): GuildUserCommand {
    val executor = UserCommandExecutor<GuildUserEvent, GuildUserCommandData>(this.executor.arguments) {
        val data = this.toGlobalUserCommandData()
        executor.execute(data, it)
    }
    return GuildUserCommand(
        name = name,
        permission = permission?.toGuildCommandPermission(),
        executor = executor,
        guildId = guildId,
    )
}

// converting guild data to global data is trivial, the other way around is, however, not trivial

internal fun GuildSlashCommandData.toGlobalSlashCommandData(): GlobalSlashCommandData =
    GlobalSlashCommandData(kommando, event)

internal fun GuildSlashSubCommandData.toGlobalSlashSubCommandData(): GlobalSlashSubCommandData =
    GlobalSlashSubCommandData(kommando, event)

internal fun GuildMessageCommandData.toGlobalMessageCommandData(): GlobalMessageCommandData =
    GlobalMessageCommandData(kommando, event)

internal fun GuildUserCommandData.toGlobalUserCommandData(): GlobalUserCommandData =
    GlobalUserCommandData(kommando, event)
