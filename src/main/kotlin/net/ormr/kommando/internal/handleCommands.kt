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

@file:Suppress("NOTHING_TO_INLINE")

package net.ormr.kommando.internal

import com.github.michaelbull.logging.InlineLogger
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.interaction.InteractionCommand
import dev.kord.core.event.interaction.*
import dev.kord.core.on
import net.ormr.kommando.*
import net.ormr.kommando.commands.*
import net.ormr.kommando.commands.arguments.AutoCompletableArgument
import net.ormr.kommando.commands.factory.ParentCommandFactory
import net.ormr.kommando.commands.factory.SingleCommandFactory
import org.kodein.di.DirectDI
import org.kodein.di.direct
import dev.kord.core.entity.interaction.GroupCommand as KordGroupCommand
import dev.kord.core.entity.interaction.RootCommand as KordRootCommand
import dev.kord.core.entity.interaction.SubCommand as KordSubCommand

private val logger = InlineLogger()

context(Kommando)
        internal suspend fun handleCommands() {
    val direct = direct

    // TODO: is the error logging format for cause correct? will it double log the messages?

    kord.on<ApplicationCommandInteractionCreateEvent> {
        try {
            when (this) {
                is GlobalChatInputCommandInteractionCreateEvent -> inputCommand<GlobalCommand>(direct)
                is GlobalMessageCommandInteractionCreateEvent -> messageCommand<GlobalMessageCommand>(direct)
                is GlobalUserCommandInteractionCreateEvent -> userCommand<GlobalUserCommand>(direct)
                is GuildChatInputCommandInteractionCreateEvent -> inputCommand<TopLevelChatInputCommand>(direct)
                is GuildMessageCommandInteractionCreateEvent -> messageCommand<GuildMessageCommand>(direct)
                is GuildUserCommandInteractionCreateEvent -> userCommand<GuildUserCommand>(direct)
            }
        } catch (e: KommandoException) { // union types where :sob:
            logger.error(e) { e.message!! }
            exceptionHandler?.slashCommandInvoke?.invoke(e, this)
        } catch (e: IllegalArgumentException) {
            logger.error(e) { e.message!! }
            exceptionHandler?.slashCommandInvoke?.invoke(e, this)
        }
    }

    kord.on<AutoCompleteInteractionCreateEvent> {
        try {
            val argumentName = interaction.command.options.entries.single { it.value.focused }.key
            val commandName = interaction.command.rootName
            val commandId = interaction.command.rootId
            val registeredCommand = getRegisteredCommand(commandId, commandName)
            val command = registeredCommand.factory(direct)

            if (command is SuperCommand<*, *>) {
                when (val interactionCommand = interaction.command) {
                    is KordRootCommand -> command.autoComplete(argumentName, commandId, commandName)
                    is KordSubCommand -> {
                        val subCommand = registeredCommand.getSubCommand(direct, command, interactionCommand.name)
                        subCommand.autoComplete(argumentName, commandId, commandName)
                    }
                    is KordGroupCommand -> {
                        val registeredGroup =
                            registeredCommand.getRegisteredGroup(command, interactionCommand.groupName)
                        registeredGroup.createGroup(direct, command)
                        val subCommand = registeredGroup.getSubCommand(direct, command, interactionCommand.name)
                        subCommand.autoComplete(argumentName, commandId, commandName)
                    }
                }
            } else {
                invalidCommandType(SuperCommand::class, command::class, CommandRequestInfo(commandId, commandName))
            }
        } catch (e: KommandoException) {
            logger.error(e) { e.message!! }
            exceptionHandler?.autoCompleteInvoke?.invoke(e, this)
        } catch (e: IllegalArgumentException) {
            logger.error(e) { e.message!! }
            exceptionHandler?.autoCompleteInvoke?.invoke(e, this)
        }
    }
}

context(AutoCompleteInteractionCreateEvent)
        private suspend inline fun Command<*>.autoComplete(name: String, commandId: Snowflake, commandName: String) {
    val event = this@AutoCompleteInteractionCreateEvent
    val argument = getArgument(name)

    if (argument is AutoCompletableArgument) {
        val autoComplete = argument.autoComplete ?: noAutoCompleteDefined(
            argument,
            CommandArgumentRequestInfo(name, CommandRequestInfo(commandId, commandName)),
        )
        autoComplete(event.interaction, event)
    } else {
        invalidCommandArgumentType(
            AutoCompletableArgument::class,
            argument::class,
            CommandArgumentRequestInfo(name, CommandRequestInfo(commandId, commandName)),
        )
    }
}

