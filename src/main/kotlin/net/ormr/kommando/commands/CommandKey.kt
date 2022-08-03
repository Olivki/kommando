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

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.application.ApplicationCommand as KordApplicationCommand
import dev.kord.core.entity.application.GuildApplicationCommand as KordGuildApplicationCommand

public sealed class CommandKey {
    public abstract override fun equals(other: Any?): Boolean

    public abstract override fun hashCode(): Int

    public abstract fun format(): String

    public data class Global(public val name: String) : CommandKey() {
        override fun format(): String = name
    }

    public data class Guild(public val name: String, public val guildId: Snowflake) : CommandKey() {
        override fun format(): String = "$name @$guildId"
    }
}

public fun CommandKey(command: TopLevelCommand<*, *>): CommandKey = when (command) {
    is GlobalTopLevelCommand -> CommandKey.Global(command.defaultName)
    is GuildTopLevelCommand -> CommandKey.Guild(command.defaultName, command.guildId)
}

public fun CommandKey(command: KordApplicationCommand): CommandKey = when (command) {
    is KordGuildApplicationCommand -> CommandKey.Guild(command.name, command.guildId)
    else -> CommandKey.Global(command.name)
}