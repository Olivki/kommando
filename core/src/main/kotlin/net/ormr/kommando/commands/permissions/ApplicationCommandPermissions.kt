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

import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.commands.WithApplicationCommandPermissionsBuilder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public data class ApplicationCommandPermissions(public val guildPermissions: List<GuildApplicationCommandPermissions>)

@KommandoDsl
public class ApplicationCommandPermissionsBuilder @PublishedApi internal constructor() {
    private val permissions = mutableListOf<GuildApplicationCommandPermissions>()

    @PublishedApi
    internal fun addPermission(permission: GuildApplicationCommandPermissions) {
        permissions += permission
    }

    @PublishedApi
    internal fun build(): ApplicationCommandPermissions = ApplicationCommandPermissions(permissions.toList())
}

@KommandoDsl
public inline fun applicationPermissions(builder: ApplicationCommandPermissionsBuilder.() -> Unit): ApplicationCommandPermissions =
    ApplicationCommandPermissionsBuilder().apply(builder).build()

@KommandoDsl
public inline fun WithApplicationCommandPermissionsBuilder.applicationPermissions(
    builder: ApplicationCommandPermissionsBuilder.() -> Unit,
): ApplicationCommandPermissions {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    val perms = ApplicationCommandPermissionsBuilder().apply(builder).build()
    applicationPermissions = perms
    return perms
}