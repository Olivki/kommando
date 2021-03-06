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

package net.ormr.kommando.utils

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