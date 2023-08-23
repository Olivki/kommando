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

import dev.kord.common.entity.optional.Optional
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.rest.builder.interaction.string
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.command.CustomizableCommand
import net.ormr.kommando.localeBundle
import net.ormr.kommando.localization.BasicMessage
import net.ormr.kommando.localization.LocalizedMessage
import net.ormr.kommando.localization.Message
import net.ormr.kommando.localization.toMutableMap

public class EnumChoiceArgument<Value>(
    override val key: String,
    override val name: Message,
    override val description: Message,
    private val entries: List<Value>, // TODO: use EnumEntries once they have an intrinsic like 'enumValues' available
) : Argument<Value, String, ArgumentType.String>
        where Value : Enum<Value>,
              Value : EnumArgumentChoice {
    init {
        require(entries.isNotEmpty()) { "Choices must not be empty" }
        require(entries.size <= 25) { "Choices must not be more than 25" }
    }

    private val nameToEntry: Map<String, Value> = entries.associateByTo(hashMapOf()) { it.name }

    override val type: ArgumentType.String
        get() = ArgumentType.String

    override fun convertArgumentValue(value: String): Value = nameToEntry[value] ?: error("$value !in $nameToEntry")

    override fun convertNullableArgumentValue(value: String?): Value? = value?.let(nameToEntry::getValue)

    context(ArgumentBuildContext, BaseInputChatBuilder)
    override fun buildArgument(isRequired: Boolean) {
        val bundle = parentCommand.localeBundle
        val path = parentCommand.componentPath / "arguments" / key / "choices"
        string(defaultName, defaultDescription) {
            this.required = isRequired
            for (entry in entries) {
                val name = entry.name
                val strings = when (val message = bundle.getMessageOrNull(parentCommand, path, name)) {
                    is LocalizedMessage -> message.strings.toMutableMap()
                    else -> null
                }
                choice(entry.choiceName, name, Optional(strings))
            }
        }
    }

    override fun toString(): String =
        "EnumChoiceArgument(key='$key', name='${name.defaultString}', description='${description.defaultString}', entries=$nameToEntry)"
}

context(Cmd)
@KommandoDsl
public inline fun <reified Value, Cmd> enum(
    name: Message? = null,
    description: String,
): ArgumentBuilder<Cmd, Value, EnumChoiceArgument<Value>>
        where Value : Enum<Value>,
              Value : EnumArgumentChoice,
              Cmd : CustomizableCommand<*> =
    ArgumentHelper.newBuilder(name, BasicMessage(description)) { key, resolvedName, desc ->
        EnumChoiceArgument(key, resolvedName, desc, enumValues<Value>().asList())
    }

context(Cmd)
@KommandoDsl
public inline fun <reified Value, Cmd> enum(
    name: Message? = null,
    description: Message? = null,
): ArgumentBuilder<Cmd, Value, EnumChoiceArgument<Value>>
        where Value : Enum<Value>,
              Value : EnumArgumentChoice,
              Cmd : CustomizableCommand<Cmd> =
    ArgumentHelper.newBuilder(name, description) { key, resolvedName, desc ->
        EnumChoiceArgument(key, resolvedName, desc, enumValues<Value>().asList())
    }