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
import dev.kord.gateway.Intent
import net.ormr.kommando.KommandoBuilder
import net.ormr.kommando.KommandoContext

private data object IgnoreSelfFilter : MessageFilter {
    context(KommandoContext, MessageCreateEvent)
    override suspend fun isOk(): Boolean = message.author?.id != kommando.kord.selfId
}

/**
 * Filters away any messages created by the bot itself.
 *
 * Enabled by default if [Intent.MessageContent] is enabled in [intents][KommandoBuilder.intents].
 */
context(KommandoBuilder)
public val ignoreSelf: MessageFilter
    get() = IgnoreSelfFilter