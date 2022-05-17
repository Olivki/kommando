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
import net.ormr.kommando.commands.arguments.slash.UserSlashArgument
import net.ormr.kommando.commands.permissions.GlobalCommandPermission
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

private typealias GlobalUserEvent = UserCommandInteractionCreateEvent

public data class GlobalUserCommand(
    override val name: String,
    override val permission: GlobalCommandPermission?,
    override val executor: ContextCommandExecutor<User, GlobalUserEvent, GlobalUserCommandData>,
) : ContextCommand<User, GlobalUserEvent, GlobalUserCommandData, GlobalCommandPermission>, GlobalApplicationCommand

public data class GlobalUserCommandData(
    override val kommando: Kommando,
    override val event: GlobalUserEvent,
) : SlashCommandData<GlobalUserEvent, UserCommandInteraction> {
    override val interaction: UserCommandInteraction
        get() = event.interaction
}

@KommandoDsl
public class GlobalUserCommandBuilder @PublishedApi internal constructor(private val name: String) :
    ContextCommandBuilder<GlobalUserCommand, User, GlobalUserEvent, GlobalUserCommandData, GlobalCommandPermission>() {
    override fun getEmptyArgument(): UserSlashArgument = UserSlashArgument("", "")

    @PublishedApi
    override fun build(): GlobalUserCommand = GlobalUserCommand(
        name = name,
        permission = permission,
        executor = getNonNullExecutor(),
    )
}

@KommandoDsl
public inline fun CommandContainerBuilder.globalUserCommand(
    name: String,
    builder: GlobalUserCommandBuilder.() -> Unit,
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    addCommand(GlobalUserCommandBuilder(name).apply(builder).build())
}