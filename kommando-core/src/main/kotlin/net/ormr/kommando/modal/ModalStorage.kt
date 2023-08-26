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

import io.github.reactivecircus.cache4k.CacheEvent
import net.ormr.kommando.internal.buildCache
import net.ormr.kommando.storage.MutableStorage
import net.ormr.kommando.storage.asMutexProtectedStorage
import kotlin.time.Duration

public class ModalStorage internal constructor(
    public val timeout: Duration,
    private val delegate: MutableStorage<String, Modal<*>>,
) : MutableStorage<String, Modal<*>> by delegate {
    public suspend fun addModal(modal: Modal<*>) {
        put(modal.modalId, modal)
    }

    public suspend fun removeModal(modal: Modal<*>) {
        remove(modal.modalId)
    }
}

public fun ModalStorage(timeout: Duration): ModalStorage {
    val cache = buildCache<String, Modal<*>> {
        expireAfterWrite(timeout)
        eventListener { event ->
            when (event) {
                is CacheEvent.Removed -> {
                    event.value.modalResponse.close()
                }
                is CacheEvent.Expired -> {
                    event.value.modalResponse.close()
                }
                else -> {
                    // do nothing
                }
            }
        }
    }.asMutexProtectedStorage()
    return ModalStorage(timeout, cache)
}