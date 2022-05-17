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

import net.ormr.kommando.KommandoBuilder
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.commands.TopLevelGlobalApplicationCommand
import net.ormr.kommando.commands.TopLevelGuildApplicationCommand
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal typealias GlobalCommandPermissionFactory = suspend GlobalCommandPermissionBuilder.(TopLevelGlobalApplicationCommand) -> Unit
internal typealias GuildCommandPermissionFactory = suspend GuildCommandPermissionBuilder.(TopLevelGuildApplicationCommand) -> Unit

public data class DefaultCommandPermissions(
    public val globalPermissionFactory: GlobalCommandPermissionFactory?,
    public val guildPermissionFactory: GuildCommandPermissionFactory?,
)

public class DefaultCommandPermissionsBuilder @PublishedApi internal constructor() {
    private var global: GlobalCommandPermissionFactory? = null
    private var guild: GuildCommandPermissionFactory? = null

    @KommandoDsl
    public fun global(factory: GlobalCommandPermissionFactory) {
        global = factory
    }

    @KommandoDsl
    public fun guild(factory: GuildCommandPermissionFactory) {
        guild = factory
    }

    @PublishedApi
    internal fun build(): DefaultCommandPermissions = DefaultCommandPermissions(global, guild)
}

@KommandoDsl
public inline fun KommandoBuilder.defaultCommandPermissions(builder: DefaultCommandPermissionsBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    defaultCommandPermissions = DefaultCommandPermissionsBuilder().apply(builder).build()
}