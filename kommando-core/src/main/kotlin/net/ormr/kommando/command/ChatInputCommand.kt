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

import net.ormr.kommando.command.permission.CommandPermissions
import net.ormr.kommando.command.permission.GlobalCommandPermissions
import net.ormr.kommando.command.permission.GuildCommandPermissions

public sealed interface ChatInputCommand<Context> : Command<Context>
        where Context : CommandContext<*>

public sealed interface RootChatInputCommand<Context, Perms> : ChatInputCommand<Context>, RootCommand<Context, Perms>
        where Context : CommandContext<*>,
              Perms : CommandPermissions

public sealed interface GuildRootChatInputCommand : RootChatInputCommand<GuildCommandContext, GuildCommandPermissions>

public sealed interface GlobalRootChatInputCommand :
    RootChatInputCommand<GlobalCommandContext, GlobalCommandPermissions>