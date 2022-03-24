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

package net.ormr.kommando.commands.arguments.slash

import dev.kord.core.entity.interaction.InteractionCommand
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.RootInputChatBuilder

private typealias SlashDefaultValueSupplier<T> = suspend ChatInputCommandInteractionCreateEvent.() -> T

public class SlashDefaultArgument<T> internal constructor(
    private val argument: SlashArgument<T>,
    private val defaultSupplier: SlashDefaultValueSupplier<T>,
) : SlashArgument<T> by argument {
    public suspend fun getValueOrCreate(
        command: InteractionCommand,
        event: ChatInputCommandInteractionCreateEvent,
    ): T = argument.getValueOrNull(command) ?: defaultSupplier(event)

    override fun getValue(command: InteractionCommand): Nothing = throw NotImplementedError("Use 'getValueOrCreate'.")

    override fun getValueOrNull(command: InteractionCommand): Nothing =
        throw NotImplementedError("Use 'getValueOrCreate'.")

    override fun RootInputChatBuilder.buildArgument(required: Boolean) {
        with(argument) {
            buildArgument(required = false)
        }
    }
}

public infix fun <T> SlashArgument<T>.default(supplier: SlashDefaultValueSupplier<T>): SlashDefaultArgument<T> {
    require(this !is SlashDefaultArgument<*>) { "Nesting of default arguments is not allowed." }
    require(this !is SlashOptionalArgument<*>) { "Optional arguments can't be made default." }
    return SlashDefaultArgument(this, supplier)
}