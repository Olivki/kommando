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

import com.github.h0tk3y.betterParse.parser.ParseResult
import com.github.h0tk3y.betterParse.parser.toParsedOrThrow
import net.ormr.kommando.commands.arguments.CommandArgument

// TODO: do we want to store the result of 'ArgumentGrammar.inherit()' inside of a property inside of the companion
//       objects of the children classes of this? Might make it more efficient, but I'm not sure if the efficiency gain
//       is that huge.
// TODO: add 'suspend fun getExamples()' function
public abstract class ChatArgument<T>(
    public val grammar: ChatArgumentGrammar<T>,
    public val typeName: String,
) : CommandArgument<T> {
    public abstract val description: String?

    public abstract suspend fun tryParse(input: String): ParseResult<ChatArgumentParseResult<T>>

    public suspend fun parse(input: String): ChatArgumentParseResult<T> = tryParse(input).toParsedOrThrow().value
}