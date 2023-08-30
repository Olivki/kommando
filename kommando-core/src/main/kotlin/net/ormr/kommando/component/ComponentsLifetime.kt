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

import dev.kord.core.behavior.interaction.response.EphemeralMessageInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.PublicMessageInteractionResponseBehavior
import dev.kord.core.entity.interaction.followup.EphemeralFollowupMessage
import dev.kord.core.entity.interaction.followup.PublicFollowupMessage
import net.ormr.kommando.KommandoContext
import net.ormr.kommando.kord.MessageEntity
import kotlin.time.Duration

context(KommandoContext)
private inline val timeout: Duration
    get() = kommando.componentStorage.timeout

// Message

context(KommandoContext)
public fun MessageEntity.disableComponentsAfter(
    duration: Duration,
    group: ComponentGroup,
    shouldUnregister: Boolean = true,
): MessageEntity = apply {
    launchLifetimeManager(group, this, ComponentLifetimeManager.DisableAfter(duration, shouldUnregister))
}

context(KommandoContext)
public fun MessageEntity.disableComponentsAfterTimeout(group: ComponentGroup): MessageEntity = apply {
    disableComponentsAfter(timeout, group, shouldUnregister = false)
}

context(KommandoContext)
public fun MessageEntity.removeComponentsAfter(
    duration: Duration,
    group: ComponentGroup,
    shouldUnregister: Boolean = true,
): MessageEntity = apply {
    launchLifetimeManager(group, this, ComponentLifetimeManager.RemoveAfter(duration, shouldUnregister))
}

context(KommandoContext)
public fun MessageEntity.removeComponentsAfterTimeout(group: ComponentGroup): MessageEntity = apply {
    removeComponentsAfter(timeout, group, shouldUnregister = false)
}

// PublicFollowupMessage

context(KommandoContext)
public fun PublicFollowupMessage.disableComponentsAfter(
    duration: Duration,
    group: ComponentGroup,
    shouldUnregister: Boolean = true,
): PublicFollowupMessage = apply {
    launchLifetimeManager(group, this, ComponentLifetimeManager.DisableAfter(duration, shouldUnregister))
}

context(KommandoContext)
public fun PublicFollowupMessage.disableComponentsAfterTimeout(group: ComponentGroup): PublicFollowupMessage = apply {
    disableComponentsAfter(timeout, group, shouldUnregister = false)
}

context(KommandoContext)
public fun PublicFollowupMessage.removeComponentsAfter(
    duration: Duration,
    group: ComponentGroup,
    shouldUnregister: Boolean = true,
): PublicFollowupMessage = apply {
    launchLifetimeManager(group, this, ComponentLifetimeManager.RemoveAfter(duration, shouldUnregister))
}

context(KommandoContext)
public fun PublicFollowupMessage.removeComponentsAfterTimeout(
    group: ComponentGroup,
): PublicFollowupMessage = apply {
    removeComponentsAfter(timeout, group, shouldUnregister = false)
}

// EphemeralFollowupMessage

context(KommandoContext)
public fun EphemeralFollowupMessage.disableComponentsAfter(
    duration: Duration,
    group: ComponentGroup,
    shouldUnregister: Boolean = true,
): EphemeralFollowupMessage = apply {
    launchLifetimeManager(group, this, ComponentLifetimeManager.DisableAfter(duration, shouldUnregister))
}

context(KommandoContext)
public fun EphemeralFollowupMessage.disableComponentsAfterTimeout(
    group: ComponentGroup,
): EphemeralFollowupMessage = apply {
    disableComponentsAfter(timeout, group, shouldUnregister = false)
}

context(KommandoContext)
public fun EphemeralFollowupMessage.removeComponentsAfter(
    duration: Duration,
    group: ComponentGroup,
    shouldUnregister: Boolean = true,
): EphemeralFollowupMessage = apply {
    launchLifetimeManager(group, this, ComponentLifetimeManager.RemoveAfter(duration, shouldUnregister))
}

context(KommandoContext)
public fun EphemeralFollowupMessage.removeComponentsAfterTimeout(
    group: ComponentGroup,
): EphemeralFollowupMessage = apply {
    removeComponentsAfter(timeout, group, shouldUnregister = false)
}

