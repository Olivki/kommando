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
import dev.kord.rest.builder.component.StringSelectBuilder
import dev.kord.rest.builder.component.option
import net.ormr.kommando.KommandoDsl

public data class ComponentSelectMenuOption(
    val label: String,
    val value: String,
    val emoji: DiscordPartialEmoji?,
    val description: String?,
    val isDefault: Boolean,
) {
    context(StringSelectBuilder)
    public fun buildOption() {
        val self = this
        option(label = self.label, value = self.value) {
            description = self.description
            emoji = self.emoji
            default = self.isDefault
        }
    }
}

@KommandoDsl
public fun ComponentSelectMenuOptionsBuilder.option(
    label: String,
    value: String,
    emoji: DiscordPartialEmoji? = null,
    description: String? = null,
    isDefault: Boolean = false,
) {
    options += ComponentSelectMenuOption(
        label = label,
        value = value,
        emoji = emoji,
        description = description,
        isDefault = isDefault,
    )
}