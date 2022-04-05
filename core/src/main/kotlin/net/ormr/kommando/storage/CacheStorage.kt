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

package net.ormr.kommando.storage

import com.google.common.cache.CacheBuilder
import kotlin.time.Duration
import kotlin.time.toJavaDuration

/**
 * A storage which removes any new values after `duration` has passed.
 */
public class CacheStorage<K : Any, V : Any>(duration: Duration) : MutableStorage<K, V> {
    private val cache = CacheBuilder.newBuilder().expireAfterAccess(duration.toJavaDuration()).build<K, V>()

    override val size: Long
        get() = cache.size()

    override fun isEmpty(): Boolean = size <= 0

    override fun get(key: K): V? = cache.getIfPresent(key)

    override fun set(key: K, value: V) {
        cache.put(key, value)
    }

    override fun remove(key: K) {
        cache.invalidate(key)
    }

    override fun contains(key: K): Boolean = get(key) != null
}