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

import net.ormr.kommando.commands.arguments.slash.SlashArgument

// TODO: make custom builder exceptions
public sealed class SlashCommandBuilder<out C : SlashCommand<E, D, S>, S : SlashSubCommand<*, *>, E : SlashEvent, D : CommandData<E>> :
    TopLevelApplicationCommandBuilder<C, E, D>() {
    protected val groups: MutableMap<String, SlashCommandGroup<S>> = hashMapOf()
    protected val subCommands: MutableMap<String, S> = hashMapOf()

    protected fun getExecutorSafe(): CommandExecutor<SlashArgument<*>, *, E, D>? {
        if (executor == null && (groups.isEmpty() && subCommands.isEmpty())) error("No groups, sub-commands nor executor has been defined.")
        if (executor != null && (groups.isNotEmpty() || subCommands.isNotEmpty())) error("Root executor is useless if groups/sub-commands have been defined.")
        return executor
    }

    @PublishedApi
    internal fun addGroup(group: SlashCommandGroup<S>) {
        groups[group.name] = group
    }

    @PublishedApi
    internal fun addSubCommand(subCommand: S) {
        subCommands[subCommand.name] = subCommand
    }
}