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

import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import net.ormr.kommando.parser.outOfBounds
import net.ormr.kommando.utils.Dummy

@Suppress("UNUSED_PARAMETER")
public sealed class ChatLongArgument(
    override val description: String? = null,
    public val min: Long = Long.MIN_VALUE,
    public val max: Long = Long.MAX_VALUE,
    dummy: Dummy,
) : ChatArgument<Long>(ArgumentGrammar.inherit()) {
    public companion object Default : ChatLongArgument(description = null, Long.MIN_VALUE, Long.MAX_VALUE, Dummy) {
        public fun negative(description: String? = null, min: Long = Long.MIN_VALUE): ChatLongArgument =
            ChatLongArgument(description, min = min, max = -1)

        public fun nonPositive(description: String? = null, min: Long = Long.MIN_VALUE): ChatLongArgument =
            ChatLongArgument(description, min = min, max = 0)

        public fun positive(description: String? = null, max: Long = Long.MAX_VALUE): ChatLongArgument =
            ChatLongArgument(description, min = 1, max = max)

        public fun nonNegative(description: String? = null, max: Long = Long.MAX_VALUE): ChatLongArgument =
            ChatLongArgument(description, min = 0, max = max)
    }

    internal object ArgumentGrammar : Grammar<Long>() {
        private val num by regexToken("-?[0-9]+")
        override val rootParser: Parser<Long> by outOfBounds(num use { text.toLong() })
    }
}

private class ChatLongArgumentImpl(description: String?, min: Long, max: Long) :
    ChatLongArgument(description, min, max, Dummy)

public fun ChatLongArgument(
    description: String? = null,
    min: Long = Long.MIN_VALUE,
    max: Long = Long.MAX_VALUE,
): ChatLongArgument = ChatLongArgumentImpl(description, min, max)