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
import net.ormr.kommando.command.CommandContext
import net.ormr.kommando.command.CommandGroup
import net.ormr.kommando.command.RootCommand
import net.ormr.kommando.command.permission.CommandPermissions

@KommandoDsl
public class CommandGroupFactoryBuilder<Group, Parent, Context, Perms> @PublishedApi internal constructor(
    private val provider: CommandGroupProvider<Parent>,
) where Group : CommandGroup<Parent>,
        Parent : RootCommand<Context, Perms>,
        Context : CommandContext<*>,
        Perms : CommandPermissions {
    private val providers = mutableListOf<SubCommandProvider<Context, Group>>()

    @KommandoDsl
    public fun subCommand(provider: SubCommandProvider<Context, Group>) {
        providers += provider
    }

    @PublishedApi
    internal fun build(): CommandGroupFactory = CommandGroupFactory(provider, providers.toPersistentList())
}