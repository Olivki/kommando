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
import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoDsl
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public data class GuildSlashSubCommand(
    override val name: String,
    override val description: String,
    override val executor: ApplicationCommandExecutor<GuildSlashEvent, GuildSlashSubCommandData>,
    override val guildId: Snowflake,
) : SlashSubCommand<GuildSlashEvent, GuildSlashSubCommandData>, GuildApplicationCommand

public data class GuildSlashSubCommandData(
    override val kommando: Kommando,
    override val event: GuildSlashEvent,
) : SlashSubCommandData<GuildSlashEvent> {
    override val interaction: GuildSlashInteraction
        get() = event.interaction
}

@KommandoDsl
public class GuildSlashSubCommandBuilder @PublishedApi internal constructor(
    private val name: String,
    private val description: String,
    private val guildId: Snowflake,
) : ApplicationCommandBuilder<GuildSlashSubCommand, GuildSlashEvent, GuildSlashSubCommandData>() {
    @PublishedApi
    override fun build(): GuildSlashSubCommand = GuildSlashSubCommand(
        name = name,
        description = description,
        executor = getNonNullExecutor(),
        guildId = guildId,
    )
}

@KommandoDsl
public inline fun GuildSlashCommandBuilder.subCommand(
    name: String,
    description: String,
    builder: GuildSlashSubCommandBuilder.() -> Unit,
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    addSubCommand(GuildSlashSubCommandBuilder(name, description, guildId).apply(builder).build())
}

@KommandoDsl
public inline fun GuildSlashCommandGroupBuilder.subCommand(
    name: String,
    description: String,
    builder: GuildSlashSubCommandBuilder.() -> Unit,
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    addSubCommand(GuildSlashSubCommandBuilder(name, description, guildId).apply(builder).build())
}

@KommandoDsl
public inline fun GuildSlashCommandGroupBuilder.subCommand(
    name: String,
    description: String,
    guildId: Snowflake,
    builder: GuildSlashSubCommandBuilder.() -> Unit,
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    addSubCommand(GuildSlashSubCommandBuilder(name, description, guildId).apply(builder).build())
}