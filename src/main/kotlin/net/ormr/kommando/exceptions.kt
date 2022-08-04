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

@file:Suppress("MemberVisibilityCanBePrivate", "CanBeParameter", "NOTHING_TO_INLINE")

package net.ormr.kommando

import dev.kord.common.entity.Snowflake
import net.ormr.kommando.commands.Command
import net.ormr.kommando.commands.CommandRequestInfo
import net.ormr.kommando.commands.arguments.Argument
import net.ormr.kommando.utils.fullName
import kotlin.reflect.KClass

public open class KommandoException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

public class NoSuchCommandException(message: String) : KommandoException(message)

internal inline fun noSuchCommand(id: Snowflake, name: String): Nothing =
    throw NoSuchCommandException("No command bound to id $id, name was: $name")

public class NoSuchCommandGroupException(message: String) : KommandoException(message)

internal inline fun noSuchCommandGroup(name: String, command: Command<*>): Nothing =
    throw NoSuchCommandGroupException(
        "Command ${command::class.fullName} does not have a group named '$name' bound to it"
    )

public class NoSuchSubCommandException(message: String) : KommandoException(message)

internal inline fun noSuchSubCommand(name: String, command: Command<*>): Nothing =
    throw NoSuchSubCommandException(
        "Command ${command::class.fullName} does not have a sub-command named '$name' bound to it"
    )

public class NoSuchCommandArgumentException(message: String) : KommandoException(message)

internal inline fun noSuchCommandArgument(name: String, command: Command<*>): Nothing =
    throw NoSuchCommandArgumentException(
        "Command ${command::class.fullName} does not have an argument named '$name' bound to it"
    )

public class InvalidCommandTypeException(public val info: CommandRequestInfo, message: String) :
    KommandoException(message)

internal inline fun invalidCommandType(expected: KClass<*>, got: KClass<*>, info: CommandRequestInfo): Nothing =
    throw InvalidCommandTypeException(
        info,
        "Expected command type ${expected.fullName}, but got ${got.fullName} instead, for: $info"
    )

public class InvalidCommandArgumentTypeException(public val info: CommandArgumentRequestInfo, message: String) :
    KommandoException(message)

internal inline fun invalidCommandArgumentType(
    expected: KClass<*>,
    got: KClass<*>,
    info: CommandArgumentRequestInfo,
): Nothing = throw InvalidCommandArgumentTypeException(
    info,
    "Expected command argument type ${expected.fullName}, but got ${got.fullName} instead, for: $info"
)

public class NoAutoCompleteDefinedException(public val info: CommandArgumentRequestInfo, message: String) :
    KommandoException(message)

internal inline fun noAutoCompleteDefined(argument: Argument<*, *>, info: CommandArgumentRequestInfo): Nothing =
    throw NoAutoCompleteDefinedException(
        info,
        "Command argument $argument does not have a autoComplete callback defined, for: $info"
    )