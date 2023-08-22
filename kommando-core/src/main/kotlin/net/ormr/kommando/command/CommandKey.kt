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

package net.ormr.kommando.command

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.application.ApplicationCommand
import dev.kord.core.entity.application.GuildApplicationCommand

public sealed interface CommandKey {
    public fun asString(): String

    public data class Global(val name: String) : CommandKey {
        override fun asString(): String = name
    }

    public data class Guild(val name: String, val guildId: Snowflake) : CommandKey {
        override fun asString(): String = "$name @$guildId"
    }
}

public fun RootCommand<*, *>.toCommandKey(): CommandKey = when (this) {
    is GuildRootCommand -> CommandKey.Guild(defaultCommandName, commandGuildId)
    is GlobalRootCommand -> CommandKey.Global(defaultCommandName)
}

internal fun RootCommand<*, *>.formatAsCommandKey(): String = when (this) {
    is GuildRootCommand -> "$defaultCommandName @$commandGuildId"
    is GlobalRootCommand -> defaultCommandName
}

public fun ApplicationCommand.toCommandKey(): CommandKey = when (this) {
    is GuildApplicationCommand -> CommandKey.Guild(name, guildId)
    else -> CommandKey.Global(name)
}