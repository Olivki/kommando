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

package net.ormr.kommando.components

import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.optional.optional
import dev.kord.core.entity.GuildEmoji
import dev.kord.core.entity.ReactionEmoji
import dev.kord.x.emoji.DiscordEmoji
import dev.kord.x.emoji.toReaction

public interface EmojiBuilder {
    public var emoji: DiscordPartialEmoji?

    public fun emoji(emoji: GuildEmoji) {
        this.emoji = DiscordPartialEmoji(id = emoji.id, name = null, animated = emoji.isAnimated.optional())
    }

    public fun emoji(emoji: ReactionEmoji.Unicode) {
        this.emoji = DiscordPartialEmoji(name = emoji.name, id = null)
    }

    public fun emoji(emoji: ReactionEmoji.Custom) {
        this.emoji = DiscordPartialEmoji(name = emoji.name, id = emoji.id, animated = emoji.isAnimated.optional())
    }
}

public fun EmojiBuilder.emoji(emoji: DiscordEmoji) {
    emoji(emoji.toReaction())
}