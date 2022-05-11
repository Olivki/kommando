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
import net.ormr.kommando.KommandoDsl
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public sealed class SlashCommandGroup<S : SlashSubCommand<*, *>> {
    public abstract val name: String
    public abstract val description: String
    public abstract val subCommands: Map<String, S>
}

public data class GlobalSlashCommandGroup(
    override val name: String,
    override val description: String,
    override val subCommands: Map<String, GlobalSlashSubCommand>,
) : SlashCommandGroup<GlobalSlashSubCommand>()

public data class GuildSlashCommandGroup(
    override val name: String,
    override val description: String,
    override val subCommands: Map<String, GuildSlashSubCommand>,
    public val guildId: Snowflake,
) : SlashCommandGroup<GuildSlashSubCommand>()

@KommandoDsl
public sealed class SlashCommandGroupBuilder<S : SlashSubCommand<*, *>, G : SlashCommandGroup<S>> {
    protected val subCommands: MutableMap<String, S> = hashMapOf()

    @PublishedApi
    internal fun addSubCommand(subCommand: S) {
        subCommands[subCommand.name] = subCommand
    }

    @PublishedApi
    internal abstract fun build(): G
}

@KommandoDsl
public class GlobalSlashCommandGroupBuilder @PublishedApi internal constructor(
    private val name: String,
    private val description: String,
) : SlashCommandGroupBuilder<GlobalSlashSubCommand, GlobalSlashCommandGroup>() {
    @PublishedApi
    override fun build(): GlobalSlashCommandGroup = GlobalSlashCommandGroup(name, description, subCommands.toMap())
}

@KommandoDsl
public class GuildSlashCommandGroupBuilder @PublishedApi internal constructor(
    private val name: String,
    private val description: String,
    @PublishedApi internal val guildId: Snowflake,
) : SlashCommandGroupBuilder<GuildSlashSubCommand, GuildSlashCommandGroup>() {
    @PublishedApi
    override fun build(): GuildSlashCommandGroup =
        GuildSlashCommandGroup(name, description, subCommands.toMap(), guildId)
}

@KommandoDsl
public inline fun GlobalSlashCommandBuilder.group(
    name: String,
    description: String,
    builder: GlobalSlashCommandGroupBuilder.() -> Unit,
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    addGroup(GlobalSlashCommandGroupBuilder(name, description).apply(builder).build())
}

@KommandoDsl
public inline fun GuildSlashCommandBuilder.group(
    name: String,
    description: String,
    builder: GuildSlashCommandGroupBuilder.() -> Unit,
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    addGroup(GuildSlashCommandGroupBuilder(name, description, guildId).apply(builder).build())
}
