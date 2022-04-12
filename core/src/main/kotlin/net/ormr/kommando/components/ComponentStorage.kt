/*
 * MIT License
 *
 * Copyright (c) 2022 Oliver Berg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.ormr.kommando.components

import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoAware
import net.ormr.kommando.storage.MutableStorage
import java.util.concurrent.ConcurrentHashMap

/**
 * Handles the storing and removal of [executable components][ExecutableComponent] for a [kommando] instance.
 *
 * Components are indexed by their [customId][ExecutableComponent.customId].
 */
public class ComponentStorage(override val kommando: Kommando) : MutableStorage<String, ExecutableComponent<*, *>>,
    KommandoAware {
    private val components: MutableMap<String, ExecutableComponent<*, *>> = ConcurrentHashMap()

    override fun get(key: String): ExecutableComponent<*, *>? = components[key]

    override fun contains(key: String): Boolean = key in components

    override fun put(key: String, value: ExecutableComponent<*, *>) {
        components[key] = value
    }

    override fun remove(key: String) {
        components.remove(key)
    }

    override fun clear() {
        components.clear()
    }

    /**
     * Adds all the [executableComponents][ComponentGroup.executableComponents] of the [group] to this storage, indexed
     * by their [customId][ExecutableComponent.customId].
     */
    public fun addComponentGroup(group: ComponentGroup) {
        components.putAll(group.executableComponents)
    }

    /**
     * Removes all the [executableComponents][ComponentGroup.executableComponents] of the [group] from this storage by
     * their [customId][ExecutableComponent.customId].
     */
    public fun removeComponentGroup(group: ComponentGroup) {
        for ((key, _) in group.executableComponents) components.remove(key)
    }
}

/**
 * Adds all the [executableComponents][ComponentGroup.executableComponents] of the [group] to this storage, indexed
 * by their [customId][ExecutableComponent.customId].
 */
public operator fun ComponentStorage.plusAssign(group: ComponentGroup) {
    addComponentGroup(group)
}

/**
 * Removes all the [executableComponents][ComponentGroup.executableComponents] of the [group] from this storage by
 * their [customId][ExecutableComponent.customId].
 */
public operator fun ComponentStorage.minusAssign(group: ComponentGroup) {
    removeComponentGroup(group)
}