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
import dev.kord.core.entity.interaction.InteractionCommand
import dev.kord.core.event.Event
import dev.kord.core.event.interaction.*
import dev.kord.core.on
import net.ormr.kommando.Kommando
import net.ormr.kommando.commands.*
import net.ormr.kommando.commands.arguments.CommandExecutorArguments
import net.ormr.kommando.commands.arguments.CommandExecutorArguments.*
import net.ormr.kommando.commands.arguments.slash.SlashDefaultArgument

// our way of handling the commands might be a bit naive, but w/e
internal suspend fun Kommando.handleSlashCommands() {
    // TODO: make this code cleaner and more modular, we can most likely cut down a lot of LOC here
    kord.on<ApplicationCommandInteractionCreateEvent> {
        when (this) {
            is GlobalUserCommandInteractionCreateEvent -> {
                val command = getCommand(interaction.invokedCommandId)
                println(command)
                if (command !is GlobalUserCommand) return@on
                val userArg = interaction.getTarget()
                command.executor.execute(GlobalUserCommandData(kord, this), Args1(userArg))
            }
            is GlobalMessageCommandInteractionCreateEvent -> {
                val command = getCommand(interaction.invokedCommandId)
                if (command !is GlobalMessageCommand) return@on
                val messageArg = interaction.getTarget()
                command.executor.execute(GlobalMessageCommandData(kord, this), Args1(messageArg))
            }
            is GlobalChatInputCommandInteractionCreateEvent -> {
                val interactionCommand = interaction.command
                val command = getCommand(interactionCommand.rootId)
                if (command !is GlobalSlashCommand) return@on
                val args = command.getArgs(interactionCommand, this)
                command.executor.execute(GlobalSlashCommandData(kord, this), args)
            }
            is GuildUserCommandInteractionCreateEvent -> {
                when (val command = getCommand(interaction.invokedCommandId)) {
                    is GlobalUserCommand -> {
                        val userArg = interaction.getTarget()
                        command.executor.execute(GlobalUserCommandData(kord, this), Args1(userArg))
                    }
                    is GuildUserCommand -> {
                        val userArg = interaction.getTarget()
                        command.executor.execute(GuildUserCommandData(kord, this), Args1(userArg))
                    }
                    else -> return@on
                }
            }
            is GuildMessageCommandInteractionCreateEvent -> {
                when (val command = getCommand(interaction.invokedCommandId)) {
                    is GlobalMessageCommand -> {
                        val messageArg = interaction.getTarget()
                        command.executor.execute(GlobalMessageCommandData(kord, this), Args1(messageArg))
                    }
                    is GuildMessageCommand -> {
                        val messageArg = interaction.getTarget()
                        command.executor.execute(GuildMessageCommandData(kord, this), Args1(messageArg))
                    }
                    else -> return@on
                }
            }
            is GuildChatInputCommandInteractionCreateEvent -> {
                val interactionCommand = interaction.command
                when (val command = getCommand(interactionCommand.rootId)) {
                    is GlobalSlashCommand -> {
                        val args = command.getArgs(interactionCommand, this)
                        command.executor.execute(GlobalSlashCommandData(kord, this), args)
                    }
                    is GuildSlashCommand -> {
                        val args = command.getArgs(interactionCommand, this)
                        command.executor.execute(GuildSlashCommandData(kord, this), args)
                    }
                    else -> return@on
                }
            }
        }
    }
}

private fun Kommando.getCommand(id: Snowflake): ApplicationCommand<*, *> =
    registeredApplicationCommands[id] ?: noCommandFound(id)

private suspend fun <E : Event, D : CommandData<E>> ApplicationCommand<E, D>.getArgs(
    command: InteractionCommand,
    event: ChatInputCommandInteractionCreateEvent,
): CommandExecutorArguments = executor.arguments.map {
    when (it) {
        is SlashDefaultArgument<*> -> it.getValueOrCreate(command, event)
        else -> it.getValue(command)
    }
}.toArgs()

private fun List<Any?>.toArgs(): CommandExecutorArguments = when (size) {
    0 -> Args0
    1 -> Args1(this[0])
    2 -> Args2(this[0], this[1])
    3 -> Args3(this[0], this[1], this[2])
    4 -> Args4(this[0], this[1], this[2], this[3])
    5 -> Args5(this[0], this[1], this[2], this[3], this[4])
    else -> throw IllegalArgumentException("Can only make up to Args5, $size was requested.")
}

private fun noCommandFound(id: Snowflake): Nothing = throw UnknownCommandException(id)

public class UnknownCommandException(public val id: Snowflake) :
    RuntimeException("Received event for command with id '$id', which is an unknown command id.")