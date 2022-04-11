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

package net.ormr.kommando.commands.prefix

import dev.kord.core.event.message.MessageCreateEvent
import net.ormr.kommando.KommandoDsl

public class LiteralCommandPrefix internal constructor(
    private val literal: String,
    private val ignoreCase: Boolean,
) : CommandPrefix {
    override suspend fun parse(message: String, event: MessageCreateEvent): String? =
        literal.takeIf { message.startsWith(literal, ignoreCase = ignoreCase) }
}

@KommandoDsl
public fun CommandPrefixBuilder.literal(literal: String, ignoreCase: Boolean = false): CommandPrefix =
    LiteralCommandPrefix(literal, ignoreCase)