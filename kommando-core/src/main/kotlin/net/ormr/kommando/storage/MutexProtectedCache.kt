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

import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class MutexProtectedCache<Key, Value>(private val cache: Cache<Key, Value>) : MutableStorage<Key, Value>
        where Key : Any,
              Value : Any {
    private val mutex = Mutex()

    override suspend fun get(key: Key): Value? = mutex.withLock {
        cache.get(key)
    }

    override suspend fun put(key: Key, value: Value): Unit = mutex.withLock {
        cache.put(key, value)
    }

    override suspend fun remove(key: Key): Unit = mutex.withLock {
        cache.invalidate(key)
    }

    override suspend fun hasKey(key: Key): Boolean = mutex.withLock {
        cache.get(key) != null
    }

    override suspend fun clear(): Unit = mutex.withLock {
        cache.invalidateAll()
    }
}

internal fun <Key, Value> Cache<Key, Value>.asMutexProtectedStorage(): MutableStorage<Key, Value>
        where Key : Any,
              Value : Any = MutexProtectedCache(this)