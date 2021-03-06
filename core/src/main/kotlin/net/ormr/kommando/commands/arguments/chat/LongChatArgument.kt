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

package net.ormr.kommando.commands.arguments.chat

import net.ormr.kommando.utils.Dummy

@Suppress("UNUSED_PARAMETER")
public sealed class LongChatArgument(
    override val description: String?,
    public val min: Long,
    public val max: Long,
    dummy: Dummy,
) : ChatArgument<Long>("Long") {
    public companion object Default : LongChatArgument(description = null, Long.MIN_VALUE, Long.MAX_VALUE, Dummy) {
        public fun negative(description: String? = null, min: Long = Long.MIN_VALUE): LongChatArgument =
            LongChatArgument(description, min = min, max = -1)

        public fun nonPositive(description: String? = null, min: Long = Long.MIN_VALUE): LongChatArgument =
            LongChatArgument(description, min = min, max = 0)

        public fun positive(description: String? = null, max: Long = Long.MAX_VALUE): LongChatArgument =
            LongChatArgument(description, min = 1, max = max)

        public fun nonNegative(description: String? = null, max: Long = Long.MAX_VALUE): LongChatArgument =
            LongChatArgument(description, min = 0, max = max)
    }
}

private class LongChatArgumentImpl(description: String?, min: Long, max: Long) :
    LongChatArgument(description, min, max, Dummy)

public fun LongChatArgument(
    description: String? = null,
    min: Long = Long.MIN_VALUE,
    max: Long = Long.MAX_VALUE,
): LongChatArgument = LongChatArgumentImpl(description, min, max)