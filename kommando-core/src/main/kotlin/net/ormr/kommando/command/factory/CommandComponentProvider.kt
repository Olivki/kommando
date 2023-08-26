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

import net.ormr.kommando.command.CommandElement
import net.ormr.kommando.command.CommandGroup
import net.ormr.kommando.command.SubCommand
import net.ormr.kommando.command.TopLevelCommand
import org.kodein.di.DirectDI

internal typealias TopLevelCommandProvider<Context, Perms> = CommandComponentProvider<TopLevelCommand<Context, Perms>>
internal typealias SubCommandProvider<Context, Parent> = CommandComponentProvider<SubCommand<Context, Parent>>
internal typealias CommandGroupProvider<Parent> = CommandComponentProvider<CommandGroup<Parent>>

public fun interface CommandComponentProvider<out Comp>
        where Comp : CommandElement {
    context(DirectDI)
    public fun get(): Comp
}