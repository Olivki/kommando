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

import dev.kord.core.entity.User
import net.ormr.kommando.KommandoAware

/**
 * Represents a type that can have permissions set for its action execution.
 */
public interface WithPermissions {
    public val permissions: Permissions?
}

/**
 * Returns `true` if [user] has high enough permission to execute the action belonging to this type, otherwise returns
 * `false`.
 *
 * Note that if [permissions][WithPermissions.permissions] is `null` then `true` will always be returned.
 */
context(KommandoAware)
        public suspend fun WithPermissions.hasPermission(user: User): Boolean {
    val request = user.toPermissionRequest()
    val permissions = permissions ?: return true
    return permissions.getPermissionOrNull(request) != null
}