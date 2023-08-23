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

package net.ormr.kommando.internal

import com.github.michaelbull.logging.InlineLogger
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.interaction.ApplicationCommandInteraction
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.core.entity.interaction.InteractionCommand
import dev.kord.core.event.interaction.*
import dev.kord.core.on
import net.ormr.kommando.*
import net.ormr.kommando.command.*
import net.ormr.kommando.command.argument.AutoCompletableArgument
import net.ormr.kommando.command.factory.*
import org.kodein.di.DirectDI
import org.kodein.di.direct
import dev.kord.core.entity.interaction.GroupCommand as KordGroupCommand
import dev.kord.core.entity.interaction.RootCommand as KordRootCommand
import dev.kord.core.entity.interaction.SubCommand as KordSubCommand

// Here be dragons

private val logger = InlineLogger()

context(Kommando)
internal suspend fun handleCommands() {
    val direct = direct
    kord.on<ApplicationCommandInteractionCreateEvent> {
        try {
            when (this) {
                is GlobalChatInputCommandInteractionCreateEvent -> inputCommand<GlobalCommand>(direct)
                is GlobalMessageCommandInteractionCreateEvent -> messageCommand<GlobalMessageCommand>(direct)
                is GlobalUserCommandInteractionCreateEvent -> userCommand<GlobalUserCommand>(direct)
                is GuildChatInputCommandInteractionCreateEvent -> inputCommand<RootChatInputCommand<*, *>>(direct)
                is GuildMessageCommandInteractionCreateEvent -> messageCommand<GuildMessageCommand>(direct)
                is GuildUserCommandInteractionCreateEvent -> userCommand<GuildUserCommand>(direct)
            }
        } catch (e: KommandoException) { // union types where :sob:
            logger.error(e) { e.message!! }
            exceptionHandler?.slashCommandInvoke?.handle(e, this)
        } catch (e: IllegalArgumentException) {
            logger.error(e) { e.message!! }
            exceptionHandler?.slashCommandInvoke?.handle(e, this)
        }
    }

    kord.on<AutoCompleteInteractionCreateEvent> {
        try {
            val argumentName = interaction.command.options.entries.single { it.value.focused }.key
            val commandName = interaction.command.rootName
            val commandId = interaction.command.rootId
            val registeredCommand = getRegisteredCommand(commandId, commandName)
            val command = registeredCommand.factory.create(direct)

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
            exceptionHandler?.autoCompleteInvoke?.handle(e, this)
        } catch (e: IllegalArgumentException) {
            logger.error(e) { e.message!! }
            exceptionHandler?.autoCompleteInvoke?.handle(e, this)
        }
    }
}

context(AutoCompleteInteractionCreateEvent)
private suspend inline fun Command<*>.autoComplete(name: String, commandId: Snowflake, commandName: String) {
    val event = this@AutoCompleteInteractionCreateEvent
    val argument = findRegistry().findArgument(name)

    if (argument is AutoCompletableArgument<*, *, *>) {
        val autoComplete = argument.autoComplete ?: noAutoCompleteDefined(
            argument,
            CommandArgumentRequestInfo(name, CommandRequestInfo(commandId, commandName)),
        )
        with(event.interaction) {
            autoComplete.provideSuggestion(event)
        }
    } else {
        invalidCommandArgumentType(
            AutoCompletableArgument::class,
            argument::class,
            CommandArgumentRequestInfo(name, CommandRequestInfo(commandId, commandName)),
        )
    }
}

context(Kommando, ChatInputCommandInteractionCreateEvent)
private suspend inline fun <reified Cmd> inputCommand(
    di: DirectDI,
) where  Cmd : RootChatInputCommand<*, *> {
    val interactionCommand = interaction.command
    val id = interactionCommand.rootId
    val rootName = interactionCommand.rootName
    val registeredCommand = getRegisteredCommand(id, rootName)
    val event = this@ChatInputCommandInteractionCreateEvent
    val command = registeredCommand.factory.create(di)
    if (command is Cmd) {
        check(command is SuperCommand<*, *>)
        when (interactionCommand) {
            is KordRootCommand -> {
                populateArguments(command, interactionCommand, event)
                runCommand(event, command)
            }
            is KordSubCommand -> {
                val subCommand = registeredCommand.getSubCommand(di, command, interactionCommand.name)
                populateArguments(subCommand, interactionCommand, event)
                runCommand(event, subCommand)
            }
            is KordGroupCommand -> {
                val registeredGroup = registeredCommand.getRegisteredGroup(command, interactionCommand.groupName)
                registeredGroup.createGroup(di, command)
                val subCommand = registeredGroup.getSubCommand(di, command, interactionCommand.name)
                populateArguments(subCommand, interactionCommand, event)
                runCommand(event, subCommand)
            }
        }
    } else {
        invalidCommandType(Cmd::class, command::class, CommandRequestInfo(id, rootName))
    }
}

