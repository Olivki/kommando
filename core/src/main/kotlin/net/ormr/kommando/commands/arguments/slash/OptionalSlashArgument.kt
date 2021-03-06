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
import dev.kord.rest.builder.interaction.BaseInputChatBuilder

public class OptionalSlashArgument<T> internal constructor(
    private val argument: SlashArgument<T>,
) : SlashArgument<T?> {
    override val type: SlashArgumentType<*>
        get() = argument.type

    override val name: String
        get() = argument.name

    override val description: String
        get() = argument.description

    override fun getValue(command: InteractionCommand): T? = argument.getValueOrNull(command)

    override fun BaseInputChatBuilder.buildArgument(required: Boolean) {
        with(argument) {
            buildArgument(required = false)
        }
    }
}

public fun <T> SlashArgument<T>.optional(): OptionalSlashArgument<T> {
    require(this !is OptionalSlashArgument<*>) { "Nesting of optional arguments is not allowed." }
    require(this !is DefaultSlashArgument<*>) { "Default arguments can't be made optional." }
    return OptionalSlashArgument(this)
}

@Suppress("unused")
@Deprecated(
    message = "Nesting of optional arguments is not allowed.",
    replaceWith = ReplaceWith(""),
    level = DeprecationLevel.ERROR,
)
public fun OptionalSlashArgument<*>.optional(): Nothing = error("Nesting of optional arguments is not allowed.")

@Suppress("unused")
@Deprecated(
    message = "Default arguments can't be made optional.",
    replaceWith = ReplaceWith(""),
    level = DeprecationLevel.ERROR,
)
public fun DefaultSlashArgument<*>.optional(): Nothing = error("Default arguments can't be made optional.")
