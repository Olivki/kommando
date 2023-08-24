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

package net.ormr.kommando.command

import net.ormr.kommando.KommandoBuilder
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.command.factory.CommandFactory
import net.ormr.kommando.command.permission.DefaultCommandPermissions
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KommandoDsl
public class CommandsBuilder @PublishedApi internal constructor() {
    @PublishedApi
    internal var defaultCommandPermissions: DefaultCommandPermissions? = null

    @PublishedApi
    internal var nameConverters: CommandNameConverters = CommandNameConverters.DEFAULT

    @PublishedApi
    internal val commandFactories: MutableList<CommandFactory<*>> = mutableListOf()

    private fun buildCommands(): Commands = Commands(
        defaultCommandPermissions = defaultCommandPermissions,
        nameConverters = nameConverters,
    )

    internal fun build(): Pair<Commands, List<CommandFactory<*>>> = buildCommands() to commandFactories.toList()
}

@KommandoDsl
public inline fun KommandoBuilder.commands(builder: CommandsBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    commandsBuilder = CommandsBuilder().apply(builder)
}