/*
 * Copyright 2022 Oliver Berg
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

package net.ormr.kommando.commands.arguments

import dev.kord.common.entity.optional.Optional
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.rest.builder.interaction.string
import net.ormr.kommando.commands.Command
import net.ormr.kommando.commands.CustomizableCommand
import net.ormr.kommando.commands.delegates.ArgumentDelegateProvider
import net.ormr.kommando.commands.delegates.createDelegate
import net.ormr.kommando.localization.LocalizationResolver
import net.ormr.kommando.localization.LocalizedString

public class StringArgument(
    override val name: String,
    override val description: LocalizedString,
    override val autoComplete: AutoCompleteAction? = null,
) : ArgumentWithChoice<String, String, ArgumentType.STRING>(), AutoCompletableArgument {
    override val type: ArgumentType.STRING get() = ArgumentType.STRING

    override fun getValue(value: String): String = value

    override fun getValueOrNull(value: String?): String? = value

    override fun convertChoiceValue(value: String): String = value

    override fun BaseInputChatBuilder.buildArgument(resolver: LocalizationResolver, isRequired: Boolean) {
        string(name, resolver[description]) {
            this.autocomplete = autoComplete != null
            this.required = isRequired
        }
    }

    override fun BaseInputChatBuilder.buildArgumentWithChoices(
        resolver: LocalizationResolver,
        choices: List<ArgumentChoice<String>>,
        isRequired: Boolean,
    ) {
        string(name, resolver[description]) {
            this.required = isRequired
            for ((name, value, entries) in choices) choice(name, value, Optional(entries))
        }
    }

    override fun toString(): String = "StringArgument(name='$name', description='${description.defaultString}')"
}

context(Cmd)
        public fun <Cmd> string(
    name: String? = null,
    description: LocalizedString,
    autoComplete: AutoCompleteAction? = null,
): ArgumentDelegateProvider<Cmd, String, StringArgument>
        where Cmd : CustomizableCommand,
              Cmd : Command<*> = createDelegate(name) { StringArgument(it, description, autoComplete) }
