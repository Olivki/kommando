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

@file:Suppress("LocalVariableName")

package net.ormr.kommando

import com.github.michaelbull.logging.InlineLogger
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import dev.kord.gateway.builder.PresenceBuilder
import net.ormr.kommando.commands.ApplicationCommand
import net.ormr.kommando.commands.ChatCommand
import net.ormr.kommando.commands.CommandGroup
import net.ormr.kommando.commands.prefix.CommandPrefix
import net.ormr.kommando.commands.prefix.CommandPrefixBuilder
import net.ormr.kommando.internal.handleChatCommands
import net.ormr.kommando.internal.handleSlashCommands
import net.ormr.kommando.internal.registerSlashCommands
import net.ormr.kommando.structures.CommandPrecondition
import net.ormr.kommando.structures.EventListener
import net.ormr.kommando.structures.MessageFilter
import net.ormr.kommando.structures.messageFilter
import org.kodein.di.DI
import org.kodein.di.DirectDI
import org.kodein.di.DirectDIAware
import org.kodein.di.bindSingleton
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

public class Kommando(
    public val kord: Kord,
    public val intents: Intents,
    override val directDI: DirectDI,
    public val commands: List<CommandGroup>,
    public val commandPreconditions: List<CommandPrecondition>,
    public val eventListeners: List<EventListener>,
    public val messageFilters: List<MessageFilter>,
    public val chatCommands: Map<String, ChatCommand<*>>,
    public val applicationCommands: List<ApplicationCommand<*, *>>,
    public val prefix: CommandPrefix?,
    internal val registeredApplicationCommands: Map<Snowflake, ApplicationCommand<*, *>>,
) : DirectDIAware {
    private companion object {
        private val logger = InlineLogger()
    }

    internal suspend fun initialize() {
        handleSlashCommands()
        handleChatCommands()
        eventListeners.forEach { it.executeBlock(this) }
    }
}

public class KommandoBuilder @PublishedApi internal constructor(
    public val kord: Kord,
    public val intents: Intents,
    override val directDI: DirectDI,
) : DirectDIAware {
    private companion object {
        private val logger = InlineLogger()
    }

    public val commands: MutableList<CommandGroup> = mutableListOf()

    public val commandPreconditions: MutableList<CommandPrecondition> = mutableListOf()

    public val eventListeners: MutableList<EventListener> = mutableListOf()

    public val messageFilters: MutableList<MessageFilter> = mutableListOf()

    /**
     * Filters away any messages created by the bot itself.
     *
     * Enabled by default if [Intent.MessageContent] is enabled in [intents].
     */
    @KommandoDsl
    public val ignoreSelf: MessageFilter = messageFilter { message.author?.id != kord.selfId }

    /**
     * Filters away any messages created by bots.
     *
     * Enabled by default if [Intent.MessageContent] is enabled in [intents].
     */
    @KommandoDsl
    public val ignoreBots: MessageFilter = messageFilter { message.author?.isBot != true }

    @PublishedApi
    internal var prefix: CommandPrefix? = null

    init {
        @OptIn(PrivilegedIntent::class)
        if (Intent.MessageContent in intents) {
            +ignoreSelf
            +ignoreBots
        }
    }

    @KommandoDsl
    public inline fun prefix(builder: CommandPrefixBuilder.() -> CommandPrefix) {
        prefix = builder(CommandPrefixBuilder(kord))
    }

    // TODO: move these unary operators to extension functions once 1.6.20 is out so we can use multi-receiver contexts
    public operator fun MessageFilter.unaryPlus() {
        messageFilters += this
    }

    public operator fun MessageFilter.unaryMinus() {
        messageFilters -= this
    }

    // TODO: throw exception if chatCommands are registered but no 'prefix' has been set
    @PublishedApi
    @OptIn(ExperimentalTime::class)
    internal suspend fun build(): Kommando {
        val commands = commands.toList()
        val commandPreconditions = commandPreconditions.toList()
        val eventListeners = eventListeners.toList()
        val messageFilters = messageFilters.toList()
        val flattenedCommands = commands.flatMap { it.commands }
        val chatCommands = buildMap {
            flattenedCommands
                .filterIsInstance<ChatCommand<*>>()
                .forEach {
                    put(it.name, it)
                    it.aliases.forEach { alias -> put(alias, it) }
                }
        }
        val applicationCommands = flattenedCommands.filterIsInstance<ApplicationCommand<*, *>>()
        logger.info { "Registering ${applicationCommands.size} application commands." }
        val (registeredApplicationCommands, duration) = measureTimedValue { registerSlashCommands(applicationCommands) }
        logger.info { "Registration of all ${applicationCommands.size} application commands took $duration." }
        val kommando = Kommando(
            kord = kord,
            intents = intents,
            directDI = directDI,
            commands = commands,
            commandPreconditions = commandPreconditions,
            eventListeners = eventListeners,
            messageFilters = messageFilters,
            chatCommands = chatCommands,
            applicationCommands = applicationCommands,
            prefix = prefix,
            registeredApplicationCommands = registeredApplicationCommands,
        )

        kommando.initialize()

        return kommando
    }
}

@KommandoDsl
public suspend inline fun bot(
    token: String,
    intents: Intents = Intents.nonPrivileged,
    presence: PresenceBuilder.() -> Unit = {},
    crossinline di: DI.MainBuilder.() -> Unit,
    builder: KommandoBuilder.() -> Unit,
) {
    bot(Kord(token), intents, presence, di, builder)
}

@KommandoDsl
public suspend inline fun bot(
    kord: Kord,
    intents: Intents = Intents.nonPrivileged,
    presence: PresenceBuilder.() -> Unit = {},
    crossinline di: DI.MainBuilder.() -> Unit,
    builder: KommandoBuilder.() -> Unit,
) {
    val kodein = DI.direct {
        bindSingleton { kord }
        di()
    }
    KommandoBuilder(kord, intents, kodein).apply(builder).build()
    kord.login {
        this.intents = intents
        this.presence = PresenceBuilder().apply(presence).toPresence()
    }
}