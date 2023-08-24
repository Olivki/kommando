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

package net.ormr.kommando

import com.github.michaelbull.logging.InlineLogger
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import net.ormr.kommando.command.CommandMessageConverters
import net.ormr.kommando.command.RegisteredCommand
import net.ormr.kommando.command.argument.ArgumentCache
import net.ormr.kommando.command.factory.CommandFactory
import net.ormr.kommando.command.permission.DefaultCommandPermissions
import net.ormr.kommando.internal.handleCommands
import net.ormr.kommando.internal.registerCommands
import net.ormr.kommando.localization.LocaleBundle

public class Kommando internal constructor(
    public val kord: Kord,
    public val localeBundle: LocaleBundle,
    public val defaultCommandPermissions: DefaultCommandPermissions?,
    public val exceptionHandler: KommandoExceptionHandler?,
    public val commandMessageConverters: CommandMessageConverters,
) : KommandoDI {
    public lateinit var registeredCommands: Map<Snowflake, RegisteredCommand>
        private set

    internal val argumentCache: ArgumentCache = ArgumentCache()

    @PublishedApi
    internal suspend fun setup(factories: List<CommandFactory<*>>) {
        registerComponents(factories)
        registerHandlers()
    }

    private suspend fun registerComponents(commands: List<CommandFactory<*>>) {
        // TODO: actually quit the application if there's problems with registering the commands,
        //       as Kord is probably gonna force the application to be alive even though it will throw exceptions
        logger.info { "Registering commands..." }
        registeredCommands = registerCommands(commands)
    }

    private suspend fun registerHandlers() {
        handleCommands()
    }

    private companion object {
        private val logger = InlineLogger()
    }
}