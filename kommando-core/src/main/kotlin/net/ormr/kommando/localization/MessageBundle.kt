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

package net.ormr.kommando.localization

import net.ormr.kommando.ElementPath

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
    public fun getMessage(path: ElementPath, key: String): Message =
        getMessageOrNull(path, key)
            ?: throw NoSuchElementException("No message found at '${path.asString()}/$key'")

    /**
     * Returns the [Message] associated with the given [key], or `null` if no such message exists.
     */
    public fun getMessageOrNull(path: ElementPath, key: String): Message?

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

private data object EmptyMessageBundle : MessageBundle {
    override fun getMessage(path: ElementPath, key: String): Message =
        throw UnsupportedOperationException("Can't get message from empty bundle")

    override fun getMessageOrNull(path: ElementPath, key: String): Message? = null

    override fun isEmpty(): Boolean = true
}