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

import dev.kord.common.entity.Permissions
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.command.TopLevelCommand
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public data class GlobalCommandPermissions(
    override val defaultMemberPermissions: Permissions = Permissions(),
    public val isAllowedInDms: Boolean = true,
) : CommandPermissions

@KommandoDsl
public class GlobalCommandPermissionsBuilder @PublishedApi internal constructor() :
    CommandPermissionsBuilder<GlobalCommandPermissions>() {
    public var isAllowedInDms: Boolean = true

    @PublishedApi
    override fun build(): GlobalCommandPermissions = GlobalCommandPermissions(
        defaultMemberPermissions = defaultPermissions,
        isAllowedInDms = isAllowedInDms,
    )
}

public inline fun GlobalCommandPermissions(
    builder: GlobalCommandPermissionsBuilder.() -> Unit,
): GlobalCommandPermissions {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return GlobalCommandPermissionsBuilder().apply(builder).build()
}

context(TopLevelCommand<*, GlobalCommandPermissions>)
@KommandoDsl
public inline fun permissions(
    builder: GlobalCommandPermissionsBuilder.() -> Unit,
): GlobalCommandPermissions {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return GlobalCommandPermissionsBuilder().apply(builder).build()
}