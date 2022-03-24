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
import dev.kord.rest.builder.interaction.RootInputChatBuilder
import dev.kord.rest.builder.interaction.string
import net.ormr.kommando.commands.arguments.ChoiceAdapter
import kotlin.reflect.KClass

public class SlashEnumArgument<T> @PublishedApi internal constructor(
    override val name: String,
    override val description: String,
    public val enumClass: KClass<T>,
    public val enumValues: List<T>,
) : SlashArgument<T>
        where T : Enum<T>,
              T : ChoiceAdapter {
    override val type: SlashArgumentType.STRING
        get() = SlashArgumentType.STRING

    private val nameToConstant = enumValues.associateBy { it.name }

    override fun getValue(command: InteractionCommand): T = nameToConstant.getValue(type.getValue(command, name))

    override fun getValueOrNull(command: InteractionCommand): T? =
        SlashArgumentType.STRING.getValueOrNull(command, name)?.let(nameToConstant::getValue)

    override fun RootInputChatBuilder.buildArgument(required: Boolean) {
        string(name, description) {
            this.required = required
            for (constant in enumValues) choice(constant.choiceName, constant.name)
        }
    }
}

@Suppress("FunctionName")
public inline fun <reified T> SlashEnumArgument(name: String, description: String): SlashEnumArgument<T>
        where T : Enum<T>,
              T : ChoiceAdapter = SlashEnumArgument(name, description, T::class, enumValues<T>().toList())