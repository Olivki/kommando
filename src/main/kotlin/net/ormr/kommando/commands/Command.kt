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

import dev.kord.core.entity.interaction.ApplicationCommandInteraction
import net.ormr.kommando.KommandoComponent
import net.ormr.kommando.commands.arguments.Argument
import net.ormr.kommando.localization.LocalizedString
import net.ormr.kommando.resolveOrNull
import kotlin.reflect.KProperty

// TODO: provide 'init' function that the user can use safely without triggering some pitfalls?
public sealed class Command<out I : ApplicationCommandInteraction>(public val defaultName: String) :
    KommandoComponent() {
    public open val name: LocalizedString by lazy {
        localization.resolveOrNull("name") ?: LocalizedString(defaultName)
    }

    init {
        CommandArgumentCache.entry(javaClass)
    }

    internal var resolvedArguments: Map<String, Any?>? = null

    public val arguments: Map<String, Argument<*, *>>
        get() = CommandArgumentCache.getArguments(javaClass)

    public open suspend fun run(interaction: @UnsafeVariance I) {}

    // TODO: '$property' or '${property.name}'
    // TODO: 'run' or ${this::run)
    internal fun getResolvedArgument(
        property: KProperty<*>,
        name: String,
    ): Any? = when (val arguments = resolvedArguments) {
        null -> error(
            """
                No resolvedArguments have been provided to $this yet.
                Property $property was most likely accessed outside of ${this::run}.
            """.trimIndent()
        )
        else -> when (name) {
            in arguments -> arguments[name]
            else -> throw NoSuchElementException("Could not find argument '$name' in resolvedArguments")
        }
    }
}