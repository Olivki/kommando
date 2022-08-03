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

package net.ormr.kommando

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import net.ormr.kommando.commands.factory.CommandFactory
import net.ormr.kommando.commands.permissions.DefaultCommandPermissions
import net.ormr.kommando.internal.handleCommands
import net.ormr.kommando.internal.registerCommands
import net.ormr.kommando.localization.Localization

public class Kommando internal constructor(
    public val kord: Kord,
    public val localization: Localization,
    public val defaultCommandPermissions: DefaultCommandPermissions?,
) : KommandoDI {
    public lateinit var registeredCommands: Map<Snowflake, CommandFactory>
        private set

    @PublishedApi
    internal suspend fun setup(commands: List<CommandFactory>) {
        registerComponents(commands)
        registerHandlers()
    }

    private suspend fun registerComponents(commands: List<CommandFactory>) {
        // TODO: actually quit the application if there's problems with registering the commands,
        //       as Kord is probably gonna force the application to be alive even though it will throw exceptions
        registeredCommands = registerCommands(commands)
    }

    private suspend fun registerHandlers() {
        handleCommands()
    }
}