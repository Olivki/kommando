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
import dev.kord.rest.builder.component.ActionRowBuilder
import net.ormr.kommando.KommandoDsl
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public data class LinkButtonComponent(
    override val label: String?,
    override val emoji: DiscordPartialEmoji?,
    public val url: String,
    override val isDisabled: Boolean,
) : Component, EmojiComponent, LabeledComponent {
    override val width: Int
        get() = 1

    override fun ActionRowBuilder.buildComponent() {
        linkButton(this@LinkButtonComponent.url) {
            label = this@LinkButtonComponent.label
            emoji = this@LinkButtonComponent.emoji
            disabled = isDisabled
        }
    }
}

@KommandoDsl
public class LinkButtonComponentBuilder @PublishedApi internal constructor(
    private val url: String,
    override var label: String?,
) : ComponentBuilder<LinkButtonComponent>(), EmojiBuilder, LabelBuilder {
    override var emoji: DiscordPartialEmoji? = null

    @PublishedApi
    override fun build(): LinkButtonComponent {
        checkLabelAndEmoji()
        return LinkButtonComponent(
            label = label,
            emoji = emoji,
            url = url,
            isDisabled = isDisabled,
        )
    }
}

@KommandoDsl
public inline fun ComponentGroupBuilder.linkButton(
    url: String,
    label: String? = null,
    builder: LinkButtonComponentBuilder.() -> Unit = {},
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    addComponent(LinkButtonComponentBuilder(url, label).apply(builder).build())
}