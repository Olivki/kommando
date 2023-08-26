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

package net.ormr.kommando.modal

import net.ormr.kommando.AbstractComponent
import net.ormr.kommando.ComponentPath
import net.ormr.kommando.internal.createUuidString

public abstract class Modal<Value>(name: String, title: String) : AbstractComponent() {
    public val modalName: String = name
    public val modalTitle: String = title
    public val modalId: String = createUuidString()
    internal val modalRegistry by lazy { ModalComponentRegistry(this) }

    @PublishedApi
    internal val modalResponse: DeferredModalResponse<Value> by lazy { DeferredModalResponse(this) }

    final override val componentPath: ComponentPath
        get() = ComponentPath("modals", modalName)

    context(ModalContext)
    public abstract suspend fun execute(): Value
}