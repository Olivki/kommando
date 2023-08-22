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

@file:Suppress("NOTHING_TO_INLINE")

package net.ormr.kommando.command.argument

import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.command.CustomizableCommand

public data class ArgumentChoice<Value>(public val key: String, public val value: Value) where Value : Any {
    override fun toString(): String = "'$key' = $value"
}

// TODO: handle localization of the name
context(CustomizableCommand<*>)
@KommandoDsl
public inline infix fun <Value> String.returns(value: Value): ArgumentChoice<Value>
        where Value : Any = ArgumentChoice(this, value)