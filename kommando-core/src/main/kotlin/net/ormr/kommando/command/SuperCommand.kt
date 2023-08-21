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

import net.ormr.kommando.command.permission.CommandPermissions
import net.ormr.kommando.getMessage
import net.ormr.kommando.localeBundle
import net.ormr.kommando.localization.Message

public sealed interface SuperCommand<Context, Perms> : RootCommand<Context, Perms>, CustomizableCommand<Context>,
    DescribableCommandComponent, ChatInputCommand<Context>
        where Context : CommandContext<*>,
              Perms : CommandPermissions {
    override val componentDescription: Message
        get() = localeBundle.getMessage("description")
}

public sealed class AbstractSuperCommand<Context, Perms>(
    defaultName: String,
) : AbstractRootCommand<Context, Perms>(defaultName), SuperCommand<Context, Perms>
        where Context : CommandContext<*>,
              Perms : CommandPermissions