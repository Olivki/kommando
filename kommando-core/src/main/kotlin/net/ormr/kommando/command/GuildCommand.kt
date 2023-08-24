/*
 * Copyright 2023 Oliver Berg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ormr.kommando.command

import dev.kord.common.entity.Snowflake
import net.ormr.kommando.command.permission.GuildCommandPermissions

public sealed interface GuildChatInputCommand : ChatInputCommand<GuildCommandContext>, GuildCommandType

public sealed interface GuildInheritableCommandComponent : InheritableCommandComponent

public abstract class GuildCommand(
    name: String,
    description: String,
) : AbstractRootCommand<GuildCommandContext, GuildCommandPermissions>(name, description), GuildTopLevelCommand,
    GuildTopLevelChatInputCommand, GuildChatInputCommand, GuildInheritableCommandComponent {
    context(GuildCommandContext)
    override suspend fun execute() {
        error("Command ${this::class.qualifiedName} should never be executed")
    }
}

public abstract class GuildSubCommand<out Root>(
    name: String,
    description: String,
) : AbstractSubCommand<GuildCommandContext, Root>(name, description), GuildCommandType, GuildChatInputCommand
        where Root : GuildInheritableCommandComponent {
    final override val commandGuildId: Snowflake
        get() = findRoot().commandGuildId
}

private fun GuildSubCommand<*>.findRoot(): GuildTopLevelCommand {
    var current: InheritableCommandComponent = rootComponent
    while (current is CommandGroup<*>) {
        current = current.rootCommand
    }
    require(current is GuildTopLevelCommand) { "Could not find root for $this" }
    return current
}