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
import dev.kord.rest.builder.interaction.string
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.command.CustomizableCommand
import net.ormr.kommando.localization.BasicMessage
import net.ormr.kommando.localization.LocalizedMessage
import net.ormr.kommando.localization.Message

public class StringArgument(
    override val key: String,
    override val name: Message,
    override val description: Message,
    override val min: Int?,
    override val max: Int?,
    override val autoComplete: AutoCompleteAction?,
) : AutoCompletableArgumentWithChoices<String, String, ArgumentType.String>,
    ArgumentWithRange<Int, String, String, ArgumentType.String> {
    override val type: ArgumentType.String
        get() = ArgumentType.String

    override fun convertArgumentValue(value: String): String = value

    override fun convertNullableArgumentValue(value: String?): String? = value

    override fun convertChoiceValue(value: String): String = value

    context(ArgumentBuildContext, BaseInputChatBuilder)
    override fun buildArgument(isRequired: Boolean) {
        string(defaultName, defaultDescription) {
            registerLocalizations()
            this.autocomplete = autoComplete != null
            this.minLength = min
            this.maxLength = max
            this.required = isRequired
        }
    }

    context(ArgumentBuildContext, BaseInputChatBuilder)
    override fun buildArgumentWithChoices(
        choices: List<ArgumentChoice<String>>,
        isRequired: Boolean,
    ) {
        string(defaultName, defaultDescription) {
            registerLocalizations()
            this.required = isRequired
            // TODO: do we add min & max for choice ones?
            this.minLength = min
            this.maxLength = max
            addChoices(choices)
        }
    }

    override fun toString(): String =
        "StringArgument(key='$key', name='${name.defaultString}', description='${description.defaultString}', min=$min, max=$max)"
}

context(Cmd)
@KommandoDsl
public fun <Cmd> string(
    name: Message? = null,
    description: String,
    min: Int? = null,
    max: Int? = null,
    autoComplete: AutoCompleteAction? = null,
): ArgumentBuilder<Cmd, String, StringArgument>
        where Cmd : CustomizableCommand<*> =
    ArgumentHelper.newBuilder(name, BasicMessage(description)) { key, resolvedName, desc ->
        StringArgument(key, resolvedName, desc, min, max, autoComplete)
    }

context(Cmd)
@KommandoDsl
public fun <Cmd> string(
    name: Message? = null,
    description: LocalizedMessage? = null,
    min: Int? = null,
    max: Int? = null,
    autoComplete: AutoCompleteAction? = null,
): ArgumentBuilder<Cmd, String, StringArgument>
        where Cmd : CustomizableCommand<*> =
    ArgumentHelper.newBuilder(name, description) { key, resolvedName, desc ->
        StringArgument(key, resolvedName, desc, min, max, autoComplete)
    }