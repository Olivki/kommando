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

import dev.kord.core.entity.channel.ResolvedChannel
import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import dev.kord.rest.builder.interaction.channel
import net.ormr.kommando.command.CustomizableCommand
import net.ormr.kommando.localization.Message

public class ChannelArgument(
    override val key: String,
    override val name: Message,
    override val description: Message,
) : Argument<ResolvedChannel, ResolvedChannel, ArgumentType.Channel> {
    override val type: ArgumentType.Channel
        get() = ArgumentType.Channel

    override fun convertArgumentValue(value: ResolvedChannel): ResolvedChannel = value

    override fun convertNullableArgumentValue(value: ResolvedChannel?): ResolvedChannel? = value

    context(ArgumentBuildContext, BaseInputChatBuilder)
    override fun buildArgument(isRequired: Boolean) {
        channel(defaultName, defaultDescription) {
            registerLocalizations()
            this.required = isRequired
        }
    }

    override fun toString(): String =
        "ChannelArgument(key='$key', name='${name.defaultString}', description='${description.defaultString}')"
}

context(Cmd)
public fun <Cmd> channel(
    name: String? = null,
    description: String,
): ArgumentBuilder<Cmd, ResolvedChannel, ChannelArgument>
        where Cmd : CustomizableCommand<*> =
    ArgumentHelper.newBuilder(name, description) { key, resolvedName, desc ->
        ChannelArgument(key, resolvedName, desc)
    }