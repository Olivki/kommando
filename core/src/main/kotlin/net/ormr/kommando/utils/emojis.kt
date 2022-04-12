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

import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.optional.optional
import dev.kord.core.entity.GuildEmoji
import dev.kord.core.entity.ReactionEmoji
import dev.kord.x.emoji.DiscordEmoji
import dev.kord.x.emoji.toReaction

// TODO: Kord has 'name = null' for GuildEmojis themselves when converting, is there an actual reason for that?
public fun GuildEmoji.toDiscordPartialEmoji(): DiscordPartialEmoji =
    DiscordPartialEmoji(id, name, isAnimated.optional())

public fun ReactionEmoji.Unicode.toDiscordPartialEmoji(): DiscordPartialEmoji = DiscordPartialEmoji(name = name)

public fun ReactionEmoji.Custom.toDiscordPartialEmoji(): DiscordPartialEmoji =
    DiscordPartialEmoji(id, name, isAnimated.optional())

public fun DiscordEmoji.toDiscordPartialEmoji(): DiscordPartialEmoji = toReaction().toDiscordPartialEmoji()