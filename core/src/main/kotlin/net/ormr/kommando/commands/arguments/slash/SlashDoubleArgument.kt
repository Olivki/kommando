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
import dev.kord.rest.builder.interaction.number

// TODO: kord doesn't actually allow us to set min/max values for integers and doubles yet, at least not via the api
//       that we're making use of
public class SlashDoubleArgument(
    override val name: String,
    override val description: String,
) : SlashArgumentWithChoice<Double> {
    override val type: SlashArgumentType.DOUBLE
        get() = SlashArgumentType.DOUBLE

    override fun BaseInputChatBuilder.buildArgument(required: Boolean) {
        number(name, description) {
            this.required = required
        }
    }

    override fun BaseInputChatBuilder.buildArgumentWithChoices(
        choices: List<SlashChoice<Double>>,
        required: Boolean,
    ) {
        number(name, description) {
            this.required = required
            for ((name, value) in choices) choice(name, value)
        }
    }
}