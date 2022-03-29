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

import com.github.h0tk3y.betterParse.parser.ParseException
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import net.ormr.kommando.Kommando
import net.ormr.kommando.commands.CommandParseException
import net.ormr.kommando.commands.NoSuchCommandException
import net.ormr.kommando.commands.arguments.chat.ChatArgument
import net.ormr.kommando.commands.arguments.chat.ChatOptionalArgument
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
        val commandName = commandContent.splitToSequence(WHITESPACE_REGEX).first()
        val command = chatCommands[commandName] ?: throw NoSuchCommandException(commandName)
        val argumentContent = commandContent.substringAfter(commandName)
        val thing = try {
            gamer(command.arguments, argumentContent)
        } catch (e: ParseException) {
            throw CommandParseException(e.errorResult, e)
        }
        println(thing)
    }
}

// TODO: we gotta handle a case where `rest` is null before end of arguments
// TODO: we gotta handle when `rest` is empty
// TODO: handle when 'rest' is empty before arguments are over
private suspend fun gamer(arguments: List<ChatArgument<*>>, argumentContent: String): List<Any?> {
    var currentInput: String? = argumentContent
    return buildList {
        for (argument in arguments) {
            val input = when {
                currentInput == null -> when (argument) {
                    is ChatOptionalArgument -> TODO()
                    else -> TODO()
                }
                else -> TODO()
            }
            argument.parse(input)
            /*val (value, rest) = argument.parse(input ?: break)
            input = rest
            add(value)*/
        }
    }
}