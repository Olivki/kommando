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

import net.ormr.kommando.internal.buildCache
import net.ormr.kommando.storage.MutableStorage
import net.ormr.kommando.storage.asMutexLockedStorage
import kotlin.time.Duration

public class ModalStorage internal constructor(
    public val expirationDuration: Duration? = null,
    private val delegate: MutableStorage<String, Modal<*>>,
) : MutableStorage<String, Modal<*>> by delegate {
    public suspend fun addModal(modal: Modal<*>) {
        delegate.put(modal.modalId, modal)
    }

    public suspend fun removeModal(modal: Modal<*>) {
        delegate.remove(modal.modalId)
    }
}

public fun ModalStorage(expireAfter: Duration? = null): ModalStorage {
    val storage = buildCache<String, Modal<*>> {
        if (expireAfter != null) {
            expireAfterWrite(expireAfter)
        }
    }.asMutexLockedStorage()
    return ModalStorage(expireAfter, storage)
}