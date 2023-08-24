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

import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.command.CommandsBuilder
import net.ormr.kommando.command.GlobalTopLevelCommand
import net.ormr.kommando.command.GuildTopLevelCommand
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal typealias GuildCommandPermissionsFactory = suspend (GuildTopLevelCommand) -> GuildCommandPermissions
internal typealias GlobalCommandPermissionsFactory = suspend (GlobalTopLevelCommand) -> GlobalCommandPermissions

public class DefaultCommandPermissions internal constructor(
    private val guildPermissionsFactory: GuildCommandPermissionsFactory?,
    private val globalPermissionsFactory: GlobalCommandPermissionsFactory?,
) {
    public suspend fun getGuildPermissions(command: GuildTopLevelCommand): GuildCommandPermissions? =
        guildPermissionsFactory?.invoke(command)

    public suspend fun getGlobalPermissions(command: GlobalTopLevelCommand): GlobalCommandPermissions? =
        globalPermissionsFactory?.invoke(command)
}

@KommandoDsl
public class DefaultCommandPermissionsBuilder @PublishedApi internal constructor() {
    private var global: GlobalCommandPermissionsFactory? = null
    private var guild: GuildCommandPermissionsFactory? = null

    @KommandoDsl
    public fun guild(factory: GuildCommandPermissionsFactory) {
        guild = factory
    }

    @KommandoDsl
    public fun global(factory: GlobalCommandPermissionsFactory) {
        global = factory
    }

    @PublishedApi
    internal fun build(): DefaultCommandPermissions = DefaultCommandPermissions(guild, global)
}

@KommandoDsl
public inline fun CommandsBuilder.defaultCommandPermissions(builder: DefaultCommandPermissionsBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    defaultCommandPermissions = DefaultCommandPermissionsBuilder().apply(builder).build()
}