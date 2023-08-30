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

import com.github.michaelbull.logging.InlineLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.ormr.kommando.KommandoContext
import kotlin.time.Duration

public sealed class ComponentLifetimeManager {
    // TODO: do we want to expose the job to the user somehow?

    context(KommandoContext)
    @PublishedApi
    internal abstract fun launch(group: ComponentGroup, message: LifetimeMessage)

    /**
     * Removes the [ComponentGroup] after the given [duration].
     */
    public data class RemoveAfter(
        val duration: Duration,
        val shouldUnregister: Boolean,
    ) : ComponentLifetimeManager() {
        context(KommandoContext)
        override fun launch(group: ComponentGroup, message: LifetimeMessage) {
            kommando.kord.launch {
                delay(duration)
                logger.debug { "Removing components for group: ${group.id}" }
                message.edit {
                    components = mutableListOf()
                }
                if (shouldUnregister) {
                    kommando.componentStorage.removeGroup(group)
                }
            }
        }
    }

    /**
     * Disables the [ComponentGroup] after the given [duration].
     */
    public data class DisableAfter(
        val duration: Duration,
        val shouldUnregister: Boolean,
    ) : ComponentLifetimeManager() {
        context(KommandoContext)
        override fun launch(group: ComponentGroup, message: LifetimeMessage) {
            kommando.kord.launch {
                delay(duration)
                val disabledGroup = group.copyWithAllDisabled()
                logger.debug { "Disabling components for group: ${group.id}" }
                message.edit {
                    useComponents(disabledGroup)
                }
                if (shouldUnregister) {
                    kommando.componentStorage.removeGroup(group)
                }
            }
        }
    }

    private companion object {
        private val logger = InlineLogger()
    }
}