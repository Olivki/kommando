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

package net.ormr.kommando.utils

import dev.kord.common.entity.MessageFlag
import dev.kord.core.behavior.interaction.ActionInteractionBehavior
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.behavior.interaction.response.*
import dev.kord.core.entity.interaction.followup.EphemeralFollowupMessage
import dev.kord.core.entity.interaction.followup.PublicFollowupMessage

/**
 * Responds to the interaction with a public [message].
 */
public suspend fun ActionInteractionBehavior.respondPublic(message: String): PublicMessageInteractionResponseBehavior =
    respondPublic { content = message }

/**
 * Responds to the interaction with an [ephemeral][MessageFlag.Ephemeral] [message].
 */
public suspend fun ActionInteractionBehavior.respondEphemeral(message: String): EphemeralMessageInteractionResponseBehavior =
    respondEphemeral { content = message }

/**
 * Follows up an interaction response by sending a public [message].
 */
public suspend fun FollowupPermittingInteractionResponseBehavior.createPublicFollowup(message: String): PublicFollowupMessage =
    createPublicFollowup { content = message }

/**
 * Follows up an interaction response by sending an [ephemeral][MessageFlag.Ephemeral] [message].
 */
public suspend fun FollowupPermittingInteractionResponseBehavior.createEphemeralFollowup(message: String): EphemeralFollowupMessage =
    createEphemeralFollowup { content = message }