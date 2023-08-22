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

import dev.kord.core.entity.Attachment
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.rest.builder.interaction.attachment
import net.ormr.kommando.command.CustomizableCommand
import net.ormr.kommando.localization.*

public class AttachmentArgument(
    override val name: Message,
    override val description: Message,
) : Argument<Attachment, Attachment, ArgumentType.Attachment> {
    override val type: ArgumentType.Attachment
        get() = ArgumentType.Attachment

    override fun convertArgumentValue(value: Attachment): Attachment = value

    override fun convertNullableArgumentValue(value: Attachment?): Attachment? = value

    context(BaseInputChatBuilder)
    override fun buildArgument(resolver: MessageResolver, isRequired: Boolean) {
        attachment(resolver[name], resolver[description]) {
            registerLocalizations()
            this.required = isRequired
        }
    }

    override fun toString(): String = "AttachmentArgument(name=$name, description=$description)"
}

context(Cmd)
public fun <Cmd> attachment(
    name: Message? = null,
    description: String,
): ArgumentBuilder<Cmd, Attachment, AttachmentArgument>
        where Cmd : CustomizableCommand<*> =
    ArgumentHelper.newBuilder(name, BasicMessage(description)) { resolvedName, desc ->
        AttachmentArgument(resolvedName, desc)
    }

context(Cmd)
public fun <Cmd> attachment(
    name: Message? = null,
    description: LocalizedMessage? = null,
): ArgumentBuilder<Cmd, Attachment, AttachmentArgument>
        where Cmd : CustomizableCommand<*> =
    ArgumentHelper.newBuilder(name, description) { resolvedName, desc ->
        AttachmentArgument(resolvedName, desc)
    }