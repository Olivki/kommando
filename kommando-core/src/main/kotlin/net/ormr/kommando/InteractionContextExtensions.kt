/*
 * Copyright 2023-2026 Oliver Berg
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

package net.ormr.kommando

import dev.kord.common.Locale
import dev.kord.core.behavior.interaction.InteractionBehavior
import dev.kord.core.entity.interaction.Interaction

context(ctx: InteractionContext<T>)
public inline val <T : InteractionBehavior> interaction: T
    get() = ctx.interaction

/**
 * See [Interaction.locale].
 */
context(_: InteractionContext<Interaction>)
public inline val locale: Locale?
    get() = interaction.locale

/**
 * See [Interaction.guildLocale].
 */
context(_: InteractionContext<Interaction>)
public inline val guildLocale: Locale?
    get() = interaction.guildLocale