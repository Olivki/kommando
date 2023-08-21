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

package net.ormr.kommando.command

import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.EphemeralMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.PublicMessageInteractionResponseBehavior
import dev.kord.rest.builder.message.create.InteractionResponseCreateBuilder
import net.ormr.kommando.kord.respondEphemeral
import net.ormr.kommando.kord.respondPublic
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

context(CommandContext<*>)
public suspend fun respondPublic(
    message: String,
): PublicMessageInteractionResponseBehavior = interaction.respondPublic(message)

context(CommandContext<*>)
public suspend inline fun respondPublic(
    builder: InteractionResponseCreateBuilder.() -> Unit,
): PublicMessageInteractionResponseBehavior {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return interaction.respondPublic(builder)
}

context(CommandContext<*>)
public suspend fun respondEphemeral(
    message: String,
): EphemeralMessageInteractionResponseBehavior = interaction.respondEphemeral(message)

context(CommandContext<*>)
public suspend inline fun respondEphemeral(
    builder: InteractionResponseCreateBuilder.() -> Unit,
): EphemeralMessageInteractionResponseBehavior {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return interaction.respondEphemeral(builder)
}