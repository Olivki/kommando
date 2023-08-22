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

context(BaseChoiceBuilder<Value>)
internal fun <Value> addChoices(choices: List<ArgumentChoice<Value>>)
        where Value : Any {
    for ((name, value, strings) in choices) {
        // TODO: do we want to use 'Missing' instead of 'Null'?
        choice(name, value, if (strings.isEmpty()) Optional(strings.asMap()) else Optional.Missing())
    }
}