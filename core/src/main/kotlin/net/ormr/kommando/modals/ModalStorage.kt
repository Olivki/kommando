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

package net.ormr.kommando.modals

import io.github.reactivecircus.cache4k.Cache
import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoAware
import net.ormr.kommando.storage.MutableStorage
import kotlin.time.Duration

/**
 * Handles the storing and removal of [Modal]s for a [kommando] instance.
 *
 * `Modal`s are stored by their [customId][Modal.customId].
 *
 * @property expirationDuration The duration after write that `Modal`s will automatically be invalidated, if `null`
 * then entries will never be automatically invalidated.
 */
public class ModalStorage(
    override val kommando: Kommando,
    public val expirationDuration: Duration?,
) : MutableStorage<String, Modal>, KommandoAware {
    private val cache: Cache<String, Modal> = Cache.Builder().apply {
        if (expirationDuration != null) expireAfterWrite(expirationDuration)
    }.build()

    override fun get(key: String): Modal? = cache.get(key)

    override fun contains(key: String): Boolean = get(key) != null

    override fun put(key: String, value: Modal) {
        cache.put(key, value)
    }

    override fun remove(key: String) {
        cache.invalidate(key)
    }

    override fun clear() {
        cache.invalidateAll()
    }

    /**
     * Stores the given [modal] in this storage by its [customId][Modal.customId].
     */
    public fun addModal(modal: Modal) {
        put(modal.customId, modal)
    }

    /**
     * Removes the given [modal] from this storage.
     *
     * If `modal` doesn't exist in this storage nothing will happen.
     */
    public fun removeModal(modal: Modal) {
        remove(modal.customId)
    }
}

/**
 * Stores the given [modal] in this storage by its [customId][Modal.customId].
 */
public operator fun ModalStorage.plusAssign(modal: Modal) {
    addModal(modal)
}

/**
 * Removes the given [modal] from this storage.
 *
 * If `modal` doesn't exist in this storage nothing will happen.
 */
public operator fun ModalStorage.minusAssign(modal: Modal) {
    removeModal(modal)
}