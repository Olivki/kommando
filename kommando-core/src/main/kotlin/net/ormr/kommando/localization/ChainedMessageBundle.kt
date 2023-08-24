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

import net.ormr.kommando.ComponentPath

private class ChainedMessageBundle(
    private val left: MessageBundle,
    private val right: MessageBundle,
) : MessageBundle {
    override fun getMessageOrNull(path: ComponentPath, key: String): Message? =
        left.getMessageOrNull(path, key) ?: right.getMessageOrNull(path, key)

    override fun isEmpty(): Boolean = left.isEmpty() && right.isEmpty()
}

public operator fun MessageBundle.plus(other: MessageBundle): MessageBundle = ChainedMessageBundle(this, other)