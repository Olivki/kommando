/*
 * Copyright 2023 Oliver Berg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ormr.kommando.command.argument

import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import net.ormr.kommando.command.CustomizableCommand

// TODO: this might be putting the wrong values in
public class ChoiceArgument<Value, ArgValue, out ArgType>(
    private val delegate: ArgumentWithChoice<Value, ArgValue, ArgType>,
    choices: List<ArgumentChoice<Value>>,
) : Argument<Value, ArgValue, ArgType> by delegate
        where Value : Any,
              ArgValue : Any,
              ArgType : ChoiceArgumentType<ArgValue> {
    init {
        require(choices.isNotEmpty()) { "Choices must not be empty" }
        require(choices.size <= 25) { "Choices must not be more than 25" }
    }

    // 'copy' on data classes doesn't change the returned generics, so we can't use it for this
    private val choices: List<ArgumentChoice<ArgValue>> =
        choices.map { ArgumentChoice(it.key, delegate.convertChoiceValue(it.value)) }

    override fun convertArgumentValue(value: ArgValue): Nothing = noConversionSupport()

    override fun convertNullableArgumentValue(value: ArgValue?): Nothing = noNullableConversionSupport()

    context(ArgumentBuildContext, BaseInputChatBuilder)
    override fun buildArgument(isRequired: Boolean) {
        delegate.buildArgumentWithChoices(choices, isRequired)
    }

    override fun toString(): String = "WithChoices<$delegate, $choices>"
}

context(Cmd)
public fun <Cmd, Value, ArgValue, ArgType, Arg> ArgumentBuilder<Cmd, Value, Arg>.choices(
    first: ArgumentChoice<Value>,
    vararg rest: ArgumentChoice<Value>,
): ArgumentBuilder<Cmd, Value, ChoiceArgument<Value, ArgValue, ArgType>>
        where Cmd : CustomizableCommand<*>,
              Value : Any,
              ArgValue : Any,
              ArgType : ChoiceArgumentType<ArgValue>,
              Arg : ArgumentWithChoice<Value, ArgValue, ArgType> {
    val choices = buildList(rest.size + 1) {
        add(first)
        addAll(rest)
    }
    return ArgumentHelper.newBuilder(name, description) { key, name, desc ->
        ChoiceArgument(createArgument(key, name, desc), choices)
    }
}