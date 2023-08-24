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

import net.ormr.kommando.AbstractComponent
import net.ormr.kommando.getMessageOrNull
import net.ormr.kommando.localization
import net.ormr.kommando.localization.BasicMessage
import net.ormr.kommando.localization.Message

// TODO: implement nsfw flag

public sealed interface Command<Context> : CommandComponent
        where Context : CommandContext<*> {
    public val commandName: Message

    context(Context)
    public suspend fun execute()
}

public sealed class AbstractCommand<Context>(
    private val defaultName: String,
) : AbstractComponent(), Command<Context>
        where Context : CommandContext<*> {
    override val commandName: Message
        get() = localization.getMessageOrNull("name") ?: BasicMessage(this.defaultName)

    internal val registry: CommandArgumentRegistry by lazy {
        CommandArgumentRegistry(this)
    }
}