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

package net.ormr.kommando.permissions

/**
 * Stores the [name] of the permission along with its [handler].
 */
public data class Permission(
    public val name: String,
    public val handler: PermissionHandler,
    public val ordinal: Int,
) : Comparable<Permission> {
    /**
     * Returns the result of invoking [handler][PermissionHandler.hasPermission] with the given [request].
     */
    public suspend fun checkPermission(request: PermissionRequest): Boolean =
        with(request) {
            with(handler) {
                hasPermission()
            }
        }

    override fun compareTo(other: Permission): Int = ordinal.compareTo(other.ordinal)
}