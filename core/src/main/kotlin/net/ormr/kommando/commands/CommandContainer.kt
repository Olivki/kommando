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

package net.ormr.kommando.commands

import net.ormr.kommando.KommandoDsl
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public data class CommandContainer(public val commands: List<Command>)

public class CommandContainerBuilder @PublishedApi internal constructor() {
    private val commands: MutableList<Command> = mutableListOf()

    @PublishedApi
    internal fun addCommand(command: Command) {
        commands += command
    }

    @PublishedApi
    internal fun build(): CommandContainer = CommandContainer(commands.toList())
}

@KommandoDsl
public inline fun commands(builder: CommandContainerBuilder.() -> Unit): CommandContainer {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return CommandContainerBuilder().apply(builder).build()
}