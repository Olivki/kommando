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

import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.commands.permissions.ApplicationCommandPermissions
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal typealias GlobalSlashEvent = ChatInputCommandInteractionCreateEvent
internal typealias GlobalSlashInteraction = ChatInputCommandInteraction

public data class GlobalSlashCommand(
    override val name: String,
    override val description: String,
    override val defaultApplicationPermission: Boolean,
    override val applicationPermissions: ApplicationCommandPermissions?,
    override val executor: ApplicationCommandExecutor<GlobalSlashEvent, GlobalSlashCommandData>?,
    override val groups: Map<String, SlashCommandGroup<GlobalSlashSubCommand>>,
    override val subCommands: Map<String, GlobalSlashSubCommand>,
) : SlashCommand<GlobalSlashEvent, GlobalSlashCommandData, GlobalSlashSubCommand>, GlobalApplicationCommand

public data class GlobalSlashCommandData(
    override val kommando: Kommando,
    override val event: GlobalSlashEvent,
) : SlashCommandData<GlobalSlashEvent, GlobalSlashInteraction> {
    override val interaction: GlobalSlashInteraction
        get() = event.interaction
}

@KommandoDsl
public class GlobalSlashCommandBuilder @PublishedApi internal constructor(
    private val name: String,
    private val description: String,
) : SlashCommandBuilder<GlobalSlashCommand, GlobalSlashSubCommand, GlobalSlashEvent, GlobalSlashCommandData>() {
    @PublishedApi
    override fun build(): GlobalSlashCommand = GlobalSlashCommand(
        name = name,
        description = description,
        defaultApplicationPermission = defaultApplicationPermission,
        applicationPermissions = applicationPermissions,
        executor = getExecutorSafe(),
        groups = groups.toMap(),
        subCommands = subCommands.toMap(),
    )
}

@KommandoDsl
public inline fun CommandContainerBuilder.globalSlashCommand(
    name: String,
    description: String,
    builder: GlobalSlashCommandBuilder.() -> Unit,
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    addCommand(GlobalSlashCommandBuilder(name, description).apply(builder).build())
}