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

package net.ormr.kommando.internal

import net.ormr.kommando.command.*
import net.ormr.kommando.command.argument.Argument
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// hacky type narrowing

/**
 * Returns the backing map of the [CommandArgumentRegistry] that is used by this [Command].
 *
 * This is different from `findArguments` in that it does not create a new map, but instead returns the backing map.
 */
internal inline fun Command<*>.findDirectArguments(): Map<String, Argument<*, *, *>> = findRegistry().asMap()

internal inline fun Command<*>.findRegistry(): CommandArgumentRegistry = fixCommand().registry

internal inline fun CommandGroup<*>.fix(): CommandGroup<SuperCommand<*, *>> = this

internal inline fun SubCommand<*, *>.fixSubCommand(): AbstractSubCommand<*, InheritableCommandComponent> =
    fixType { "SubCommand" }

internal inline fun Command<*>.fixCommand(): AbstractCommand<*> = fixType { "Command" }

internal inline fun <reified From, reified To> From.fixType(name: () -> String): To
        where From : Any, To : From {
    contract {
        callsInPlace(name, InvocationKind.AT_MOST_ONCE)
    }
    check(this is To) { "${name()} '${this::class}' is not an instance of '${To::class}'" }
    return this
}