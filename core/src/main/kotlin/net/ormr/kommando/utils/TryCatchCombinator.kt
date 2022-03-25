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

import com.github.h0tk3y.betterParse.lexer.TokenMatchesSequence
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.ParseResult
import com.github.h0tk3y.betterParse.parser.Parser

private typealias ExceptionMapper = (exception: Exception, tokens: TokenMatchesSequence, fromPosition: Int) -> ErrorResult

// TODO: better name?
public class TryCatchCombinator<T>(public val parser: Parser<T>, public val mapper: ExceptionMapper) : Parser<T> {
    override fun tryParse(tokens: TokenMatchesSequence, fromPosition: Int): ParseResult<T> = try {
        parser.tryParse(tokens, fromPosition)
    } catch (e: Exception) {
        mapper(e, tokens, fromPosition)
    }
}

/**
 * Uses [parser] and allows to map any exceptions thrown from it into an appropriate [ErrorResult] instance using
 * [mapper].
 */
public fun <T> tryCatch(parser: Parser<T>, mapper: ExceptionMapper): Parser<T> = TryCatchCombinator(parser, mapper)