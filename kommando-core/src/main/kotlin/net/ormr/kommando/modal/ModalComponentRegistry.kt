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

import net.ormr.kommando.modal.component.ModalComponent
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KProperty

internal class ModalComponentRegistry(private val modal: Modal<*>) {
    private val isInitialized: AtomicBoolean = AtomicBoolean(false)
    private val components: MutableMap<String, ModalComponent<*, *, *>> = linkedMapOf()
    private var values: Map<String, Any?>? = null

    fun registerComponent(component: ModalComponent<*, *, *>) {
        isInitialized.set(true)
        components[component.customId] = component
    }

    fun findComponent(key: String): ModalComponent<*, *, *> {
        checkInitialized()
        return components[key] ?: error("No component with key '$key' found")
    }

    fun findValue(key: String, property: KProperty<*>): Any? {
        checkInitialized()
        return when (val values = this.values) {
            null -> error(
                """
                    Registry has not yet been populated.
                    Property $property was most likely accessed outside of ${modal::execute}.
                """.trimIndent()
            )
            else -> when (key) {
                in values -> values[key]
                else -> throw NoSuchElementException("No component with key '$key' found")
            }
        }
    }

    fun populate(values: Map<String, Any?>) {
        this.values = values
    }

    fun asMap(): Map<String, ModalComponent<*, *, *>> = components

    private fun checkInitialized() {
        check(isInitialized.get()) { "Modal component registry has not been initialized yet" }
    }
}