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

package net.ormr.kommando.parser

import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.ParseResult
import com.github.h0tk3y.betterParse.parser.Parsed

public inline fun <T, R> ParseResult<T>.fold(
    ifFailure: (ErrorResult) -> R,
    ifSuccess: (Parsed<T>) -> R,
): R = when (this) {
    is ErrorResult -> ifFailure(this)
    is Parsed -> ifSuccess(this)
}

public inline fun <T, R> ParseResult<T>.map(transformer: (T) -> R): ParseResult<R> = when (this) {
    is ErrorResult -> this
    is Parsed -> ParsedValue(transformer(value), nextPosition)
}

public inline fun <T, R> ParseResult<T>.flatMap(transformer: (value: T, nextPosition: Int) -> ParseResult<R>): ParseResult<R> =
    when (this) {
        is ErrorResult -> this
        is Parsed -> transformer(value, nextPosition)
    }

public inline fun <T> ParseResult<T>.recoverWith(transformer: (ErrorResult) -> ParseResult<T>): ParseResult<T> =
    when (this) {
        is ErrorResult -> transformer(this)
        is Parsed -> this
    }