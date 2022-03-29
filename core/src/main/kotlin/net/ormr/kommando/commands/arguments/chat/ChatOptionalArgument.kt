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

import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ParseResult
import net.ormr.kommando.parser.ParsedValue
import net.ormr.kommando.parser.recoverWith

@Suppress("UNCHECKED_CAST")
public class ChatOptionalArgument<T> internal constructor(
    private val argument: ChatArgument<T>,
) : ChatArgument<T?>(argument.grammar as ChatArgumentGrammar<T?>, "${argument.typeName}?") {
    override val description: String?
        get() = argument.description

    override suspend fun tryParse(input: String): ParseResult<ChatArgumentParseResult<T?>> =
        grammar.tryParseToEnd(input).recoverWith { ParsedValue(ChatArgumentParseResult(null, null), -1) }
}

public fun <T> ChatArgument<T>.optional(): ChatArgument<T?> {
    require(this !is ChatOptionalArgument<*>) { "Nesting of optional arguments is not allowed." }
    return ChatOptionalArgument(this)
}