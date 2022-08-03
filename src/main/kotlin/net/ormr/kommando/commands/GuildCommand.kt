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
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import net.ormr.kommando.commands.permissions.GuildCommandPermissions
import kotlin.reflect.KClass

public typealias GuildCommandInteraction = GuildChatInputCommandInteraction

public sealed interface GuildApplicationCommand : GuildCentricCommand

public abstract class GuildCommand(
    name: String,
    override val guildId: Snowflake,
) : SuperCommand<GuildCommand, GuildCommandInteraction, GuildSubCommand, GuildCommandPermissions>(name),
    GuildApplicationCommand, GuildTopLevelCommand

public abstract class GuildSubCommand(
    parent: KClass<out GuildCommand>,
    name: String,
) : SubCommand<GuildSubCommand, GuildCommandInteraction, GuildCommand>(parent, name), GuildApplicationCommand {
    final override val guildId: Snowflake
        get() = parent.guildId
}