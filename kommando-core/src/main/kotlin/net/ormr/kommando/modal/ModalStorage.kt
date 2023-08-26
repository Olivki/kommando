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

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.ormr.kommando.storage.MutableStorage
import kotlin.time.Duration

public class ModalStorage internal constructor(
    public val timeout: Duration,
    private val delegate: MutableMap<String, Modal<*>>,
) : MutableStorage<String, Modal<*>> {
    private val mutex = Mutex()

    override suspend fun get(key: String): Modal<*>? = mutex.withLock {
        delegate[key]
    }

    override suspend fun put(key: String, value: Modal<*>): Unit = mutex.withLock {
        delegate[key] = value
    }

    override suspend fun remove(key: String): Unit = mutex.withLock {
        val modal = delegate.remove(key)
        modal?.modalResponse?.close()
    }

    override suspend fun hasKey(key: String): Boolean = mutex.withLock { key in delegate }

    override suspend fun clear(): Unit = mutex.withLock {
        delegate.clear()
    }

    public suspend fun addModal(modal: Modal<*>) {
        put(modal.modalId, modal)
    }

    public suspend fun removeModal(modal: Modal<*>) {
        remove(modal.modalId)
    }
}

public fun ModalStorage(timeout: Duration): ModalStorage = ModalStorage(timeout, hashMapOf())