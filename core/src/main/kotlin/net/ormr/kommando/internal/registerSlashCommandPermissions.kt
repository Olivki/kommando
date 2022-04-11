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

package net.ormr.kommando.internal

import dev.kord.common.entity.Snowflake
import net.ormr.kommando.Kommando
import net.ormr.kommando.commands.permissions.ApplicationCommandPermission
import net.ormr.kommando.commands.permissions.GuildApplicationCommandPermissions
import net.ormr.kommando.commands.permissions.PermissionMode

internal suspend fun Kommando.registerSlashCommandPermissions() {
    val sortedPermissions = buildMap<Snowflake, MutableList<PermissionWrapper>> {
        for ((commandId, command) in registeredApplicationCommands) {
            val permissions = command.permissions ?: continue
            for (permission in permissions.guildPermissions) {
                getOrPut(permission.guildId) { mutableListOf() }.add(PermissionWrapper(commandId, permission))
            }
        }
    }

    for ((guildId, wrappers) in sortedPermissions) {
        kord.bulkEditApplicationCommandPermissions(guildId) {
            for ((commandId, permissions) in wrappers) {
                command(commandId) {
                    for (permission in permissions.permissions) {
                        val allow = when (permission.mode) {
                            PermissionMode.ALLOW -> true
                            PermissionMode.DENY -> false
                        }
                        when (permission) {
                            is ApplicationCommandPermission.Role -> role(permission.id, allow)
                            is ApplicationCommandPermission.User -> user(permission.id, allow)
                        }
                    }
                }
            }
        }
    }
}

private data class PermissionWrapper(val commandId: Snowflake, val permissions: GuildApplicationCommandPermissions)