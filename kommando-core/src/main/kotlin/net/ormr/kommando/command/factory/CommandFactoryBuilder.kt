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
        where Cmd : RootCommand<Context, Perms>,
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

    // TODO: separate builder for groups
    @KommandoDsl
    public fun <Group> group(
        group: DirectDI.() -> Group,
        firstCommand: DirectDI.() -> SubCommand<Context, Group>,
        vararg restCommands: DirectDI.() -> SubCommand<Context, Group>,
    ) where Group : CommandGroup<Cmd> {
        val factories = buildList(restCommands.size + 1) {
            add(firstCommand)
            addAll(restCommands)
        }

        children += CommandGroupFactory(group, factories)
    }

    @PublishedApi
    internal fun createFactory(): CommandFactory = when {
        children.isEmpty() -> SingleCommandFactory(factory)
        else -> RootCommandFactory(factory, children.toPersistentList())
    }
}

context(KommandoBuilder)
@KommandoDsl
public inline fun <Cmd, Context, Perms> commandFactory(
    noinline factory: DirectDI.() -> Cmd,
    builder: CommandFactoryBuilder<Cmd, Context, Perms>.() -> Unit,
) where Cmd : RootCommand<Context, Perms>,
        Context : CommandContext<*>,
        Perms : CommandPermissions {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    commandFactories += CommandFactoryBuilder(factory).apply(builder).createFactory()
}

context(KommandoBuilder)
@KommandoDsl
public fun commandFactory(factory: DirectDI.() -> TopLevelCommand<*, *>) {
    commandFactories += SingleCommandFactory(factory)
}