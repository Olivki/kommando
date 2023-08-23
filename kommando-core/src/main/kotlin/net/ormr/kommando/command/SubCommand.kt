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

import net.ormr.kommando.ComponentPath
import net.ormr.kommando.getMessageOrNull
import net.ormr.kommando.localeBundle
import net.ormr.kommando.localization.BasicMessage
import net.ormr.kommando.localization.Message

public sealed interface SubCommand<Context, out Super> : Command<Context>, CustomizableCommand<Context>,
    DescribableCommandComponent, ChatInputCommand<Context>
        where Context : CommandContext<*>,
              Super : InheritableCommandComponent {
    public val superComponent: @UnsafeVariance Super
}

public sealed class AbstractSubCommand<Context, out Super>(
    private val defaultName: String,
    private val defaultDescription: String,
) : AbstractCommand<Context>(defaultName), SubCommand<Context, Super>
        where Context : CommandContext<*>,
              Super : InheritableCommandComponent {
    override val componentDescription: Message
        get() = localeBundle.getMessageOrNull("description") ?: BasicMessage(defaultDescription)

    final override lateinit var superComponent: @UnsafeVariance Super
        private set

    final override val componentPath: ComponentPath
        get() = ComponentPath("subCommands", defaultName)

    // Workaround for 'Setter for property is removed by type projection' error
    internal fun initSuperComponent(parent: @UnsafeVariance Super) {
        superComponent = parent
    }
}