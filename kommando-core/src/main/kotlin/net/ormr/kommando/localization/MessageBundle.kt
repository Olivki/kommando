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

@file:Suppress("NOTHING_TO_INLINE")

package net.ormr.kommando.localization

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentHashMap
import net.ormr.kommando.KommandoComponentPath
import net.ormr.kommando.util.toPersistentHashMap

/**
 * A bundle of [Message]s.
 *
 * May be represented as a [Map] of [String] to [Message], or some other data structure.
 *
 * Users are encouraged to implement this interface if they wish to use a different data structure than what's provided
 * by default.
 */
public interface MessageBundle {
    /**
     * Returns the [Message] associated with the given [key], or throws a [NoSuchElementException] if no such message
     * exists.
     *
     * @throws [NoSuchElementException] if no message exists for the given [key]
     */
    public fun getMessage(path: KommandoComponentPath, key: String): Message =
        getMessageOrNull(path, key)
            ?: throw NoSuchElementException("No message found for path '${path.asString()}/$key'")

    /**
     * Returns the [Message] associated with the given [key], or `null` if no such message exists.
     */
    public fun getMessageOrNull(path: KommandoComponentPath, key: String): Message?

    /**
     * Returns `true` if this bundle contains a message for the given [key].
     */
    public operator fun contains(key: String): Boolean

    /**
     * Returns `true` if this bundle contains no messages.
     */
    public fun isEmpty(): Boolean
}

/**
 * Returns an empty [MessageBundle].
 */
public fun emptyMessageBundle(): MessageBundle = EmptyMessageBundle

/**
 * Returns an empty [MessageBundle].
 */
public fun messageBundleOf(): MessageBundle = emptyMessageBundle()

/**
 * Returns a [MessageBundle] containing the given [messages].
 */
public fun messageBundleOf(vararg messages: Pair<String, Message>): MessageBundle = when (messages.size) {
    0 -> emptyMessageBundle()
    else -> MapMessageBundle(messages.toPersistentHashMap())
}

/**
 * Returns a [MessageBundle] containing the messages in `this` map.
 */
public fun Map<String, Message>.toMessageBundle(): MessageBundle = when (size) {
    0 -> emptyMessageBundle()
    else -> MapMessageBundle(toPersistentHashMap())
}

/**
 * Returns `true` if this bundle contains any messages.
 */
public inline fun MessageBundle.isNotEmpty(): Boolean = !isEmpty()

private class MapMessageBundle(private val delegate: PersistentMap<String, Message>) : MessageBundle {
    override fun getMessageOrNull(path: KommandoComponentPath, key: String): Message? = delegate[key]

    override fun contains(key: String): Boolean = key in delegate

    override fun isEmpty(): Boolean = delegate.isEmpty()
}

private data object EmptyMessageBundle : MessageBundle {
    override fun getMessage(path: KommandoComponentPath, key: String): Message =
        throw UnsupportedOperationException("Can't get message from empty bundle")

    override fun getMessageOrNull(path: KommandoComponentPath, key: String): Message? = null

    override fun contains(key: String): Boolean = false

    override fun isEmpty(): Boolean = true
}