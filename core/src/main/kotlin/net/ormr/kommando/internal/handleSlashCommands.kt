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
import dev.kord.core.entity.interaction.GroupCommand
import dev.kord.core.entity.interaction.InteractionCommand
import dev.kord.core.entity.interaction.RootCommand
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.core.event.Event
import dev.kord.core.event.interaction.*
import dev.kord.core.on
import net.ormr.kommando.Kommando
import net.ormr.kommando.commands.*
import net.ormr.kommando.commands.arguments.CommandExecutorArguments
import net.ormr.kommando.commands.arguments.CommandExecutorArguments.*
import net.ormr.kommando.commands.arguments.slash.AutoCompleteAction
import net.ormr.kommando.commands.arguments.slash.SlashArgument
import net.ormr.kommando.commands.arguments.slash.SlashArgumentWithChoice
import net.ormr.kommando.commands.arguments.slash.SlashDefaultArgument

internal suspend fun Kommando.handleApplicationCommands() {
    val kommando = this@handleApplicationCommands
    // TODO: clean up this code, there's a lot of just the same stuff repeated over and over again
    kord.on<ApplicationCommandInteractionCreateEvent> {
        when (this) {
            is GlobalUserCommandInteractionCreateEvent -> {
                val command = getCommand(interaction.invokedCommandId)
                // TODO: throw exception?
                if (command !is GlobalUserCommand) return@on
                val userArg = interaction.getTarget()
                command.executor.execute(GlobalUserCommandData(kommando, this), Args1(userArg))
            }
            is GlobalMessageCommandInteractionCreateEvent -> {
                val command = getCommand(interaction.invokedCommandId)
                // TODO: throw exception?
                if (command !is GlobalMessageCommand) return@on
                val messageArg = interaction.getTarget()
                command.executor.execute(GlobalMessageCommandData(kommando, this), Args1(messageArg))
            }
            is GlobalChatInputCommandInteractionCreateEvent -> {
                val interaction = interaction.command
                val commandId = interaction.rootId
                val command = getCommand(commandId)
                // TODO: throw exception?
                if (command !is GlobalSlashCommand) return@on
                when (interaction) {
                    is RootCommand -> {
                        val args = command.getArgs(interaction, this)
                        command.executor!!.execute(GlobalSlashCommandData(kommando, this), args)
                    }
                    is SubCommand -> {
                        val subCommand = command.subCommands[interaction.name]
                            ?: noSuchSubCommand(interaction.name, commandId)
                        val args = subCommand.getArgs(interaction, this)
                        subCommand.executor.execute(GlobalSlashSubCommandData(kommando, this), args)
                    }
                    is GroupCommand -> {
                        val group = command.groups[interaction.groupName]
                            ?: noSuchCommandGroup(interaction.groupName, commandId)
                        val subCommand = group.subCommands[interaction.name]
                            ?: noSuchSubCommand(interaction.name, commandId)
                        val args = subCommand.getArgs(interaction, this)
                        subCommand.executor.execute(GlobalSlashSubCommandData(kommando, this), args)
                    }
                }
            }
            is GuildUserCommandInteractionCreateEvent -> {
                when (val command = getCommand(interaction.invokedCommandId)) {
                    is GlobalUserCommand -> {
                        val userArg = interaction.getTarget()
                        command.executor.execute(GlobalUserCommandData(kommando, this), Args1(userArg))
                    }
                    is GuildUserCommand -> {
                        val userArg = interaction.getTarget()
                        command.executor.execute(GuildUserCommandData(kommando, this), Args1(userArg))
                    }
                    // TODO: throw exception?
                    else -> return@on
                }
            }
            is GuildMessageCommandInteractionCreateEvent -> {
                when (val command = getCommand(interaction.invokedCommandId)) {
                    is GlobalMessageCommand -> {
                        val messageArg = interaction.getTarget()
                        command.executor.execute(GlobalMessageCommandData(kommando, this), Args1(messageArg))
                    }
                    is GuildMessageCommand -> {
                        val messageArg = interaction.getTarget()
                        command.executor.execute(GuildMessageCommandData(kommando, this), Args1(messageArg))
                    }
                    // TODO: throw exception?
                    else -> return@on
                }
            }
            is GuildChatInputCommandInteractionCreateEvent -> {
                val interaction = interaction.command
                val commandId = interaction.rootId
                when (val command = getCommand(commandId)) {
                    is GlobalSlashCommand -> when (interaction) {
                        is RootCommand -> {
                            val args = command.getArgs(interaction, this)
                            command.executor!!.execute(GlobalSlashCommandData(kommando, this), args)
                        }
                        is SubCommand -> {
                            val subCommand = command.getSubCommand(interaction.name, commandId)
                            val args = subCommand.getArgs(interaction, this)
                            subCommand.executor.execute(GlobalSlashSubCommandData(kommando, this), args)
                        }
                        is GroupCommand -> {
                            val group = command.getGroup(interaction.groupName, commandId)
                            val subCommand = group.getSubCommand(interaction.name, commandId)
                            val args = subCommand.getArgs(interaction, this)
                            subCommand.executor.execute(GlobalSlashSubCommandData(kommando, this), args)
                        }
                    }
                    is GuildSlashCommand -> when (interaction) {
                        is RootCommand -> {
                            val args = command.getArgs(interaction, this)
                            command.executor!!.execute(GuildSlashCommandData(kommando, this), args)
                        }
                        is SubCommand -> {
                            val subCommand = command.getSubCommand(interaction.name, commandId)
                            val args = subCommand.getArgs(interaction, this)
                            subCommand.executor.execute(GuildSlashSubCommandData(kommando, this), args)
                        }
                        is GroupCommand -> {
                            val group = command.getGroup(interaction.groupName, commandId)
                            val subCommand = group.getSubCommand(interaction.name, commandId)
                            val args = subCommand.getArgs(interaction, this)
                            subCommand.executor.execute(GuildSlashSubCommandData(kommando, this), args)
                        }
                    }
                    // TODO: throw exception?
                    else -> return@on
                }
            }
        }
    }
    kord.on<AutoCompleteInteractionCreateEvent> {
        val focusedCommandName = interaction.command.options.entries.single { it.value.focused }.key
        val commandId = interaction.command.rootId
        val command = getCommand(commandId)
        // TODO: throw exception?
        if (command !is SlashCommand<*, *, *>) return@on
        when (val interactionCommand = interaction.command) {
            is RootCommand -> {
                val argument = command.executor!!.getArgument(focusedCommandName)
                if (argument !is SlashArgumentWithChoice) invalidArgumentForAutoComplete()
                val autoComplete = argument.getAutoComplete()
                autoComplete(interaction, this)
            }
            is SubCommand -> {
                val subCommand = command.getSubCommand(interactionCommand.name, commandId)
                val argument = subCommand.executor.getArgument(focusedCommandName)
                if (argument !is SlashArgumentWithChoice) invalidArgumentForAutoComplete()
                val autoComplete = argument.getAutoComplete()
                autoComplete(interaction, this)
            }
            is GroupCommand -> {
                val group = command.getGroup(interactionCommand.groupName, commandId)
                val subCommand = group.getSubCommand(interactionCommand.name, commandId)
                val argument = subCommand.executor.getArgument(focusedCommandName)
                if (argument !is SlashArgumentWithChoice) invalidArgumentForAutoComplete()
                val autoComplete = argument.getAutoComplete()
                autoComplete(interaction, this)
            }
        }
    }
}

