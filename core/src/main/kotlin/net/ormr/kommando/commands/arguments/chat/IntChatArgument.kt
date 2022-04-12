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
public sealed class IntChatArgument(
    override val description: String?,
    public val min: Int,
    public val max: Int,
    dummy: Dummy,
) : ChatArgument<Int>("Int") {
    public companion object Default : IntChatArgument(description = null, Int.MIN_VALUE, Int.MAX_VALUE, Dummy) {
        public fun negative(description: String? = null, min: Int = Int.MIN_VALUE): IntChatArgument =
            IntChatArgument(description, min = min, max = -1)

        public fun nonPositive(description: String? = null, min: Int = Int.MIN_VALUE): IntChatArgument =
            IntChatArgument(description, min = min, max = 0)

        public fun positive(description: String? = null, max: Int = Int.MAX_VALUE): IntChatArgument =
            IntChatArgument(description, min = 1, max = max)

        public fun nonNegative(description: String? = null, max: Int = Int.MAX_VALUE): IntChatArgument =
            IntChatArgument(description, min = 0, max = max)
    }
}

private class IntChatArgumentImpl(description: String?, min: Int, max: Int) :
    IntChatArgument(description, min, max, Dummy)

public fun IntChatArgument(
    description: String? = null,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
): IntChatArgument = IntChatArgumentImpl(description, min, max)