context(Kommando, ChatInputCommandInteractionCreateEvent)
        private suspend inline fun <reified C : TopLevelChatInputCommand> inputCommand(
    di: DirectDI,
) {
    val interactionCommand = interaction.command
    val id = interactionCommand.rootId
    val rootName = interactionCommand.rootName
    val registeredCommand = getRegisteredCommand(id, rootName)
    val event = this@ChatInputCommandInteractionCreateEvent
    val command = registeredCommand.factory(di)
    if (command is C) {
        check(command is SuperCommand<*, *>)
        when (interactionCommand) {
            is KordRootCommand -> {
                command.registerArguments(interactionCommand, event)
                runCommand(event, command)
            }
            is KordSubCommand -> {
                val subCommand = registeredCommand.getSubCommand(di, command, interactionCommand.name)
                subCommand.registerArguments(interactionCommand, event)
                runCommand(event, subCommand)
            }
            is KordGroupCommand -> {
                val registeredGroup = registeredCommand.getRegisteredGroup(command, interactionCommand.groupName)
                registeredGroup.createGroup(di, command)
                val subCommand = registeredGroup.getSubCommand(di, command, interactionCommand.name)
                subCommand.registerArguments(interactionCommand, event)
                runCommand(event, subCommand)
            }
        }
    } else {
        invalidCommandType(C::class, command::class, CommandRequestInfo(id, rootName))
    }
}

private fun RegisteredGroup.createGroup(di: DirectDI, command: SuperCommand<*, *>): CommandGroup<*> {
    val group = this.factory(di)
    group.setParent(command)
    return group
}

private fun RegisteredCommand.getRegisteredGroup(
    command: Command<*>,
    name: String,
): RegisteredGroup = when (factory) {
    is ParentCommandFactory -> groups[name] ?: noSuchCommandGroup(name, command)
    is SingleCommandFactory -> noSuchCommandGroup(name, command)
}

private fun RegisteredSubCommandContainer.getSubCommand(
    di: DirectDI,
    command: SuperCommand<*, *>,
    name: String,
): SubCommand<*, *> = subCommands[name]?.invoke(di)?.also { it.setParent(command) } ?: noSuchSubCommand(name, command)

private suspend fun runCommand(
    event: ChatInputCommandInteractionCreateEvent,
    command: ChatInputCommand,
) {
    when (event) {
        is GuildChatInputCommandInteractionCreateEvent -> when (command) {
            is GlobalChatInputCommand -> command.fix().run(event.interaction)
            is GuildChatInputCommand -> command.fix().run(event.interaction)
        }
        else -> if (command is GlobalChatInputCommand) command.fix().run(event.interaction)
    }
}

// kotlin can't really determine that this should in fact just be valid, as the only implementations of
// 'GlobalChatInputCommand' are Command<GlobalCommandInteraction> instances, so this cast is actually completely safe
@Suppress("UNCHECKED_CAST")
private inline fun GlobalChatInputCommand.fix(): Command<GlobalCommandInteraction> =
    this as Command<GlobalCommandInteraction>

@Suppress("UNCHECKED_CAST")
private inline fun GuildChatInputCommand.fix(): Command<GuildCommandInteraction> =
    this as Command<GuildCommandInteraction>

// TODO: make 'arg.getValue' suspend
private suspend fun Command<*>.registerArguments(
    interactionCommand: InteractionCommand,
    event: ChatInputCommandInteractionCreateEvent,
) {
    val arguments =
        registeredArguments?.mapValuesTo(hashMapOf()) { (_, arg) -> arg.getValue(interactionCommand, event) }
    resolvedArguments = arguments
}

context(Kommando, MessageCommandInteractionCreateEvent)
        private suspend inline fun <reified C : MessageCommand<*, *>> messageCommand(di: DirectDI) =
    withCommand<C>(di) { command ->
        val value = interaction.getTarget()
        command.value = value
        command.run(interaction)
    }

context(Kommando, UserCommandInteractionCreateEvent)
        private suspend inline fun <reified C : UserCommand<*, *>> userCommand(di: DirectDI) =
    withCommand<C>(di) { command ->
        val value = interaction.getTarget()
        command.value = value
        command.run(interaction)
    }

context(Kommando, ApplicationCommandInteractionCreateEvent)
        private inline fun <reified C : Command<*>> withCommand(di: DirectDI, block: (command: C) -> Unit) {
    val id = interaction.invokedCommandId
    val registeredCommand = getRegisteredCommand(id, interaction.invokedCommandName)
    val command = registeredCommand.factory(di)
    if (command is C) {
        block(command)
    } else {
        invalidCommandType(C::class, command::class, CommandRequestInfo(id, interaction.invokedCommandName))
    }
}

context(Kommando)
        private fun getRegisteredCommand(id: Snowflake, name: String): RegisteredCommand =
    registeredCommands[id] ?: noSuchCommand(id, name)