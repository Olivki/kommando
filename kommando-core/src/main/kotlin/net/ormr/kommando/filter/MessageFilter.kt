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

import dev.kord.core.event.message.MessageCreateEvent
import net.ormr.kommando.KommandoBuilder
import net.ormr.kommando.KommandoContext

public fun interface MessageFilter {
    context(KommandoContext, MessageCreateEvent)
    public suspend fun isOk(): Boolean
}

public operator fun MessageFilter.plus(other: MessageFilter): MessageFilter = MessageFilter {
    this.isOk() && other.isOk()
}

context(KommandoBuilder)
public operator fun MessageFilter.unaryPlus() {
    messageFilters += this
}

context(KommandoBuilder)
public operator fun MessageFilter.unaryMinus() {
    messageFilters -= this
}