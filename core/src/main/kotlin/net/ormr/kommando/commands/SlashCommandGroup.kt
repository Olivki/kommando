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

public data class SlashCommandGroup<S : SlashSubCommand<*, *>>(
    public val name: String,
    public val description: String,
    public val subCommands: Map<String, S>,
)

public class SlashCommandGroupBuilder<S : SlashSubCommand<*, *>> @PublishedApi internal constructor(
    private val name: String,
    private val description: String,
) {
    private val subCommands: MutableMap<String, S> = hashMapOf()

    @PublishedApi
    internal fun addSubCommand(subCommand: S) {
        subCommands[subCommand.name] = subCommand
    }

    @PublishedApi
    internal fun build(): SlashCommandGroup<S> = SlashCommandGroup(name, description, subCommands.toMap())
}

@KommandoDsl
public inline fun <S : SlashSubCommand<*, *>> SlashCommandBuilder<*, S, *, *>.group(
    name: String,
    description: String,
    builder: SlashCommandGroupBuilder<S>.() -> Unit,
) {
    addGroup(SlashCommandGroupBuilder<S>(name, description).apply(builder).build())
}
