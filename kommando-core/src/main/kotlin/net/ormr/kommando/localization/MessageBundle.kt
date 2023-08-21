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

public interface MessageBundle {
    public fun getMessage(key: String): Message =
        getMessageOrNull(key) ?: throw NoSuchElementException("No message found for key '$key'")

    public fun getMessageOrNull(key: String): Message?

    public operator fun contains(key: String): Boolean

    public fun isEmpty(): Boolean

    public companion object {
        public fun empty(): MessageBundle = EmptyMessageBundle
    }
}

public fun MessageBundle.isNotEmpty(): Boolean = !isEmpty()

private data object EmptyMessageBundle : MessageBundle {
    override fun getMessage(key: String): Message =
        throw UnsupportedOperationException("Can't get message from empty bundle")

    override fun getMessageOrNull(key: String): Message? = null

    override fun contains(key: String): Boolean = false

    override fun isEmpty(): Boolean = true
}