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

import net.ormr.kommando.util.buildHashMap

/**
 * A collection of [Component]s grouped together into rows.
 *
 * @property [id] The unique identifier of the component collection.
 * @property [rows] The rows that make up the component collection.
 */
public data class ComponentGroup(public val id: String, public val rows: ComponentRows) {
    init {
        require(id.isNotBlank()) { "Component collection id must not be blank" }
        require(rows.isNotEmpty()) { "Component collection must have at least one row" }
    }

    /**
     * A map of all the [VisibleComponent]s in this component collection, indexed by their [id][VisibleComponent.id].
     */
    public val components: Map<String, VisibleComponent<*>> by lazy {
        buildHashMap {
            rows.flatForEach { _, component -> put(component.id, component) }
        }
    }

    /**
     * A map of all the [ExecutableComponent]s in this component collection, indexed by their
     * [interactionId][ExecutableComponent.interactionId].
     */
    public val executableComponents: Map<String, ExecutableComponent<*, *>> by lazy {
        when {
            hasExecutableComponents() -> buildHashMap {
                rows.flatForEach { _, component ->
                    if (component is ExecutableComponent<*, *>) {
                        put(component.interactionId, component)
                    }
                }
            }
            else -> emptyMap()
        }
    }

    /**
     * Returns the [VisibleComponent] with the given [id], or `null` if no component with the given `id` exists.
     */
    public fun getComponentOrNull(id: String): VisibleComponent<*>? = components[id]

    /**
     * Returns the [VisibleComponent] with the given [id], or throws an [IllegalArgumentException] if no component with
     * the given `id` exists.
     */
    public fun getComponent(id: String): VisibleComponent<*> =
        components[id] ?: throw IllegalArgumentException("No component with id '$id' found")

    /**
     * Returns the [ExecutableComponent] with the given [interactionId], or `null` if no component with the given
     * `interactionId` exists.
     */
    public fun getExecutableComponentOrNull(interactionId: String): ExecutableComponent<*, *>? =
        executableComponents[interactionId]

    /**
     * Returns the [ExecutableComponent] with the given [interactionId], or throws an [IllegalArgumentException] if no component
     * with the given `interactionId` exists.
     */
    public fun getExecutableComponent(interactionId: String): ExecutableComponent<*, *> =
        executableComponents[interactionId]
            ?: throw IllegalArgumentException("No component with interaction id '$interactionId' found")

    private fun hasExecutableComponents(): Boolean {
        rows.flatForEach { _, component ->
            if (component is ExecutableComponent<*, *>) return true
        }
        return false
    }

    override fun toString(): String = "Components(id='$id', rows=$rows)"
}