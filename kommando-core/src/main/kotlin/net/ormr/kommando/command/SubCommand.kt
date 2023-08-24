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

import net.ormr.kommando.*
import net.ormr.kommando.localization.BasicMessage
import net.ormr.kommando.localization.Message

public sealed interface SubCommand<Context, out Parent> : Command<Context>, CustomizableCommand<Context>,
    DescribableCommandComponent, ChatInputCommand<Context>, ComposableComponent
        where Context : CommandContext<*>,
              Parent : InheritableCommandComponent {
    public val parentComponent: @UnsafeVariance Parent
}

public sealed class AbstractSubCommand<Context, out Parent>(
    private val defaultName: String,
    private val defaultDescription: String,
) : AbstractCommand<Context>(defaultName), SubCommand<Context, Parent>
        where Context : CommandContext<*>,
              Parent : InheritableCommandComponent {
    override val componentDescription: Message
        get() = localeBundle.getMessageOrNull("description") ?: BasicMessage(defaultDescription)

    final override lateinit var parentComponent: @UnsafeVariance Parent
        private set

    final override val componentPath: ComponentPath
        get() = ComponentPath("subCommands", defaultName)

    final override val fullComponentPath: ComponentPath
        get() = parentComponent.findFullComponentPath() / componentPath

    // Workaround for 'Setter for property is removed by type projection' error
    internal fun initParentComponent(parent: @UnsafeVariance Parent) {
        parentComponent = parent
    }
}