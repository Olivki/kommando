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
import net.ormr.kommando.KommandoDsl
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public data class SelectMenuOptionComponent(
    override val label: String,
    public val value: String,
    public val description: String?,
    override val emoji: DiscordPartialEmoji?,
    public val isDefault: Boolean,
) : EmojiComponent, LabeledComponent

@KommandoDsl
public class SelectMenuOptionComponentBuilder @PublishedApi internal constructor(
    private val label: String,
    private val value: String,
) : EmojiBuilder {
    public var description: String? = null
    override var emoji: DiscordPartialEmoji? = null

    /**
     * Whether this option should be the default option for the select menu.
     */
    public var isDefault: Boolean = false

    @PublishedApi
    internal fun build(): SelectMenuOptionComponent = SelectMenuOptionComponent(
        label = label,
        value = value,
        description = description,
        emoji = emoji,
        isDefault = isDefault,
    )
}

@KommandoDsl
public inline fun SelectMenuComponentBuilder.option(
    label: String,
    value: String,
    builder: SelectMenuOptionComponentBuilder.() -> Unit = {},
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    addOption(SelectMenuOptionComponentBuilder(label, value).apply(builder).build())
}