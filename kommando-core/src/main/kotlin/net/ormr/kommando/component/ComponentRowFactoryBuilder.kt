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
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class ComponentRowFactoryBuilder @PublishedApi internal constructor(
    override val groupId: String,
    override val interactionIdProvider: ComponentInteractionIdProvider,
) : ComponentContainerBuilder<VisibleComponent<*>>() {
    @PublishedApi
    internal fun build(): ComponentRowFactory = ComponentRowFactory(
        groupId = groupId,
        factories = entries.toList(),
        interactionIdProvider = interactionIdProvider,
    )
}

@KommandoDsl
public inline fun ComponentContainerBuilder<Component>.row(
    builder: ComponentRowFactoryBuilder.() -> Unit,
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    val row = ComponentRowFactoryBuilder(groupId, interactionIdProvider).apply(builder).build()
    addRow(row)
}