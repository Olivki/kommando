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

package net.ormr.kommando.commands.permissions

import dev.kord.common.entity.Snowflake
import net.ormr.kommando.KommandoDsl

public data class GuildApplicationCommandPermissions(
    public val guildId: Snowflake,
    public val permissions: List<ApplicationCommandPermission>,
)

public typealias GuildApplicationCommandPermissionsContainer = suspend GuildApplicationCommandPermissionsBuilder.() -> Unit

@KommandoDsl
public class GuildApplicationCommandPermissionsBuilder @PublishedApi internal constructor(private val guildId: Snowflake) {
    private val permissions = mutableListOf<ApplicationCommandPermission>()

    /**
     * Allows or denies any role with the given [id] access to the command, depending on the [mode].
     */
    @KommandoDsl
    public fun role(id: Snowflake, mode: ApplicationPermissionMode = ApplicationPermissionMode.ALLOW) {
        permissions += ApplicationCommandPermission.Role(id, mode)
    }

    /**
     * Allows or denies the user with the given [id] access to the command, depending on the [mode].
     */
    @KommandoDsl
    public fun user(id: Snowflake, mode: ApplicationPermissionMode = ApplicationPermissionMode.ALLOW) {
        permissions += ApplicationCommandPermission.User(id, mode)
    }

    @PublishedApi
    internal fun build(): GuildApplicationCommandPermissions =
        GuildApplicationCommandPermissions(guildId, permissions.toList())
}

@KommandoDsl
public inline fun ApplicationCommandPermissionsBuilder.guild(
    guildId: Snowflake,
    builder: GuildApplicationCommandPermissionsBuilder.() -> Unit,
) {
    addPermission(GuildApplicationCommandPermissionsBuilder(guildId).apply(builder).build())
}