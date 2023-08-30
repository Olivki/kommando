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

package net.ormr.kommando.component

import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.internal.createUuidString
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class ComponentGroupFactoryBuilder @PublishedApi internal constructor(
    override val groupId: String,
    override val interactionIdProvider: ComponentInteractionIdProvider,
) : ComponentContainerBuilder<Component>() {
    @PublishedApi
    internal fun build(): ComponentGroupFactory = ComponentGroupFactory(
        groupId = groupId,
        factories = entries.toList(),
        interactionIdProvider = interactionIdProvider,
    )
}

@PublishedApi
internal data object DefaultInteractionIdProvider : ComponentInteractionIdProvider {
    override fun get(groupId: String, componentId: String): String = createUuidString()
}

// TODO: figure out a better system for creating components with auto generated IDs
//       the current system is not the best, as if the user defines as "static" components
//       the interactionId will always be the same, which might cause issues
//       we could maybe make 'components' return a builder of sorts, that can be used to create
//       components with auto generated IDs, and then the user can use the builder to create 'ComponentGroup' instances

@KommandoDsl
public inline fun components(
    id: String,
    interactionIdProvider: ComponentInteractionIdProvider = DefaultInteractionIdProvider,
    builder: ComponentGroupFactoryBuilder.() -> Unit,
): ComponentGroupFactory {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    return ComponentGroupFactoryBuilder(id, interactionIdProvider).apply(builder).build()
}