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

import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.rest.builder.interaction.boolean
import net.ormr.kommando.command.CustomizableCommand
import net.ormr.kommando.localization.*

public class BooleanArgument(
    override val name: Message,
    override val description: Message,
) : Argument<Boolean, Boolean, ArgumentType.Boolean> {
    override val type: ArgumentType.Boolean
        get() = ArgumentType.Boolean

    override fun convertArgumentValue(value: Boolean): Boolean = value

    override fun convertNullableArgumentValue(value: Boolean?): Boolean? = value

    context(BaseInputChatBuilder)
    override fun buildArgument(resolver: MessageResolver, isRequired: Boolean) {
        boolean(resolver[name], resolver[description]) {
            registerLocalizations()
            this.required = isRequired
        }
    }
}

context(Cmd)
public fun <Cmd> boolean(
    name: Message? = null,
    description: String,
): ArgumentBuilder<Cmd, Boolean, BooleanArgument>
        where Cmd : CustomizableCommand<*> =
    ArgumentHelper.newBuilder(name, BasicMessage(description)) { resolvedName, desc ->
        BooleanArgument(resolvedName, desc)
    }

context(Cmd)
public fun <Cmd> boolean(
    name: Message? = null,
    description: LocalizedMessage? = null,
): ArgumentBuilder<Cmd, Boolean, BooleanArgument>
        where Cmd : CustomizableCommand<*> =
    ArgumentHelper.newBuilder(name, description) { resolvedName, desc ->
        BooleanArgument(resolvedName, desc)
    }