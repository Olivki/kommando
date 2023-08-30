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

import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.rest.builder.component.ActionRowBuilder
import net.ormr.kommando.KommandoDsl

public data class ComponentLinkButton(
    override val id: String,
    override val label: String?,
    override val emoji: DiscordPartialEmoji?,
    public val url: String,
    override val isDisabled: Boolean,
) : VisibleComponent<ComponentLinkButton>, LabeledComponent<ComponentLinkButton>, EmojiComponent<ComponentLinkButton> {
    override val width: ComponentWidth
        get() = ComponentWidth.ONE

    override fun withDisabled(isDisabled: Boolean): ComponentLinkButton = copy(isDisabled = isDisabled)

    context(ActionRowBuilder)
    override fun buildComponent() {
        val self = this
        linkButton(url = url) {
            label = self.label
            emoji = self.emoji
            disabled = self.isDisabled
        }
    }
}

@KommandoDsl
public fun VisibleComponentContainerBuilder.linkButton(
    id: String,
    url: String,
    label: String,
    emoji: DiscordPartialEmoji,
    isDisabled: Boolean = false,
) {
    linkButton0(
        id = id,
        url = url,
        label = label,
        emoji = emoji,
        isDisabled = isDisabled,
    )
}

@KommandoDsl
public fun VisibleComponentContainerBuilder.linkButton(
    id: String,
    url: String,
    label: String,
    isDisabled: Boolean = false,
) {
    linkButton0(
        id = id,
        url = url,
        label = label,
        emoji = null,
        isDisabled = isDisabled,
    )
}

@KommandoDsl
public fun VisibleComponentContainerBuilder.linkButton(
    id: String,
    url: String,
    emoji: DiscordPartialEmoji,
    isDisabled: Boolean = false,
) {
    linkButton0(
        id = id,
        url = url,
        label = null,
        emoji = emoji,
        isDisabled = isDisabled,
    )
}

@KommandoDsl
private fun VisibleComponentContainerBuilder.linkButton0(
    id: String,
    url: String,
    label: String?,
    emoji: DiscordPartialEmoji?,
    isDisabled: Boolean = false,
) {
    addVisible(id) {
        ComponentLinkButton(
            id = id,
            label = label,
            emoji = emoji,
            url = url,
            isDisabled = isDisabled,
        )
    }
}