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

import net.ormr.kommando.command.permission.GlobalCommandPermissions

public sealed interface GlobalChatInputCommand : GlobalCommandType, ChatInputCommand<GlobalCommandContext>

public abstract class GlobalCommand(
    name: String,
    description: String,
) : AbstractSuperCommand<GlobalCommandContext, GlobalCommandPermissions>(name, description), GlobalRootCommand,
    GlobalRootChatInputCommand, GlobalChatInputCommand {
    context(GlobalCommandContext)
    override suspend fun execute() {
        error("Command ${this::class.qualifiedName} should never be executed")
    }
}

public abstract class GlobalSubCommand<out Super : GlobalCommand>(
    name: String,
    description: String,
) : AbstractSubCommand<GlobalCommandContext, Super>(name, description), GlobalCommandType, GlobalChatInputCommand