// PublicMessageInteractionResponseBehavior

context(KommandoContext)
public fun PublicMessageInteractionResponseBehavior.disableComponentsAfter(
    duration: Duration,
    group: ComponentGroup,
    shouldUnregister: Boolean = true,
): PublicMessageInteractionResponseBehavior = apply {
    launchLifetimeManager(group, this, ComponentLifetimeManager.DisableAfter(duration, shouldUnregister))
}

context(KommandoContext)
public fun PublicMessageInteractionResponseBehavior.disableComponentsAfterTimeout(
    group: ComponentGroup,
): PublicMessageInteractionResponseBehavior = apply {
    disableComponentsAfter(timeout, group, shouldUnregister = false)
}

context(KommandoContext)
public fun PublicMessageInteractionResponseBehavior.removeComponentsAfter(
    duration: Duration,
    group: ComponentGroup,
    shouldUnregister: Boolean = true,
): PublicMessageInteractionResponseBehavior = apply {
    launchLifetimeManager(group, this, ComponentLifetimeManager.RemoveAfter(duration, shouldUnregister))
}

context(KommandoContext)
public fun PublicMessageInteractionResponseBehavior.removeComponentsAfterTimeout(
    group: ComponentGroup,
): PublicMessageInteractionResponseBehavior = apply {
    removeComponentsAfter(timeout, group, shouldUnregister = false)
}

// EphemeralMessageInteractionResponseBehavior

context(KommandoContext)
public fun EphemeralMessageInteractionResponseBehavior.disableComponentsAfter(
    duration: Duration,
    group: ComponentGroup,
    shouldUnregister: Boolean = true,
): EphemeralMessageInteractionResponseBehavior = apply {
    launchLifetimeManager(group, this, ComponentLifetimeManager.DisableAfter(duration, shouldUnregister))
}

context(KommandoContext)
public fun EphemeralMessageInteractionResponseBehavior.disableComponentsAfterTimeout(
    group: ComponentGroup,
): EphemeralMessageInteractionResponseBehavior = apply {
    disableComponentsAfter(timeout, group, shouldUnregister = false)
}

context(KommandoContext)
public fun EphemeralMessageInteractionResponseBehavior.removeComponentsAfter(
    duration: Duration,
    group: ComponentGroup,
    shouldUnregister: Boolean = true,
): EphemeralMessageInteractionResponseBehavior = apply {
    launchLifetimeManager(group, this, ComponentLifetimeManager.RemoveAfter(duration, shouldUnregister))
}

context(KommandoContext)
public fun EphemeralMessageInteractionResponseBehavior.removeComponentsAfterTimeout(
    group: ComponentGroup,
): EphemeralMessageInteractionResponseBehavior = apply {
    removeComponentsAfter(timeout, group, shouldUnregister = false)
}

// misc

context(KommandoContext)
internal fun launchLifetimeManager(
    group: ComponentGroup,
    message: MessageEntity,
    lifetime: ComponentLifetimeManager,
) {
    lifetime.launch(group, LifetimeMessage.Message(message))
}

context(KommandoContext)
internal fun launchLifetimeManager(
    group: ComponentGroup,
    message: PublicFollowupMessage,
    lifetime: ComponentLifetimeManager,
) {
    lifetime.launch(group, LifetimeMessage.PublicFollowup(message))
}

context(KommandoContext)
internal fun launchLifetimeManager(
    group: ComponentGroup,
    message: EphemeralFollowupMessage,
    lifetime: ComponentLifetimeManager,
) {
    lifetime.launch(group, LifetimeMessage.EphemeralFollowup(message))
}

context(KommandoContext)
internal fun launchLifetimeManager(
    group: ComponentGroup,
    message: PublicMessageInteractionResponseBehavior,
    lifetime: ComponentLifetimeManager,
) {
    lifetime.launch(group, LifetimeMessage.PublicResponse(message))
}

context(KommandoContext)
internal fun launchLifetimeManager(
    group: ComponentGroup,
    message: EphemeralMessageInteractionResponseBehavior,
    lifetime: ComponentLifetimeManager,
) {
    lifetime.launch(group, LifetimeMessage.EphemeralResponse(message))
}