/*
 * Copyright 2022 Oliver Berg
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

package net.ormr.kommando.commands

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import dev.kord.core.entity.interaction.GuildUserCommandInteraction
import dev.kord.core.entity.interaction.UserCommandInteraction
import net.ormr.kommando.commands.permissions.CommandPermissions
import net.ormr.kommando.commands.permissions.GlobalCommandPermissions
import net.ormr.kommando.commands.permissions.GuildCommandPermissions

public sealed class UserCommand<out I : UserCommandInteraction, Perms : CommandPermissions>(name: String) :
    ContextCommand<I, Perms, User>(name) {
    public val user: User get() = value
}

public abstract class GlobalUserCommand(
    name: String,
) : UserCommand<UserCommandInteraction, GlobalCommandPermissions>(name), GlobalTopLevelCommand

public abstract class GuildUserCommand(
    name: String,
    override val guildId: Snowflake,
) : UserCommand<GuildUserCommandInteraction, GuildCommandPermissions>(name), GuildTopLevelCommand