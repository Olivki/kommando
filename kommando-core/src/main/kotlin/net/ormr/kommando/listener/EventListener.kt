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

package net.ormr.kommando.listener

import dev.kord.core.event.message.MessageCreateEvent
import net.ormr.kommando.AbstractElement
import net.ormr.kommando.ElementPath
import net.ormr.kommando.KommandoBuilder
import net.ormr.kommando.util.checkIntents
import dev.kord.core.event.Event as KordEvent
import dev.kord.core.on as kordOn

public abstract class EventListener(public val id: String) : AbstractElement() {
    final override val elementPath: ElementPath
        get() = ElementPath("listener", id)

    public inline fun <reified Event> on(crossinline consumer: suspend Event.() -> Unit)
            where Event : KordEvent {
        kommando.kord.kordOn<Event> {
            checkIntents<Event>(kommando.intents)

            if (this is MessageCreateEvent && kommando.messageFilters.any { !it.isOk() }) return@kordOn
            consumer()
        }
    }

    public abstract suspend fun register()
}

context(KommandoBuilder)
public operator fun EventListener.unaryPlus() {
    eventListeners += this
}
