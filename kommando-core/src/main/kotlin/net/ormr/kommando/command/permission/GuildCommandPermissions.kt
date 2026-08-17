/*
 * Copyright 2023-2026 Oliver Berg
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

import dev.kord.common.entity.Permissions
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.command.TopLevelCommand
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public data class GuildCommandPermissions(
    override val defaultMemberPermissions: Permissions = Permissions(),
) : CommandPermissions

@KommandoDsl
public class GuildCommandPermissionsBuilder @PublishedApi internal constructor() :
    CommandPermissionsBuilder<GuildCommandPermissions>() {
    @PublishedApi
    override fun build(): GuildCommandPermissions = GuildCommandPermissions(
        defaultMemberPermissions = defaultPermissions,
    )
}

public inline fun GuildCommandPermissions(
    builder: GuildCommandPermissionsBuilder.() -> Unit,
): GuildCommandPermissions {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return GuildCommandPermissionsBuilder().apply(builder).build()
}

@KommandoDsl
context(_: TopLevelCommand<*, GuildCommandPermissions>)
public inline fun permissions(
    builder: GuildCommandPermissionsBuilder.() -> Unit,
): GuildCommandPermissions {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return GuildCommandPermissionsBuilder().apply(builder).build()
}