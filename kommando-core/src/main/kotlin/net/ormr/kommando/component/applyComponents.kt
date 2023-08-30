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
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.create.MessageCreateBuilder
import dev.kord.rest.builder.message.modify.MessageModifyBuilder
import kotlinx.coroutines.launch
import net.ormr.kommando.KommandoContext
import net.ormr.kommando.kord.MessageEntity

// MessageCreateBuilder

/**
 * Applies the given [group] to this message.
 *
 * Note that this will override any existing components on the message.
 */
context(KommandoContext)
public fun MessageCreateBuilder.applyComponents(group: ComponentGroup) {
    useComponents(group)
}

/**
 * Applies the given [group] to this message.
 *
 * Note that this will override any existing components on the message.
 */
context(KommandoContext)
public fun MessageModifyBuilder.applyComponents(group: ComponentGroup) {
    useComponents(group)
}

// Message

/**
 * Applies the given [group] to this message.
 *
 * Note that this will override any existing components on the message.
 */
context(KommandoContext)
public suspend fun MessageEntity.applyComponents(
    group: ComponentGroup,
): MessageEntity = apply {
    edit {
        useComponents(group)
    }
}

// PublicFollowupMessage

/**
 * Applies the given [group] to this message.
 *
 * Note that this will override any existing components on the message.
 */
context(KommandoContext)
public suspend fun PublicFollowupMessage.applyComponents(
    group: ComponentGroup,
): PublicFollowupMessage = apply {
    edit {
        useComponents(group)
    }
}

// EphemeralFollowupMessage

/**
 * Applies the given [group] to this message.
 *
 * Note that this will override any existing components on the message.
 */
context(KommandoContext)
public suspend fun EphemeralFollowupMessage.applyComponents(
    group: ComponentGroup,
): EphemeralFollowupMessage = apply {
    edit {
        useComponents(group)
    }
}

// PublicMessageInteractionResponseBehavior

/**
 * Applies the given [group] to this message.
 *
 * Note that this will override any existing components on the message.
 */
context(KommandoContext)
public suspend fun PublicMessageInteractionResponseBehavior.applyComponents(
    group: ComponentGroup,
): PublicMessageInteractionResponseBehavior = apply {
    edit {
        useComponents(group)
    }
}

// EphemeralMessageInteractionResponseBehavior

/**
 * Applies the given [group] to this message.
 *
 * Note that this will override any existing components on the message.
 */
context(KommandoContext)
public suspend fun EphemeralMessageInteractionResponseBehavior.applyComponents(
    group: ComponentGroup,
): EphemeralMessageInteractionResponseBehavior = apply {
    edit {
        useComponents(group)
    }
}

context(KommandoContext)
@PublishedApi
internal fun MessageCreateBuilder.useComponents(group: ComponentGroup) {
    useComponents(group, components)
}

context(KommandoContext)
@PublishedApi
internal fun MessageModifyBuilder.useComponents(group: ComponentGroup) {
    val components = this.components ?: mutableListOf()
    useComponents(group, components)
    this.components = components
}

context(KommandoContext)
internal fun useComponents(
    group: ComponentGroup,
    builders: MutableList<MessageComponentBuilder>,
) {
    builders.clear()
    group.rows.forEach { row ->
        val builder = ActionRowBuilder().apply {
            row.forEach {
                it.buildComponent()
            }
        }
        builders.add(builder)
    }
    kommando.kord.launch {
        kommando.componentStorage.addGroup(group)
    }
}