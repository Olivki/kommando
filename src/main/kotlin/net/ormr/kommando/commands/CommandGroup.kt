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

import net.ormr.kommando.*
import net.ormr.kommando.localization.LocalizedString

// TODO: in the annotation processor we need to check both if any members extending CommandGroup have nested subcommand
//       instances, and check if any explicitly marked subcommands are nested within a member extending CommandGroup
public abstract class CommandGroup<out Super : SuperCommand<*, *>>(
    public val defaultName: String,
) : KommandoComponent(), DescribableCommand {
    public override val description: LocalizedString by lazy { localization.resolve("description") }

    public open val name: LocalizedString by lazy {
        localization.resolveOrNull("name") ?: LocalizedString(defaultName)
    }

    public lateinit var parent: @UnsafeVariance Super
        internal set

    final override val componentPath: KommandoComponentPath by lazy {
        parent.componentPath.extend("groups", defaultName)
    }
}

// to circumvent 'setter for property is removed by type projection' error while still keeping the type information
// for the end user on 'parent'
@PublishedApi
internal fun CommandGroup<*>.setParent(parent: SuperCommand<*, *>) {
    this::parent.set(parent)
}