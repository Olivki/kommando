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

package net.ormr.kommando.component

import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.followup.edit
import dev.kord.core.behavior.interaction.response.EphemeralMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.PublicMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.edit
import dev.kord.core.entity.interaction.followup.EphemeralFollowupMessage
import dev.kord.core.entity.interaction.followup.PublicFollowupMessage
import dev.kord.rest.builder.message.modify.MessageModifyBuilder
import net.ormr.kommando.kord.MessageEntity
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Wrapper for the different messages that can be `edit`ed.
 *
 * This is needed because Kord doesn't expose a common interface for these types.
 */
@PublishedApi
internal sealed interface LifetimeMessage {
    data class Message(val message: MessageEntity) : LifetimeMessage

    data class PublicFollowup(val message: PublicFollowupMessage) : LifetimeMessage

    data class EphemeralFollowup(val message: EphemeralFollowupMessage) : LifetimeMessage

    data class PublicResponse(val message: PublicMessageInteractionResponseBehavior) : LifetimeMessage

    data class EphemeralResponse(val message: EphemeralMessageInteractionResponseBehavior) : LifetimeMessage
}

internal suspend inline fun LifetimeMessage.edit(block: MessageModifyBuilder.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    when (this) {
        is LifetimeMessage.Message -> message.edit(block)
        is LifetimeMessage.PublicFollowup -> message.edit(block)
        is LifetimeMessage.EphemeralFollowup -> message.edit(block)
        is LifetimeMessage.PublicResponse -> message.edit(block)
        is LifetimeMessage.EphemeralResponse -> message.edit(block)
    }
}