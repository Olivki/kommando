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

import dev.kord.rest.builder.interaction.BaseInputChatBuilder

public class SlashChoiceArgument<T> internal constructor(
    private val argument: SlashArgumentWithChoice<T>,
    private val choices: List<SlashChoice<T>>,
) : SlashArgument<T> by argument {
    override fun BaseInputChatBuilder.buildArgument(required: Boolean) {
        with(argument) {
            buildArgumentWithChoices(choices, required)
        }
    }
}

public typealias SlashChoice<T> = Pair<String, T>

public fun <T> SlashArgumentWithChoice<T>.choices(
    first: SlashChoice<T>,
    vararg rest: SlashChoice<T>,
): SlashChoiceArgument<T> {
    require(rest.size <= 24) { "Commands can only have 25 choices max, was given ${rest.size} choices." }
    val choices = buildList(rest.size + 1) {
        add(first)
        addAll(rest)
    }
    return SlashChoiceArgument(this, choices)
}

public infix fun <T> SlashArgumentWithChoice<T>.choices(choices: List<SlashChoice<T>>): SlashChoiceArgument<T> {
    require(choices.size <= 25) { "Commands can only have 25 choices max, was given ${choices.size} choices." }
    return SlashChoiceArgument(this, choices.toList())
}