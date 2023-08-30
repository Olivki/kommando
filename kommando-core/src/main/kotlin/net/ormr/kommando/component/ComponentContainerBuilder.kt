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

@KommandoDsl
public sealed class ComponentContainerBuilder<in E>
        where E : Component {
    @PublishedApi
    internal abstract val groupId: String

    @PublishedApi
    internal abstract val interactionIdProvider: ComponentInteractionIdProvider

    protected val entries: MutableList<ComponentFactory> = mutableListOf()

    @PublishedApi
    internal fun addExecutable(id: String, factory: (String) -> ExecutableComponent<*, *>) {
        entries.add(ComponentFactory.Executable(id, factory))
    }


    @PublishedApi
    internal fun addVisible(id: String, factory: () -> VisibleComponent<*>) {
        entries.add(ComponentFactory.Visible(id, factory))
    }

    @PublishedApi
    internal fun addRow(factory: ComponentRowFactory) {
        entries.add(ComponentFactory.Row(factory))
    }
}