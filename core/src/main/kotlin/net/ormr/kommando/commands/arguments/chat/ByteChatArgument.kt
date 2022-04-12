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
public sealed class ByteChatArgument(
    override val description: String?,
    public val min: Byte,
    public val max: Byte,
    dummy: Dummy,
) : ChatArgument<Byte>("Byte") {
    public companion object Default : ByteChatArgument(description = null, Byte.MIN_VALUE, Byte.MAX_VALUE, Dummy) {
        public fun negative(description: String? = null, min: Byte = Byte.MIN_VALUE): ByteChatArgument =
            ByteChatArgument(description, min = min, max = -1)

        public fun nonPositive(description: String? = null, min: Byte = Byte.MIN_VALUE): ByteChatArgument =
            ByteChatArgument(description, min = min, max = 0)

        public fun positive(description: String? = null, max: Byte = Byte.MAX_VALUE): ByteChatArgument =
            ByteChatArgument(description, min = 1, max = max)

        public fun nonNegative(description: String? = null, max: Byte = Byte.MAX_VALUE): ByteChatArgument =
            ByteChatArgument(description, min = 0, max = max)
    }
}

private class ByteChatArgumentImpl(description: String?, min: Byte, max: Byte) :
    ByteChatArgument(description, min, max, Dummy)

public fun ByteChatArgument(
    description: String? = null,
    min: Byte = Byte.MIN_VALUE,
    max: Byte = Byte.MAX_VALUE,
): ByteChatArgument = ByteChatArgumentImpl(description, min, max)