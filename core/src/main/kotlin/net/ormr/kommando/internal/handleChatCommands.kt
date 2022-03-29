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

package net.ormr.kommando.internal

import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import net.ormr.kommando.Kommando
import net.ormr.kommando.utils.checkIntents

// TODO: do we want to throw an error at creation if chat commands have been created but no intent for MessageContent
//       has been registered?
internal suspend fun Kommando.handleChatCommands() {
    if (chatCommands.isEmpty()) return
    val prefix = prefix ?: throw IllegalArgumentException("'prefix' is not set but chat commands have been registered.")
    kord.on<MessageCreateEvent> {
        checkIntents<MessageCreateEvent>(intents)
        if (member?.isBot != false || message.author?.id == kord.selfId) return@on
        val content = message.content
        val parsedPrefix = prefix.parse(content, this) ?: return@on
        val commandContent = content.substringAfter(parsedPrefix)
        // TODO: should we throw an exception for empty command content?
        if (commandContent.isEmpty()) return@on
    }
}