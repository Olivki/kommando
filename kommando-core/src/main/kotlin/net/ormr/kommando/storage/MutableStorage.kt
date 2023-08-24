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

package net.ormr.kommando.storage

public interface MutableStorage<Key, Value> : Storage<Key, Value>
        where Key : Any,
              Value : Any {
    /**
     * Stores the given [value] in this storage under the given [key].
     */
    public suspend fun put(key: Key, value: Value)

    /**
     * Removes the value stored under the given [key].
     */
    public suspend fun remove(key: Key)

    /**
     * Removes all values from this storage.
     */
    public suspend fun clear()
}