private fun RegisteredGroup.createGroup(di: DirectDI, command: SuperCommand<*, *>): CommandGroup<*> {
    val group = factory.create(di).fix()
    group.initSuperCommand(command)
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
): SubCommand<*, *> {
    val result = subCommands[name]?.create(di) ?: noSuchSubCommand(name, command)
    result.fixSubCommand().initSuperComponent(command)
    return result
}

private suspend fun runCommand(
    event: ChatInputCommandInteractionCreateEvent,
    command: ChatInputCommand<*>,
) {
    when (event) {
        is GuildChatInputCommandInteractionCreateEvent -> when (command) {
            is GlobalChatInputCommand -> executeCommand(command, event.interaction)
            is GuildChatInputCommand -> executeCommand(command, event.interaction)
        }
        else -> if (command is GlobalChatInputCommand) executeCommand(command, event.interaction)
    }
}

private suspend fun populateArguments(
    command: Command<*>,
    interactionCommand: InteractionCommand,
    event: ChatInputCommandInteractionCreateEvent,
) {
    val fixedCommand = command.fixCommand()
    val arguments = fixedCommand
        .findDirectArguments()
        .mapValuesTo(hashMapOf()) { (_, arg) -> arg.getValue(interactionCommand, event) }
    fixedCommand
        .registry
        .populate(arguments)
}

context(Kommando, MessageCommandInteractionCreateEvent)
private suspend inline fun <reified Cmd> messageCommand(di: DirectDI)
        where Cmd : MessageCommand<*, *> =
    withCommand<Cmd>(di) { command ->
        val value = interaction.getTarget()
        command.value = value
        executeCommand(command, interaction)
    }

context(Kommando, UserCommandInteractionCreateEvent)
private suspend inline fun <reified Cmd> userCommand(di: DirectDI)
        where Cmd : UserCommand<*, *> =
    withCommand<Cmd>(di) { command ->
        val value = interaction.getTarget()
        command.value = value
        executeCommand(command, interaction)
    }

// Kotlin fails to smart cast here, but still complains about useless casts, lol
@Suppress("USELESS_CAST")
private suspend fun executeCommand(command: Command<*>, interaction: ApplicationCommandInteraction) {
    @Suppress("REDUNDANT_ELSE_IN_WHEN")
    when (command) {
        is GuildCommandType -> {
            val context = GuildCommandContextImpl(interaction.unsafeCast())
            with(context) { (command as GuildCommandType).execute() }
        }
        is GlobalCommandType -> {
            val context = GlobalCommandContextImpl(interaction.unsafeCast())
            with(context) { (command as GlobalCommandType).execute() }
        }
        // The Kotlin CLI compiler sometimes fails to infer that this is exhaustive,
        // this problem seems to be fully fixed in 2.0
        // TODO: remove this when Kotlin 2.0 is released
        else -> error("Should never happen, but it did for: $command")
    }
}

private inline fun <reified Value> Any.unsafeCast(): Value = this as Value

context(Kommando, ApplicationCommandInteractionCreateEvent)
private inline fun <reified Cmd> withCommand(di: DirectDI, block: (command: Cmd) -> Unit)
        where Cmd : Command<*> {
    val id = interaction.invokedCommandId
    val registeredCommand = getRegisteredCommand(id, interaction.invokedCommandName)
    val command = registeredCommand.factory.create(di)
    if (command is Cmd) {
        block(command)
    } else {
        invalidCommandType(Cmd::class, command::class, CommandRequestInfo(id, interaction.invokedCommandName))
    }
}

context(Kommando)
private fun getRegisteredCommand(id: Snowflake, name: String): RegisteredCommand =
    registeredCommands[id] ?: noSuchCommand(id, name)

private data class GuildCommandContextImpl(
    override val interaction: GuildChatInputCommandInteraction,
) : GuildCommandContext

private data class GlobalCommandContextImpl(
    override val interaction: ChatInputCommandInteraction,
) : GlobalCommandContext