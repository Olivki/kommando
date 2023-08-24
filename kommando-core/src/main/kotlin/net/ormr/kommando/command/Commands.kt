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

package net.ormr.kommando.command

import com.github.michaelbull.logging.InlineLogger
import dev.kord.common.entity.Snowflake
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.ormr.kommando.Kommando
import net.ormr.kommando.command.argument.ArgumentCache
import net.ormr.kommando.command.factory.CommandFactory
import net.ormr.kommando.command.permission.DefaultCommandPermissions
import net.ormr.kommando.internal.registerCommands as registerCommands0

public class Commands internal constructor(
    public val defaultCommandPermissions: DefaultCommandPermissions?,
    public val nameConverters: CommandNameConverters,
) {
    public lateinit var registeredCommands: Map<Snowflake, RegisteredCommand>
        private set
    internal val argumentCache: ArgumentCache = ArgumentCache()
    private val mutex = Mutex()

    context(Kommando)
    internal suspend fun registerCommands(factories: List<CommandFactory<*>>) = mutex.withLock {
        logger.info { "Registering commands..." }
        // TODO: do we need to quit the application if we encounter problems with registering the commands?
        //       Kord might try and keep the application alive even if exceptions were thrown
        registeredCommands = registerCommands0(factories)
    }

    private companion object {
        private val logger = InlineLogger()
    }
}