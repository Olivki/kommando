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

package net.ormr.kommando.filter

import net.ormr.kommando.KommandoBuilder
import net.ormr.kommando.KommandoContext
import net.ormr.kommando.kord.KordEvent
import kotlin.reflect.typeOf

public sealed interface EventFilter<in Event>
        where Event : KordEvent {
    /**
     * Returns `true` if the [event] is ok to be processed, `false` otherwise.
     *
     * @param [event] the event to check
     */
    context(KommandoContext)
    public suspend fun isOk(event: Event): Boolean
}

@PublishedApi
internal abstract class AbstractEventFilter<Event> : EventFilter<Event>
        where Event : KordEvent {
    @PublishedApi
    internal abstract fun isFilterFor(event: KordEvent): Boolean
}

public inline operator fun <reified Event> EventFilter<Event>.plus(
    other: EventFilter<Event>,
): EventFilter<Event>
        where Event : KordEvent = EventFilter {
    this.isOk(it) && other.isOk(it)
}

/**
 * Returns a new [EventFilter] with [predicate] as the [isOk][EventFilter.isOk] function.
 *
 * @param [predicate] the predicate to use, should return `true` if the `event` is ok to be processed, `false` otherwise
 */
public inline fun <reified Event : KordEvent> EventFilter(
    crossinline predicate: suspend context(KommandoContext) (event: Event) -> Boolean,
): EventFilter<Event> = object : AbstractEventFilter<KordEvent>() {
    context(KommandoContext)
    override suspend fun isOk(event: KordEvent): Boolean {
        require(event is Event) { "Event ($event) is not type ${typeOf<Event>()}. Was 'isFilterFor' skipped?" }
        return predicate(this@KommandoContext, event)
    }

    override fun isFilterFor(event: KordEvent): Boolean = event is Event
}

context(KommandoBuilder)
public operator fun EventFilter<*>.unaryPlus() {
    eventFilters += this
}

context(KommandoBuilder)
public operator fun EventFilter<*>.unaryMinus() {
    eventFilters -= this
}