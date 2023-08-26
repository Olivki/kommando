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

import dev.kord.core.entity.Role
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.rest.builder.interaction.role
import net.ormr.kommando.command.CustomizableCommand
import net.ormr.kommando.localization.Message

public class RoleArgument(
    override val key: String,
    override val name: Message,
    override val description: Message,
) : Argument<Role, Role, ArgumentType.Role> {
    override val type: ArgumentType.Role
        get() = ArgumentType.Role

    override fun convertArgumentValue(value: Role): Role = value

    override fun convertNullableArgumentValue(value: Role?): Role? = value

    context(ArgumentBuildContext, BaseInputChatBuilder)
    override fun buildArgument(isRequired: Boolean) {
        role(defaultName, defaultDescription) {
            registerLocalizations()
            this.required = isRequired
        }
    }

    override fun toString(): String =
        "RoleArgument(key='$key', name='${name.defaultString}', description='${description.defaultString}')"
}

context(Cmd)
public fun <Cmd> role(
    name: String? = null,
    description: String,
): ArgumentBuilder<Cmd, Role, RoleArgument>
        where Cmd : CustomizableCommand<*> =
    ArgumentHelper.newBuilder(name, description) { key, resolvedName, desc ->
        RoleArgument(key, resolvedName, desc)
    }