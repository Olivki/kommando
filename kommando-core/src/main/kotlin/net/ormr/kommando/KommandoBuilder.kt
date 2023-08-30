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

package net.ormr.kommando

import com.github.michaelbull.logging.InlineLogger
import dev.kord.core.Kord
import dev.kord.gateway.Intents
import dev.kord.gateway.builder.PresenceBuilder
import net.ormr.kommando.command.CommandsBuilder
import net.ormr.kommando.component.ComponentStorage
import net.ormr.kommando.localization.Localization
import net.ormr.kommando.localization.LocalizationBuilder
import net.ormr.kommando.modal.ModalStorage
import org.kodein.di.DI
import org.kodein.di.DirectDI
import org.kodein.di.DirectDIAware
import org.kodein.di.bindEagerSingleton
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration.Companion.minutes

@KommandoDsl
public class KommandoBuilder @PublishedApi internal constructor(
    public val kord: Kord,
    public val intents: Intents,
    override val directDI: DirectDI,
) : DirectDIAware {
    public var modalStorage: ModalStorage = ModalStorage(timeout = 7.minutes)
    public var componentStorage: ComponentStorage = ComponentStorage(timeout = 5.minutes)

    @PublishedApi
    internal var exceptionHandler: KommandoExceptionHandler? = null

    @PublishedApi
    internal var localization: Localization? = null

    @PublishedApi
    internal var commandsBuilder: CommandsBuilder? = null

    @PublishedApi
    internal suspend fun build(): Kommando {
        val localization = localization ?: LocalizationBuilder().build()
        val (commands, commandFactories) = commandsBuilder?.build() ?: CommandsBuilder().build()
        val kommando = Kommando(
            kord = kord,
            localization = localization,
            commands = commands,
            exceptionHandler = exceptionHandler,
            modalStorage = modalStorage,
            componentStorage = componentStorage,
        )

        if (localization.messageBundle.isEmpty()) {
            logger.warn { "MessageBundle is currently empty." }
        }

        kommandoDI.addExtend(directDI.di).addConfig { bindEagerSingleton { kommando } }

        kommando.setup(factories = commandFactories)

        return kommando
    }

    private companion object {
        private val logger = InlineLogger()
    }
}

@KommandoDsl
public suspend inline fun bot(
    token: String,
    intents: Intents = Intents.nonPrivileged,
    presence: PresenceBuilder.() -> Unit = {},
    crossinline di: DI.MainBuilder.() -> Unit = {},
    builder: KommandoBuilder.() -> Unit,
) {
    contract {
        callsInPlace(presence, InvocationKind.EXACTLY_ONCE)
        callsInPlace(di, InvocationKind.AT_LEAST_ONCE)
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    bot(Kord(token), intents, presence, di, builder)
}

@KommandoDsl
public suspend inline fun bot(
    kord: Kord,
    intents: Intents = Intents.nonPrivileged,
    presence: PresenceBuilder.() -> Unit = {},
    crossinline di: DI.MainBuilder.() -> Unit = {},
    builder: KommandoBuilder.() -> Unit,
) {
    contract {
        callsInPlace(presence, InvocationKind.EXACTLY_ONCE)
        callsInPlace(di, InvocationKind.EXACTLY_ONCE)
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    val kodein = DI.direct {
        bindEagerSingleton { kord }
        di()
    }

    KommandoBuilder(kord, intents, kodein).apply(builder).build()

    kord.login {
        this.intents = intents
        this.presence = PresenceBuilder().apply(presence).toPresence()
    }
}