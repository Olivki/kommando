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

import net.ormr.kommando.command.ChildCommandComponent
import net.ormr.kommando.command.CommandGroup
import net.ormr.kommando.command.SubCommand
import org.kodein.di.DirectDI

public sealed interface ChildCommandFactory<Comp>
        where Comp : ChildCommandComponent {
    public val provider: CommandComponentProvider<Comp>
}

public fun <Comp> ChildCommandFactory<Comp>.create(di: DirectDI): Comp
        where Comp : ChildCommandComponent = with(di) { provider.get() }

public class SubCommandFactory internal constructor(override val provider: SubCommandProvider<*, *>) :
    ChildCommandFactory<SubCommand<*, *>>

public class CommandGroupFactory internal constructor(
    override val provider: CommandGroupProvider<*>,
    public val providers: List<SubCommandProvider<*, *>>,
) : ChildCommandFactory<CommandGroup<*>>

