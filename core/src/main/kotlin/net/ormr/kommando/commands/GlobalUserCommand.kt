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

import dev.kord.core.entity.User
import dev.kord.core.entity.interaction.UserCommandInteraction
import dev.kord.core.event.interaction.UserCommandInteractionCreateEvent
import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.commands.arguments.CommandExecutorArguments
import net.ormr.kommando.commands.arguments.slash.SlashArgument
import net.ormr.kommando.commands.arguments.slash.SlashUserArgument

private typealias GlobalUserEvent = UserCommandInteractionCreateEvent

public data class GlobalUserCommand(
    override val category: String,
    override val name: String,
    override val defaultPermission: Boolean,
    override val permissions: ApplicationCommandPermissions?,
    override val executor: CommandExecutor<SlashArgument<*>, CommandExecutorArguments.Args1<User>, GlobalUserEvent, GlobalUserCommandData>,
) : ApplicationCommand<GlobalUserEvent, GlobalUserCommandData>, GlobalApplicationCommand

public data class GlobalUserCommandData(
    override val kommando: Kommando,
    override val event: GlobalUserEvent,
) : SlashCommandData<GlobalUserEvent, UserCommandInteraction> {
    override val interaction: UserCommandInteraction
        get() = event.interaction
}

@KommandoDsl
public fun CommandGroupBuilder.globalUserCommand(
    name: String,
    defaultPermission: Boolean = true,
    permissions: ApplicationCommandPermissions? = null,
    execute: suspend GlobalUserCommandData.(CommandExecutorArguments.Args1<User>) -> Unit,
) {
    val executor = CommandExecutor(listOf(SlashUserArgument("", "")), execute)
    addCommand(GlobalUserCommand(category, name, defaultPermission, permissions, executor))
}