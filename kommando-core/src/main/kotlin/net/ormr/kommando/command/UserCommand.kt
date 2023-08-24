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
import net.ormr.kommando.command.permission.CommandPermissions
import net.ormr.kommando.command.permission.GlobalCommandPermissions
import net.ormr.kommando.command.permission.GuildCommandPermissions
import net.ormr.kommando.kord.UserEntity

public sealed class UserCommand<Context, Perms>(
    defaultName: String,
) : AbstractContextMenuCommand<Context, Perms, UserEntity>(defaultName)
        where Context : CommandContext<*>,
              Perms : CommandPermissions {
    public val user: UserEntity get() = value
}

public abstract class GuildUserCommand(
    defaultName: String,
    override val commandGuildId: Snowflake,
) : UserCommand<GuildCommandContext, GuildCommandPermissions>(defaultName), GuildTopLevelCommand

public abstract class GlobalUserCommand(
    defaultName: String,
) : UserCommand<GlobalCommandContext, GlobalCommandPermissions>(defaultName), GlobalTopLevelCommand