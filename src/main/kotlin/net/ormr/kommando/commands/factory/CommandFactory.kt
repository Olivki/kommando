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

import net.ormr.kommando.commands.SuperCommand
import net.ormr.kommando.commands.TopLevelCommand
import org.kodein.di.DirectDI

public sealed interface CommandFactory {
    public val factory: DirectDI.() -> TopLevelCommand<*, *>

    public operator fun invoke(di: DirectDI): TopLevelCommand<*, *>
}

public class SingleCommandFactory internal constructor(override val factory: DirectDI.() -> TopLevelCommand<*, *>) :
    CommandFactory {
    override fun invoke(di: DirectDI): TopLevelCommand<*, *> = factory(di)
}

public class ParentCommandFactory internal constructor(
    override val factory: DirectDI.() -> SuperCommand<*, *>,
    public val children: List<CommandChildFactory<*>>,
) : CommandFactory {
    override fun invoke(di: DirectDI): SuperCommand<*, *> = factory(di)
}