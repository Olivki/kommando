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
import net.ormr.kommando.localization.DefaultMessage
import net.ormr.kommando.localization.Message

public sealed interface CommandGroup<out Super> : KommandoComponent, DescribableCommandComponent
        where Super : SuperCommand<*, *> {
    public val defaultGroupName: String
    public val superCommand: @UnsafeVariance Super
    public val groupName: Message
        get() = localeBundle.getMessageOrNull("name") ?: DefaultMessage(defaultGroupName)

    override val componentDescription: Message
        get() = localeBundle.getMessage("description")
}

public sealed class AbstractCommandGroup<out Super>(override val defaultGroupName: String) : CommandGroup<Super>
        where Super : SuperCommand<*, *> {
    final override lateinit var superCommand: @UnsafeVariance Super
        internal set

    final override val componentPath: KommandoComponentPath
        get() = superCommand.componentPath / "groups" / defaultGroupName
}