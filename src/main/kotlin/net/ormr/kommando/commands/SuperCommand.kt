/*
 * Copyright 2022 Oliver Berg
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

package net.ormr.kommando.commands

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import net.ormr.kommando.commands.permissions.CommandPermissions
import net.ormr.kommando.localization.LocalizedString
import net.ormr.kommando.resolve

public sealed class SuperCommand<
        Self : SuperCommand<Self, I, Sub, Perms>,
        out I : ChatInputCommandInteraction,
        Sub : SubCommand<Sub, I, Self>,
        Perms : CommandPermissions,
        >(
    name: String,
) : TopLevelCommand<I, Perms>(name), CustomizableCommand, DescribableCommand {
    public override val description: LocalizedString by lazy { localization.resolve("description") }
}