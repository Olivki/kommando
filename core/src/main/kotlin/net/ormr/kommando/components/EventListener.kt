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

package net.ormr.kommando.components

import dev.kord.core.enableEvent
import dev.kord.core.event.Event
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.gateway.Intents
import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoDsl
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import dev.kord.core.on as kordOn

// entire design here is very heavily based on the Listener DSL from DiscordKt

@KommandoDsl
public class EventListenerBuilder internal constructor(public val kommando: Kommando) {
    @KommandoDsl
    public inline fun <reified T : Event> on(crossinline consumer: suspend T.() -> Unit) {
        kommando.kord.kordOn<T> {
            val intents = Intents { enableEvent<T>() }

            if (intents !in kommando.intents) {
                throw MissingEventIntentException(typeOf<T>(), intents - kommando.intents)
            }

            if (this is MessageCreateEvent && kommando.messageFilters.any { !it.isOk(this) }) return@kordOn
            consumer()
        }
    }
}

public class EventListener internal constructor(private val block: EventListenerBuilder.() -> Unit) {
    internal fun executeBlock(kommando: Kommando) {
        EventListenerBuilder(kommando).apply(block)
    }
}

@KommandoDsl
public fun eventListener(block: EventListenerBuilder.() -> Unit): EventListener = EventListener(block)

@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
public class MissingEventIntentException(
    public val eventType: KType,
    public val missingIntents: Intents,
) : RuntimeException("Intents for event $eventType is missing: ${missingIntents.values}.")