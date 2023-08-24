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
import net.ormr.kommando.ComponentPath
import net.ormr.kommando.getMessageOrNull
import net.ormr.kommando.localization
import net.ormr.kommando.localization.BasicMessage
import net.ormr.kommando.localization.Message

public abstract class CommandGroup<out Parent>(
    private val name: String,
    private val description: String,
) : AbstractComponent(), DescribableCommandComponent, GlobalInheritableCommandComponent,
    GuildInheritableCommandComponent, ChildCommandComponent
        where Parent : RootCommand<*, *> {
    public lateinit var parentCommand: @UnsafeVariance Parent
        private set

    public val groupName: Message
        get() = localization.getMessageOrNull("name") ?: BasicMessage(name)

    override val componentDescription: Message
        get() = localization.getMessageOrNull("description") ?: BasicMessage(description)

    final override val componentPath: ComponentPath
        get() = ComponentPath("groups", name)

    final override val fullComponentPath: ComponentPath
        get() = parentCommand.componentPath / componentPath

    // Workaround for 'Setter for property is removed by type projection' error
    internal fun initParentCommand(parent: @UnsafeVariance Parent) {
        parentCommand = parent
    }
}
