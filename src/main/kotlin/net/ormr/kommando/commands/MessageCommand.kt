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
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.GuildMessageCommandInteraction
import dev.kord.core.entity.interaction.MessageCommandInteraction
import net.ormr.kommando.commands.permissions.CommandPermissions
import net.ormr.kommando.commands.permissions.GlobalCommandPermissions
import net.ormr.kommando.commands.permissions.GuildCommandPermissions

public sealed class MessageCommand<out I : MessageCommandInteraction, Perms : CommandPermissions>(name: String) :
    ContextCommand<I, Perms, Message>(name) {
    // TODO: better name?
    public val message: Message get() = value
}

public abstract class GlobalMessageCommand(
    name: String,
) : MessageCommand<MessageCommandInteraction, GlobalCommandPermissions>(name), GlobalTopLevelCommand

public abstract class GuildMessageCommand(
    name: String,
    override val guildId: Snowflake,
) : MessageCommand<GuildMessageCommandInteraction, GuildCommandPermissions>(name), GuildTopLevelCommand