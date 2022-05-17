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

package net.ormr.kommando.internal

import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.interaction.*
import net.ormr.kommando.KommandoBuilder
import net.ormr.kommando.commands.*
import net.ormr.kommando.commands.arguments.slash.SlashArgument
import net.ormr.kommando.commands.permissions.GlobalCommandPermission
import net.ormr.kommando.commands.permissions.GuildCommandPermission

// TODO: this gets relatively slow, but only for guild commands?
internal suspend fun KommandoBuilder.registerSlashCommands(
    applicationCommands: List<TopLevelApplicationCommand<*, *, *>>,
): Map<Snowflake, TopLevelApplicationCommand<*, *, *>> = buildMap {
    for (command in applicationCommands) {
        when (command) {
            is GlobalSlashCommand -> {
                val registeredCommand = kord.createGlobalChatInputCommand(
                    command.name,
                    command.description,
                ) {
                    applyPermissions(command)
                    buildCommand(command)
                }
                put(registeredCommand.id, command)
            }
            is GlobalUserCommand -> {
                val registeredCommand = kord.createGlobalUserCommand(command.name) {
                    applyPermissions(command)
                }
                put(registeredCommand.id, command)
            }
            is GlobalMessageCommand -> {
                val registeredCommand = kord.createGlobalMessageCommand(command.name) {
                    applyPermissions(command)
                }
                put(registeredCommand.id, command)
            }
            is GuildSlashCommand -> {
                val registeredCommand = kord.createGuildChatInputCommand(
                    command.guildId,
                    command.name,
                    command.description,
                ) {
                    applyPermissions(command)
                    buildCommand(command)
                }
                put(registeredCommand.id, command)
            }
            is GuildUserCommand -> {
                val registeredCommand = kord.createGuildUserCommand(command.guildId, command.name) {
                    applyPermissions(command)
                }
                put(registeredCommand.id, command)
            }
            is GuildMessageCommand -> {
                val registeredCommand = kord.createGuildMessageCommand(command.guildId, command.name) {
                    applyPermissions(command)
                }
                put(registeredCommand.id, command)
            }
        }
    }
}

private fun GlobalApplicationCommandCreateBuilder.applyPermissions(
    command: TopLevelApplicationCommand<*, *, GlobalCommandPermission>,
) {
    defaultMemberPermissions = command.permission?.defaultRequiredPermissions
    dmPermission = command.permission?.isAllowedInDms
}

private fun ApplicationCommandCreateBuilder.applyPermissions(
    command: TopLevelApplicationCommand<*, *, GuildCommandPermission>,
) {
    defaultMemberPermissions = command.permission?.defaultRequiredPermissions
}

private fun RootInputChatBuilder.buildCommand(command: SlashCommand<*, *, *, *>) {
    buildArguments(command.executor?.arguments ?: emptyList())

    for ((_, group) in command.groups) {
        group(group.name, group.description) {
            for ((_, subCommand) in group.subCommands) {
                subCommand(subCommand.name, subCommand.description) {
                    buildArguments(subCommand.executor.arguments)
                }
            }
        }
    }

    for ((_, subCommand) in command.subCommands) {
        subCommand(subCommand.name, subCommand.description) {
            buildArguments(subCommand.executor.arguments)
        }
    }
}

private fun BaseInputChatBuilder.buildArguments(arguments: List<SlashArgument<*>>) {
    for (argument in arguments) {
        with(argument) {
            buildArgument(required = true)
        }
    }
}