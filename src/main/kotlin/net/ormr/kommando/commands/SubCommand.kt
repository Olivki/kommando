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
import net.ormr.kommando.KommandoComponentPath
import net.ormr.kommando.extend
import net.ormr.kommando.localization.LocalizedString
import net.ormr.kommando.resolve

public sealed class SubCommand<out I : ChatInputCommandInteraction, out Super : SuperCommand<*, *>>(
    name: String,
) : Command<I>(name), CustomizableCommand, DescribableCommand, ChatInputCommand {
    public override val description: LocalizedString by lazy { localization.resolve("description") }

    // TODO: use
    protected open val descriptionArguments: Map<String, String>? = null

    // the setter is not available to the user, so @UnsafeVariance is *relatively* safe here
    public lateinit var parent: @UnsafeVariance Super
        internal set

    // TODO: this needs to take the group into account
    final override val componentPath: KommandoComponentPath by lazy {
        parent.componentPath.extend("subCommands", defaultName)
    }
}

// to circumvent 'setter for property is removed by type projection' error while still keeping the type information
// for the end user on 'parent'
@PublishedApi
internal fun SubCommand<*, *>.setParent(parent: SuperCommand<*, *>) {
    this::parent.set(parent)
}