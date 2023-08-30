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

public sealed interface ComponentFactory {
    public class Row(private val factory: ComponentRowFactory) : ComponentFactory {
        public fun create(): ComponentRow = factory.create()
    }

    public class Visible<Comp>(
        public val id: String,
        private val newInstance: () -> Comp,
    ) : ComponentFactory
            where Comp : VisibleComponent<*> {
        public fun create(): Comp = newInstance()
    }

    public class Executable<Comp>(
        public val id: String,
        private val newInstance: (interactionId: String) -> Comp,
    ) : ComponentFactory
            where Comp : VisibleComponent<*> {
        public fun create(interactionId: String): Comp = newInstance(interactionId)
    }
}