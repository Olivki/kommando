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

import net.ormr.kommando.internal.buildCache
import net.ormr.kommando.storage.MutableStorage
import net.ormr.kommando.storage.asMutexProtectedStorage
import kotlin.time.Duration

public class ComponentStorage internal constructor(
    public val timeout: Duration,
    private val delegate: MutableStorage<String, ExecutableComponent<*, *>>,
) : MutableStorage<String, ExecutableComponent<*, *>> by delegate {
    public suspend fun addGroup(group: ComponentGroup) {
        for ((key, component) in group.executableComponents) {
            put(key, component)
        }
    }

    public suspend fun removeGroup(group: ComponentGroup) {
        for ((key, _) in group.executableComponents) {
            remove(key)
        }
    }
}

public fun ComponentStorage(timeout: Duration): ComponentStorage {
    val cache = buildCache<String, ExecutableComponent<*, *>> {
        expireAfterWrite(timeout)
    }.asMutexProtectedStorage()
    return ComponentStorage(timeout, cache)
}