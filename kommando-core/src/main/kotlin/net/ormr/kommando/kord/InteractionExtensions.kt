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

package net.ormr.kommando.kord

import dev.kord.core.behavior.interaction.ActionInteractionBehavior
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.*
import dev.kord.core.entity.interaction.followup.EphemeralFollowupMessage
import dev.kord.core.entity.interaction.followup.PublicFollowupMessage
import dev.kord.core.entity.interaction.response.EphemeralMessageInteractionResponse
import dev.kord.core.entity.interaction.response.PublicMessageInteractionResponse

public suspend fun DeferredPublicMessageInteractionResponseBehavior.respond(
    message: String,
): PublicMessageInteractionResponse = this.respond { content = message }

public suspend fun DeferredEphemeralMessageInteractionResponseBehavior.respond(
    message: String,
): EphemeralMessageInteractionResponse = this.respond { content = message }

public suspend fun ActionInteractionBehavior.respondPublic(
    message: String,
): PublicMessageInteractionResponseBehavior = respondPublic { content = message }

public suspend fun ActionInteractionBehavior.respondEphemeral(
    message: String,
): EphemeralMessageInteractionResponseBehavior = respondEphemeral { content = message }

public suspend fun FollowupPermittingInteractionResponseBehavior.createPublicFollowup(
    message: String,
): PublicFollowupMessage = createPublicFollowup { content = message }

public suspend fun FollowupPermittingInteractionResponseBehavior.createEphemeralFollowup(
    message: String,
): EphemeralFollowupMessage = createEphemeralFollowup { content = message }