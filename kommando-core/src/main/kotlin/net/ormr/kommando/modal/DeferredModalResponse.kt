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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration

@PublishedApi
internal class DeferredModalResponse<Value>(private val modal: Modal<Value>) {
    private val channel: Channel<ModalResult<Value>> = Channel(capacity = 0)
    private val mutex = Mutex()
    private var completed = false

    suspend fun emit(value: ModalResult<Value>) {
        mutex.withLock {
            check(!completed) { "DeferredModalResponse has already been completed" }
            channel.send(value)
            completed = true
        }
    }

    @PublishedApi
    @OptIn(ExperimentalCoroutinesApi::class)
    internal suspend fun await(timeout: Duration): ModalResult<Value>? = select {
        channel.onReceive { it }
        onTimeout(timeout) {
            // TODO: will this cause the above 'channel.onReceive' to be ran first?
            modal.kommando.modalStorage.removeModal(modal)
            null
        }
    }

    fun close() {
        channel.close()
    }
}