// TODO: custom exception
private fun invalidArgumentForAutoComplete(): Nothing = error("Found argument does not have auto complete support.")

// TODO: make into a normal function?
private fun <S : SlashSubCommand<*, *>> SlashCommand<*, *, S>.getGroup(
    name: String,
    id: Snowflake,
): SlashCommandGroup<S> = groups[name] ?: noSuchCommandGroup(name, id)

// TODO: make into a normal function?
private fun <S : SlashSubCommand<*, *>> SlashCommand<*, *, S>.getSubCommand(
    name: String,
    id: Snowflake,
): S = subCommands[name] ?: noSuchSubCommand(name, id)

// TODO: make into a normal function?
private fun <S : SlashSubCommand<*, *>> SlashCommandGroup<S>.getSubCommand(
    name: String,
    id: Snowflake,
): S = subCommands[name] ?: noSuchSubCommand(name, id)

// TODO: custom exception
private fun SlashArgumentWithChoice<*>.getAutoComplete(): AutoCompleteAction =
    autoComplete ?: error("Auto complete was requested but autoComplete action is 'null' on argument.")

private fun CommandExecutor<SlashArgument<*>, *, *, *>.getArgument(name: String): SlashArgument<*> =
    arguments.single { it.name == name }

private fun Kommando.getCommand(id: Snowflake): ApplicationCommand<*, *> =
    registeredApplicationCommands[id] ?: noCommandFound(id)

private suspend fun <E : Event, D : CommandData<E>> ApplicationCommand<E, D>.getArgs(
    command: InteractionCommand,
    event: ChatInputCommandInteractionCreateEvent,
): CommandExecutorArguments = executor?.arguments?.map {
    when (it) {
        is SlashDefaultArgument<*> -> it.getValueOrCreate(command, event)
        else -> it.getValue(command)
    }
}?.toArgs() ?: error("Executor for $this is null for command (id=${command.rootId}, name=${command.rootName})")

private fun List<Any?>.toArgs(): CommandExecutorArguments = when (size) {
    0 -> Args0
    1 -> Args1(this[0])
    2 -> Args2(this[0], this[1])
    3 -> Args3(this[0], this[1], this[2])
    4 -> Args4(this[0], this[1], this[2], this[3])
    5 -> Args5(this[0], this[1], this[2], this[3], this[4])
    else -> throw IllegalArgumentException("Can only make up to Args5, $size was requested.")
}

private fun noSuchCommandGroup(name: String, id: Snowflake): Nothing = throw NoSuchCommandGroupException(name, id)

private fun noSuchSubCommand(name: String, id: Snowflake): Nothing = throw NoSuchSubCommandException(name, id)

private fun noCommandFound(id: Snowflake): Nothing = throw UnknownCommandException(id)