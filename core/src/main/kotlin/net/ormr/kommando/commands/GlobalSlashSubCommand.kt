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

import dev.kord.core.Kord
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.commands.arguments.slash.SlashArgument

public data class GlobalSlashSubCommand(
    override val category: String,
    override val name: String,
    override val description: String,
    override val executor: CommandExecutor<SlashArgument<*>, *, GlobalSlashEvent, GlobalSlashSubCommandData>,
) : SlashSubCommand<GlobalSlashEvent, GlobalSlashSubCommandData>, GlobalApplicationCommand

public data class GlobalSlashSubCommandData(
    override val kord: Kord,
    override val event: GlobalSlashEvent,
) : SlashSubCommandData<GlobalSlashEvent> {
    override val interaction: GlobalSlashInteraction
        get() = event.interaction
}

@KommandoDsl
public class GlobalSlashSubCommandBuilder @PublishedApi internal constructor(
    private val name: String,
    private val description: String,
) : CommandBuilder<GlobalSlashSubCommand, SlashArgument<*>, GlobalSlashEvent, GlobalSlashSubCommandData>() {
    @PublishedApi
    override fun build(category: String): GlobalSlashSubCommand = GlobalSlashSubCommand(
        category = category,
        name = name,
        description = description,
        executor = getNonNullExecutor(),
    )
}

@KommandoDsl
public inline fun GlobalSlashCommandBuilder.subCommand(
    name: String,
    description: String,
    builder: GlobalSlashSubCommandBuilder.() -> Unit,
) {
    // TODO: we can access category easily once we got context receivers
    addSubCommand(GlobalSlashSubCommandBuilder(name, description).apply(builder).build(category = ""))
}

@KommandoDsl
public inline fun SlashCommandGroupBuilder<GlobalSlashSubCommand>.subCommand(
    name: String,
    description: String,
    builder: GlobalSlashSubCommandBuilder.() -> Unit,
) {
    // TODO: we can access category easily once we got context receivers
    addSubCommand(GlobalSlashSubCommandBuilder(name, description).apply(builder).build(category = ""))
}