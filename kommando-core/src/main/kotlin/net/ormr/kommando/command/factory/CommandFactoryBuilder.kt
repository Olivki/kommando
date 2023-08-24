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

package net.ormr.kommando.command.factory

import kotlinx.collections.immutable.toPersistentList
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.command.*
import net.ormr.kommando.command.permission.CommandPermissions
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@KommandoDsl
public class CommandFactoryBuilder<Cmd, Context, Perms>(
    private val provider: CommandComponentProvider<Cmd>,
    private val type: KType,
) where Cmd : RootCommand<Context, Perms>,
        Context : CommandContext<*>,
        Perms : CommandPermissions {
    private val children = mutableListOf<ChildCommandFactory<*>>()

    @KommandoDsl
    public fun subCommand(provider: SubCommandProvider<Context, Cmd>) {
        addChild(SubCommandFactory(provider))
    }

    @KommandoDsl
    public inline fun <Group> group(
        provider: CommandComponentProvider<Group>,
        builder: CommandGroupFactoryBuilder<Group, Cmd, Context, Perms>.() -> Unit,
    ) where Group : CommandGroup<Cmd> {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        addChild(CommandGroupFactoryBuilder<Group, Cmd, Context, Perms>(provider).apply(builder).build())
    }

    @PublishedApi
    internal fun addChild(child: ChildCommandFactory<*>) {
        children += child
    }

    @PublishedApi
    internal fun build(): CommandFactory<*> = when {
        children.isEmpty() -> SingleCommandFactory(provider, type)
        else -> RootCommandFactory(provider, type, children.toPersistentList())
    }
}

@KommandoDsl
public inline fun <reified Cmd, Context, Perms> CommandsBuilder.command(
    provider: CommandComponentProvider<Cmd>,
    builder: CommandFactoryBuilder<Cmd, Context, Perms>.() -> Unit,
) where Cmd : RootCommand<Context, Perms>,
        Context : CommandContext<*>,
        Perms : CommandPermissions {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    commandFactories += CommandFactoryBuilder(provider, typeOf<Cmd>()).apply(builder).build()
}

@KommandoDsl
public inline fun <reified Cmd> CommandsBuilder.command(provider: CommandComponentProvider<Cmd>)
        where Cmd : TopLevelCommand<*, *> {
    commandFactories += SingleCommandFactory(provider, typeOf<Cmd>())
}