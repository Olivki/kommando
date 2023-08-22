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

import net.ormr.kommando.command.CustomizableCommand
import net.ormr.kommando.localization.LocalizedStrings
import net.ormr.kommando.localization.emptyLocalizedStrings

public data class ArgumentChoice<Value>(
    public val defaultName: String,
    public val value: Value,
    public val strings: LocalizedStrings,
) where Value : Any {
    override fun toString(): String = "'$defaultName' = $value"
}

// TODO: handle localization of the name
context(CustomizableCommand<*>)
public infix fun <Value> String.means(value: Value): ArgumentChoice<Value>
        where Value : Any = ArgumentChoice(this, value, emptyLocalizedStrings())