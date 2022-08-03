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

package net.ormr.kommando.commands.factory

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import net.ormr.kommando.KommandoBuilder
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.commands.CommandGroup
import net.ormr.kommando.commands.SubCommand
import net.ormr.kommando.commands.SuperCommand
import net.ormr.kommando.commands.TopLevelCommand
import net.ormr.kommando.commands.permissions.CommandPermissions
import org.kodein.di.DirectDI
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KommandoDsl
public class CommandFactoryBuilder<
        Cmd : SuperCommand<I, Perms>,
        I : ChatInputCommandInteraction,
        Perms : CommandPermissions,
        >(private val factory: DirectDI.() -> Cmd) {
    private val children = mutableListOf<CommandChildFactory<*>>()

    @KommandoDsl
    public fun subCommands(
        factory: DirectDI.() -> SubCommand<I, Cmd>,
        vararg moreFactories: DirectDI.() -> SubCommand<I, Cmd>,
    ) {
        val factories = buildList(moreFactories.size + 1) {
            add(SubCommandFactory(factory))
            addAll(moreFactories.map(::SubCommandFactory))
        }

        children.addAll(factories)
    }

    @KommandoDsl
    public fun group(
        group: DirectDI.() -> CommandGroup<Cmd>,
        factory: DirectDI.() -> SubCommand<I, Cmd>,
        vararg moreFactories: DirectDI.() -> SubCommand<I, Cmd>,
    ) {
        val factories = buildList(moreFactories.size + 1) {
            add(factory)
            addAll(moreFactories)
        }

        children += CommandGroupFactory(group, factories)
    }

    @PublishedApi
    internal fun createFactory(): CommandFactory = when {
        children.isEmpty() -> SingleCommandFactory(factory)
        else -> ParentCommandFactory(factory, children)
    }
}

context(KommandoBuilder)
        @KommandoDsl
        public inline fun <
        Cmd : SuperCommand<I, Perms>,
        I : ChatInputCommandInteraction,
        Perms : CommandPermissions,
        > commandFactory(
    noinline factory: DirectDI.() -> Cmd,
    builder: CommandFactoryBuilder<Cmd, I, Perms>.() -> Unit,
): CommandFactory {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return CommandFactoryBuilder(factory).apply(builder).createFactory()
}

context(KommandoBuilder)
        @KommandoDsl
        public fun commandFactory(factory: DirectDI.() -> TopLevelCommand<*, *>): CommandFactory =
    SingleCommandFactory(factory)