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

import dev.kord.core.event.interaction.ApplicationCommandInteractionCreateEvent
import dev.kord.core.event.interaction.AutoCompleteInteractionCreateEvent
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public fun interface EventFailureHandler<Event> {
    public fun handle(cause: Exception, event: Event)
}

private typealias SlashCommandFailure = EventFailureHandler<ApplicationCommandInteractionCreateEvent>
private typealias AutoCompleteFailure = EventFailureHandler<AutoCompleteInteractionCreateEvent>

public class KommandoExceptionHandler internal constructor(
    public val slashCommandInvoke: SlashCommandFailure?,
    public val autoCompleteInvoke: AutoCompleteFailure?,
)

@KommandoDsl
public class KommandoExceptionHandlerBuilder @PublishedApi internal constructor() {
    private var slashCommandInvoke: SlashCommandFailure? = null
    private var autoCompleteInvoke: AutoCompleteFailure? = null

    @KommandoDsl
    public fun slashCommandInvoke(handler: SlashCommandFailure) {
        slashCommandInvoke = handler
    }

    @KommandoDsl
    public fun autoCompleteInvoke(handler: AutoCompleteFailure) {
        autoCompleteInvoke = handler
    }

    @PublishedApi
    internal fun build(): KommandoExceptionHandler = KommandoExceptionHandler(
        slashCommandInvoke = slashCommandInvoke,
        autoCompleteInvoke = autoCompleteInvoke,
    )
}

@KommandoDsl
public inline fun KommandoBuilder.exceptionHandler(builder: KommandoExceptionHandlerBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    exceptionHandler = KommandoExceptionHandlerBuilder().apply(builder).build()
}