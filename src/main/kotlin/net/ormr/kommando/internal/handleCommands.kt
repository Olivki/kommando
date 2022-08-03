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

package net.ormr.kommando.internal

import com.github.michaelbull.logging.InlineLogger
import dev.kord.common.entity.Snowflake
import dev.kord.core.event.interaction.*
import dev.kord.core.on
import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoException
import net.ormr.kommando.NoSuchCommandException
import net.ormr.kommando.commands.*
import net.ormr.kommando.commands.factory.CommandFactory
import org.kodein.di.DirectDI
import org.kodein.di.direct

private val logger = InlineLogger()

context(Kommando)
        internal suspend fun handleCommands() {
    val direct = direct
    kord.on<ApplicationCommandInteractionCreateEvent> {
        try {
            when (this) {
                is GlobalChatInputCommandInteractionCreateEvent -> TODO()
                is GlobalMessageCommandInteractionCreateEvent -> messageCommand<GlobalMessageCommand>(direct)
                is GlobalUserCommandInteractionCreateEvent -> userCommand<GlobalUserCommand>(direct)
                is GuildChatInputCommandInteractionCreateEvent -> TODO()
                is GuildMessageCommandInteractionCreateEvent -> messageCommand<GuildMessageCommand>(direct)
                is GuildUserCommandInteractionCreateEvent -> userCommand<GuildUserCommand>(direct)
            }
        } catch (e: KommandoException) {
            // TODO: is this the correct logging format? 'error(e)' might already print the message?
            logger.error(e) { e.message!! }
        }
    }
}

context(Kommando, ChatInputCommandInteractionCreateEvent)
        private suspend inline fun <reified C : SuperCommand<*, *>> inputCommand() {
    val interactionCommand = interaction.command
    val id = interactionCommand.rootId
    val command = registeredCommands[id] ?: throw NoSuchCommandException(
        id,
        interaction.invokedCommandName,
    )
    if (command is C) {
        TODO()
    } else {
        logger.error { "Command under id $id was requested to be type '${C::class.qualifiedName!!}', but was type '${command::class.qualifiedName!!}'." }
    }
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
    val command = getCommandFactory(id, interaction.invokedCommandName)(di)
    if (command is C) {
        block(command)
    } else {
        logger.error { "Command under id $id was requested to be type '${C::class.qualifiedName!!}', but was type '${command::class.qualifiedName!!}'." }
    }
}

context(Kommando)
        private fun getCommandFactory(id: Snowflake, name: String): CommandFactory =
    registeredCommands[id] ?: throw NoSuchCommandException(id, name)