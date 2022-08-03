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

package net.ormr.kommando

import com.github.michaelbull.logging.InlineLogger
import dev.kord.common.Locale
import dev.kord.core.Kord
import dev.kord.gateway.Intents
import dev.kord.gateway.builder.PresenceBuilder
import net.ormr.kommando.commands.factory.CommandFactory
import net.ormr.kommando.commands.permissions.DefaultCommandPermissions
import net.ormr.kommando.localization.DEFAULT_PATH_RESOLVER
import net.ormr.kommando.localization.EmptyLocalizationContainer
import net.ormr.kommando.localization.Localization
import net.ormr.kommando.localization.LocalizationContainer
import org.kodein.di.DI
import org.kodein.di.DirectDI
import org.kodein.di.DirectDIAware
import org.kodein.di.bindEagerSingleton
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KommandoDsl
public class KommandoBuilder @PublishedApi internal constructor(
    public val kord: Kord,
    public val intents: Intents,
    override val directDI: DirectDI,
) : DirectDIAware {
    private companion object {
        private val logger = InlineLogger()
    }

    public val commands: MutableList<CommandFactory> = mutableListOf()

    public var defaultCommandPermissions: DefaultCommandPermissions? = null

    public var localization: Localization = Localization(
        defaultLocale = Locale.ENGLISH_UNITED_STATES,
        container = LocalizationContainer.empty(),
        pathResolver = DEFAULT_PATH_RESOLVER,
    )

    @PublishedApi
    internal suspend fun build(): Kommando {
        val kommando = Kommando(
            kord = kord,
            localization = localization,
            defaultCommandPermissions = defaultCommandPermissions,
        )

        if (localization.container === EmptyLocalizationContainer) {
            logger.warn { "'localization.container' is currently set to 'empty'." }
        }

        kommandoDI.addExtend(directDI.di).addConfig { bindEagerSingleton { kommando } }

        kommando.setup(commands = commands)

        return kommando
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
        callsInPlace(di, InvocationKind.AT_LEAST_ONCE)
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