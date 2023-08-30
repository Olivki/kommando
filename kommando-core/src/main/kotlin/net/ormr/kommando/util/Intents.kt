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

package net.ormr.kommando.util

import dev.kord.core.enableEvent
import dev.kord.core.event.Event
import dev.kord.gateway.Intents
import net.ormr.kommando.MissingEventIntentException
import kotlin.reflect.typeOf

/**
 * Throws an [MissingEventIntentException] if the required intents for [T] are not available in [requestedIntents].
 */
public inline fun <reified T : Event> checkIntents(requestedIntents: Intents) {
    val requiredIntents = Intents { enableEvent<T>() }

    if (requiredIntents !in requestedIntents) {
        throw MissingEventIntentException(typeOf<T>(), requiredIntents - requestedIntents)
    }
}

/**
 * Throws an [MissingEventIntentException] if the required intents for [T] are not available in [requestedIntents].
 */
public inline fun <reified T : Event> T.checkEventIntents(requestedIntents: Intents) {
    checkIntents<T>(requestedIntents)
}