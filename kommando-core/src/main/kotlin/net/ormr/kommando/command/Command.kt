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

import net.ormr.kommando.AbstractKommandoComponent
import net.ormr.kommando.KommandoComponent
import net.ormr.kommando.getMessageOrNull
import net.ormr.kommando.localeBundle
import net.ormr.kommando.localization.BasicMessage
import net.ormr.kommando.localization.Message

// TODO: implement nsfw flag

public sealed interface Command<Context> : KommandoComponent
        where Context : CommandContext<*> {
    public val defaultCommandName: String
    public val commandName: Message

    context(Context)
    public suspend fun execute()
}

public sealed class AbstractCommand<Context>(
    override val defaultCommandName: String,
) : AbstractKommandoComponent(), Command<Context>
        where Context : CommandContext<*> {
    internal val registry: CommandArgumentRegistry by lazy {
        CommandArgumentRegistry(this)
    }

    override val commandName: Message
        get() = localeBundle.getMessageOrNull("name") ?: BasicMessage(defaultCommandName)
}