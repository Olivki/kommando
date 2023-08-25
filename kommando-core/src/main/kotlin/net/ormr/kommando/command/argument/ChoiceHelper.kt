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
import dev.kord.rest.builder.interaction.BaseChoiceBuilder
import net.ormr.kommando.findFullComponentPath
import net.ormr.kommando.localization
import net.ormr.kommando.localization.LocalizedMessage
import net.ormr.kommando.localization.toMutableMap

context(ArgumentWithChoice<*, *, *>, ArgumentBuildContext, BaseChoiceBuilder<Value>)
internal fun <Value> addChoices(choices: List<ArgumentChoice<Value>>)
        where Value : Any {
    val bundle = parentCommand.localization
    val path = parentCommand.findFullComponentPath() / "arguments" / key / "choices"
    for ((name, value) in choices) {
        val strings = when (val message = bundle.getMessageOrNull(parentCommand, path, name)) {
            is LocalizedMessage -> message.strings.toMutableMap()
            else -> null
        }
        choice(name, value, Optional(strings))
    }
}