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

import dev.kord.core.Kord
import net.ormr.kommando.command.Commands
import net.ormr.kommando.command.factory.CommandFactory
import net.ormr.kommando.component.ComponentStorage
import net.ormr.kommando.internal.handleCommands
import net.ormr.kommando.internal.handleModals
import net.ormr.kommando.localization.Localization
import net.ormr.kommando.modal.ModalStorage

public class Kommando internal constructor(
    public val kord: Kord,
    public val localization: Localization,
    public val commands: Commands,
    public val exceptionHandler: KommandoExceptionHandler?,
    public val modalStorage: ModalStorage,
    public val componentStorage: ComponentStorage,
) : KommandoDI {
    @PublishedApi
    internal suspend fun setup(factories: List<CommandFactory<*>>) {
        registerComponents(factories)
        registerHandlers()
    }

    private suspend fun registerComponents(factories: List<CommandFactory<*>>) {
        commands.registerCommands(factories)
    }

    private suspend fun registerHandlers() {
        handleCommands()
        handleModals()
    }
}