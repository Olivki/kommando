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
import net.ormr.kommando.localization.MessageResolver

public class ChoiceArgument<Value, ArgValue, out ArgType>(
    private val delegate: ArgumentWithChoice<Value, ArgValue, ArgType>,
    choices: List<ArgumentChoice<Value>>,
) : Argument<Value, ArgValue, ArgType> by delegate
        where Value : Any,
              ArgValue : Any,
              ArgType : ChoiceArgumentType<ArgValue> {
    // 'copy' on data classes doesn't change the returned generics, so we can't use it for this
    private val choices: List<ArgumentChoice<ArgValue>> =
        choices.map { ArgumentChoice(it.defaultName, delegate.convertChoiceValue(it.value), it.strings) }

    override fun convertArgumentValue(value: ArgValue): Nothing = noConversionSupport()

    override fun convertNullableArgumentValue(value: ArgValue?): Nothing = noNullableConversionSupport()

    context(BaseInputChatBuilder)
    override fun buildArgument(resolver: MessageResolver, isRequired: Boolean) {
        delegate.buildArgumentWithChoices(resolver, choices, isRequired)
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
    return ArgumentHelper.newBuilder(name, description) { name, desc ->
        ChoiceArgument(argumentFactory.create(name, desc), choices)
    }
}