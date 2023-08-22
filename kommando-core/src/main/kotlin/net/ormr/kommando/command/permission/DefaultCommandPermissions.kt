/*
 * Copyright 2023 Oliver Berg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ormr.kommando.command.permission

import net.ormr.kommando.KommandoBuilder
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.command.GlobalCommandType
import net.ormr.kommando.command.GuildCommandType
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal typealias GlobalCommandPermissionsFactory = suspend (GlobalCommandType) -> GlobalCommandPermissions
internal typealias GuildCommandPermissionsFactory = suspend (GuildCommandType) -> GuildCommandPermissions

public class DefaultCommandPermissions(
    public val globalPermissionsFactory: GlobalCommandPermissionsFactory?,
    public val guildPermissionsFactory: GuildCommandPermissionsFactory?,
)

@KommandoDsl
public class DefaultCommandPermissionsBuilder @PublishedApi internal constructor() {
    private var global: GlobalCommandPermissionsFactory? = null
    private var guild: GuildCommandPermissionsFactory? = null

    @KommandoDsl
    public fun global(factory: GlobalCommandPermissionsFactory) {
        global = factory
    }

    @KommandoDsl
    public fun guild(factory: GuildCommandPermissionsFactory) {
        guild = factory
    }

    @PublishedApi
    internal fun build(): DefaultCommandPermissions = DefaultCommandPermissions(global, guild)
}

context(KommandoBuilder)
@KommandoDsl
public inline fun defaultCommandPermissions(
    builder: DefaultCommandPermissionsBuilder.() -> Unit,
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    defaultCommandPermissions = DefaultCommandPermissionsBuilder().apply(builder).build()
}