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

import com.github.h0tk3y.betterParse.combinators.and
import com.github.h0tk3y.betterParse.combinators.optional
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser

public abstract class ChatArgumentGrammar<T> @PublishedApi internal constructor() :
    Grammar<ChatArgumentParseResult<T>>() {
    public abstract val inheritedParser: Parser<T>
}

// this entire thing is rather scuffed, but we have to do it this way around to work around an issue wherein grammars
// do not actually inherit tokens from others, which really fucks with inheritance (https://github.com/h0tk3y/better-parse/issues/44)
// TODO: better name?
public inline fun <reified T> Grammar<T>.inherit(): ChatArgumentGrammar<T> {
    val parser = parser { rootParser }
    return object : ChatArgumentGrammar<T>() {
        override val inheritedParser: Parser<T> by parser

        val matchAll by regexToken(".*".toRegex(RegexOption.DOT_MATCHES_ALL))
        val matchRest by optional(matchAll use { text })

        override val rootParser: ChatArgumentParser<T> by
        (inheritedParser and matchRest) use { ChatArgumentParseResult(t1, t2) }

        override val tokens: List<Token> = this@inherit.tokens + listOf(matchAll)
    }
}
