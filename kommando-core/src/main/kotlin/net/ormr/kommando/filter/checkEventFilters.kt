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

import net.ormr.kommando.KommandoContext
import net.ormr.kommando.kord.KordEvent
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

context(KommandoContext)
@PublishedApi
@Suppress("UNCHECKED_CAST")
internal suspend inline fun checkEventFilters(filters: List<EventFilter<*>>, event: KordEvent, action: () -> Unit) {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }

    for (i in filters.indices) {
        val filter = filters[i] as AbstractEventFilter<KordEvent>
        if (!filter.isFilterFor(event)) continue
        if (!filter.isOk(event)) {
            action()
            break
        }
    }
}