/*
 * MIT License
 *
 * Copyright (c) 2022 Oliver Berg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.ormr.kommando.commands

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import dev.kord.core.entity.interaction.GuildUserCommandInteraction
import dev.kord.core.event.interaction.GuildUserCommandInteractionCreateEvent
import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.commands.arguments.slash.UserSlashArgument
import net.ormr.kommando.commands.permissions.GuildCommandPermission
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal typealias GuildUserEvent = GuildUserCommandInteractionCreateEvent

public data class GuildUserCommand(
    override val name: String,
    override val permission: GuildCommandPermission?,
    override val executor: UserCommandExecutor<GuildUserEvent, GuildUserCommandData>,
    override val guildId: Snowflake,
) : ContextCommand<User, GuildUserEvent, GuildUserCommandData, GuildCommandPermission>, TopLevelGuildApplicationCommand

public data class GuildUserCommandData(
    override val kommando: Kommando,
    override val event: GuildUserEvent,
) : SlashCommandData<GuildUserEvent, GuildUserCommandInteraction> {
    override val interaction: GuildUserCommandInteraction
        get() = event.interaction
}

@KommandoDsl
public class GuildUserCommandBuilder @PublishedApi internal constructor(
    private val name: String,
    private val guildId: Snowflake,
) : ContextCommandBuilder<GuildUserCommand, User, GuildUserEvent, GuildUserCommandData, GuildCommandPermission>() {
    override fun getEmptyArgument(): UserSlashArgument = UserSlashArgument("", "")

    @PublishedApi
    override fun build(): GuildUserCommand = GuildUserCommand(
        name = name,
        permission = permission,
        executor = getNonNullExecutor(),
        guildId = guildId,
    )
}

@KommandoDsl
public inline fun CommandContainerBuilder.guildUserCommand(
    name: String,
    guildId: Snowflake,
    builder: GuildUserCommandBuilder.() -> Unit,
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    addCommand(GuildUserCommandBuilder(name, guildId).apply(builder).build())
}