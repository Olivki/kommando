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

package net.ormr.kommando.command.argument

import dev.kord.core.entity.Entity
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.rest.builder.interaction.mentionable
import net.ormr.kommando.command.CustomizableCommand
import net.ormr.kommando.localization.*

public class MentionableArgument(
    override val key: String,
    override val name: Message,
    override val description: Message,
) : Argument<Entity, Entity, ArgumentType.Mentionable> {
    override val type: ArgumentType.Mentionable
        get() = ArgumentType.Mentionable

    override fun convertArgumentValue(value: Entity): Entity = value

    override fun convertNullableArgumentValue(value: Entity?): Entity? = value

    context(ArgumentBuildContext, BaseInputChatBuilder)
    override fun buildArgument(resolver: MessageResolver, isRequired: Boolean) {
        mentionable(resolver[name], resolver[description]) {
            registerLocalizations()
            this.required = isRequired
        }
    }

    override fun toString(): String =
        "MentionableArgument(key='$key', name='${name.defaultString}', description='${description.defaultString}')"
}

context(Cmd)
public fun <Cmd> mentionable(
    name: Message? = null,
    description: String,
): ArgumentBuilder<Cmd, Entity, MentionableArgument>
        where Cmd : CustomizableCommand<*> =
    ArgumentHelper.newBuilder(name, BasicMessage(description)) { key, resolvedName, desc ->
        MentionableArgument(key, resolvedName, desc)
    }

context(Cmd)
public fun <Cmd> mentionable(
    name: Message? = null,
    description: LocalizedMessage? = null,
): ArgumentBuilder<Cmd, Entity, MentionableArgument>
        where Cmd : CustomizableCommand<*> =
    ArgumentHelper.newBuilder(name, description) { key, resolvedName, desc ->
        MentionableArgument(key, resolvedName, desc)
    }