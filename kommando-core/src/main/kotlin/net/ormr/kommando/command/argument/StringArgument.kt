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
import net.ormr.kommando.command.CustomizableCommand
import net.ormr.kommando.localization.*

public class StringArgument(
    override val name: String,
    override val description: Message,
    override val autoComplete: AutoCompleteAction?,
) : AutoCompletableArgumentWithChoices<String, String, ArgumentType.String> {
    override val type: ArgumentType.String
        get() = ArgumentType.String

    override fun convertArgumentValue(value: String): String = value

    override fun convertNullableArgumentValue(value: String?): String? = value

    override fun convertChoiceValue(value: String): String = value

    context(BaseInputChatBuilder)
    override fun buildArgument(resolver: MessageResolver, isRequired: Boolean) {
        string(name, resolver[description]) {
            this.autocomplete = autoComplete != null
            this.required = isRequired
        }
    }

    context(BaseInputChatBuilder)
    override fun buildArgumentWithChoices(
        resolver: MessageResolver,
        choices: List<ArgumentChoice<String>>,
        isRequired: Boolean,
    ) {
        string(name, resolver[description]) {
            this.required = isRequired
            addChoices(choices)
        }
    }

    override fun toString(): String = "StringArgument(name='$name', description='${description.defaultString}')"
}

context(Cmd)
public fun <Cmd> string(
    name: String? = null,
    description: String,
    autoComplete: AutoCompleteAction? = null,
): ArgumentBuilder<Cmd, String, StringArgument>
        where Cmd : CustomizableCommand<*> =
    ArgumentHelper.newBuilder(name, BasicMessage(description)) { resolvedName, desc ->
        StringArgument(resolvedName, desc, autoComplete)
    }

context(Cmd)
public fun <Cmd> string(
    name: String? = null,
    description: LocalizedMessage? = null,
    autoComplete: AutoCompleteAction? = null,
): ArgumentBuilder<Cmd, String, StringArgument>
        where Cmd : CustomizableCommand<*> =
    ArgumentHelper.newBuilder(name, description) { resolvedName, desc ->
        StringArgument(resolvedName, desc, autoComplete)
    }