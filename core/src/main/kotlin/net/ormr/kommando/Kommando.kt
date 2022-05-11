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
import net.ormr.kommando.commands.ChatCommand
import net.ormr.kommando.commands.CommandContainer
import net.ormr.kommando.commands.TopLevelApplicationCommand
import net.ormr.kommando.commands.prefix.CommandPrefix
import net.ormr.kommando.commands.prefix.CommandPrefixBuilder
import net.ormr.kommando.components.ComponentStorage
import net.ormr.kommando.components.ExecutableComponent
import net.ormr.kommando.internal.*
import net.ormr.kommando.modals.Modal
import net.ormr.kommando.modals.ModalStorage
import net.ormr.kommando.structures.EventListener
import net.ormr.kommando.structures.MessageFilter
import net.ormr.kommando.structures.messageFilter
import org.kodein.di.*
import java.lang.invoke.MethodHandles.lookup
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

public class Kommando internal constructor(
    public val kord: Kord,
    public val intents: Intents,
    override val di: DI,
    public val commands: List<CommandContainer>,
    public val eventListeners: List<EventListener>,
    public val messageFilters: List<MessageFilter>,
    public val chatCommands: Map<String, ChatCommand<*>>,
    public val applicationCommands: List<TopLevelApplicationCommand<*, *>>,
    public val prefix: CommandPrefix?,
    internal val registeredApplicationCommands: Map<Snowflake, TopLevelApplicationCommand<*, *>>,
    internal val config: KommandoConfig,
) : DIAware, KommandoAware {
    /**
     * This [Kommando] instance.
     */
    override val kommando: Kommando
        get() = this

    /**
     * The [Modal]s that are currently being managed by this Kommando instance.
     */
    public val modalStorage: ModalStorage = ModalStorage(this, config.modalExpirationDuration)

    /**
     * The [ExecutableComponent]s that are currently being managed by this Kommando instance.
     */
    public val componentStorage: ComponentStorage = ComponentStorage(this)

    internal suspend fun initialize() {
        registerApplicationCommandPermissions()
        handleApplicationCommands()
        handleChatCommands()
        handleComponents()
        handleModals()
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

    public val commands: MutableList<CommandContainer> = mutableListOf()

    public val eventListeners: MutableList<EventListener> = mutableListOf()

    public val messageFilters: MutableList<MessageFilter> = mutableListOf()

    /**
     * How long until any stored [modals][Kommando.modalStorage] should be expired after write, if `null` then no expiration
     * duration is set.
     */
    public var modalExpirationDuration: Duration? = null

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
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        prefix = builder(CommandPrefixBuilder(kord))
    }

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
        val applicationCommands = flattenedCommands.filterIsInstance<TopLevelApplicationCommand<*, *>>()
        // TODO: should we log this if 'applicationCommands' is empty?
        logger.info { "Registering ${applicationCommands.size} application commands." }
        val (registeredApplicationCommands, duration) = measureTimedValue { registerSlashCommands(applicationCommands) }
        logger.info { "Application command registration took $duration." }
        val kommando = Kommando(
            kord = kord,
            intents = intents,
            di = directDI.di,
            commands = commands,
            eventListeners = eventListeners,
            messageFilters = messageFilters,
            chatCommands = chatCommands,
            applicationCommands = applicationCommands,
            prefix = prefix,
            registeredApplicationCommands = registeredApplicationCommands,
            config = KommandoConfig(
                modalExpirationDuration = modalExpirationDuration,
            ),
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
    contract {
        callsInPlace(presence, InvocationKind.EXACTLY_ONCE)
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

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
    contract {
        callsInPlace(presence, InvocationKind.EXACTLY_ONCE)
        callsInPlace(di, InvocationKind.AT_LEAST_ONCE)
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    val lookup = lookup()
    val (diSetup, kommandoSetup) = readProcessorMetadata(lookup)
    val kodein = DI.direct {
        bindSingleton { kord }
        diSetup?.invoke(this)
        di()
    }
    KommandoBuilder(kord, intents, kodein).apply { kommandoSetup?.invoke(this) }.apply(builder).build()
    kord.login {
        this.intents = intents
        this.presence = PresenceBuilder().apply(presence).toPresence()
    }
}