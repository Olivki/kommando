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

import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.commands.arguments.slash.SlashArgument
import net.ormr.kommando.commands.permissions.CommandPermission

@KommandoDsl
public sealed class SlashCommandBuilder<out C : SlashCommand<E, D, S, P>, S : SlashSubCommand<*, *>, G : SlashCommandGroup<S>, E : SlashEvent, D : CommandData<E>, P : CommandPermission> :
    ApplicationCommandBuilder<C, E, D>(), WithCommandPermissionBuilder<P> {
    protected val groups: MutableMap<String, G> = hashMapOf()
    protected val subCommands: MutableMap<String, S> = hashMapOf()
    override var permission: P? = null

    protected fun getExecutorSafe(): CommandExecutor<SlashArgument<*>, *, E, D>? {
        if (executor == null && (groups.isEmpty() && subCommands.isEmpty())) error("No groups, sub-commands nor executor has been defined.")
        if (executor != null && (groups.isNotEmpty() || subCommands.isNotEmpty())) error("Root executor is useless if groups/sub-commands have been defined.")
        return executor
    }

    @PublishedApi
    internal fun addGroup(group: G) {
        groups[group.name] = group
    }

    @PublishedApi
    internal fun addSubCommand(subCommand: S) {
        subCommands[subCommand.name] = subCommand
    }
}