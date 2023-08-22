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

package net.ormr.kommando.command.factory

import kotlinx.collections.immutable.toPersistentList
import net.ormr.kommando.KommandoBuilder
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.command.*
import net.ormr.kommando.command.permission.CommandPermissions
import org.kodein.di.DirectDI
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KommandoDsl
public class CommandFactoryBuilder<Cmd, Context, Perms>(private val factory: DirectDI.() -> Cmd)
        where Cmd : SuperCommand<Context, Perms>,
              Context : CommandContext<*>,
              Perms : CommandPermissions {
    private val children = mutableListOf<CommandChildFactory<*>>()

    @KommandoDsl
    public fun subCommands(
        first: DirectDI.() -> SubCommand<Context, Cmd>,
        vararg rest: DirectDI.() -> SubCommand<Context, Cmd>,
    ) {
        children.add(SubCommandFactory(first))
        for (factory in rest) {
            children.add(SubCommandFactory(factory))
        }
    }

    @KommandoDsl
    public fun group(
        group: DirectDI.() -> CommandGroup<Cmd>,
        first: DirectDI.() -> SubCommand<Context, Cmd>,
        vararg rest: DirectDI.() -> SubCommand<Context, Cmd>,
    ) {
        val factories = buildList(rest.size + 1) {
            add(first)
            addAll(rest)
        }

        children += CommandGroupFactory(group, factories)
    }

    @PublishedApi
    internal fun createFactory(): CommandFactory = when {
        children.isEmpty() -> SingleCommandFactory(factory)
        else -> ParentCommandFactory(factory, children.toPersistentList())
    }
}

context(KommandoBuilder)
@KommandoDsl
public inline fun <Cmd, Context, Perms> CommandFactory(
    noinline factory: DirectDI.() -> Cmd,
    builder: CommandFactoryBuilder<Cmd, Context, Perms>.() -> Unit,
): CommandFactory
        where Cmd : SuperCommand<Context, Perms>,
              Context : CommandContext<*>,
              Perms : CommandPermissions {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return CommandFactoryBuilder(factory).apply(builder).createFactory()
}

context(KommandoBuilder)
@KommandoDsl
public fun CommandFactory(factory: DirectDI.() -> RootCommand<*, *>): CommandFactory =
    SingleCommandFactory(factory)

context(KommandoBuilder)
public inline operator fun CommandFactory.unaryPlus() {
    commandFactories += this
}