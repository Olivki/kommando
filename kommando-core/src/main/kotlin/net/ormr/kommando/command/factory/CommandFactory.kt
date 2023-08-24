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

import kotlinx.collections.immutable.PersistentList
import net.ormr.kommando.command.RootCommand
import net.ormr.kommando.command.TopLevelCommand
import org.kodein.di.DirectDI

public sealed interface CommandFactory<Cmd>
        where Cmd : TopLevelCommand<*, *> {
    public val provider: CommandComponentProvider<Cmd>
}

public fun <Cmd> CommandFactory<Cmd>.create(di: DirectDI): Cmd
        where Cmd : TopLevelCommand<*, *> = with(di) { provider.get() }

public class SingleCommandFactory internal constructor(
    override val provider: CommandComponentProvider<TopLevelCommand<*, *>>,
) : CommandFactory<TopLevelCommand<*, *>>

public class RootCommandFactory internal constructor(
    override val provider: CommandComponentProvider<RootCommand<*, *>>,
    public val children: PersistentList<ChildCommandFactory<*>>,
) : CommandFactory<RootCommand<*, *>>