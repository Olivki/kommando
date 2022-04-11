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
import net.ormr.kommando.KommandoDsl
import kotlin.reflect.typeOf

/**
 * All the known permissions *(sorted from the highest permission level to lowest)* and the [default] permission.
 */
// TODO: do we even need an explicit 'default' permission?.. We could probably just remove it
public data class Permissions(public val entries: List<Permission>, public val default: Permission)

// adapted from DiscordKts old permission system
/**
 * Returns the first permission that the given [request] is allowed to execute, or [default][Permissions.default]
 * if none can be found.
 *
 * As [entries][Permissions.entries] is sorted from the highest permission level to lowest, the first found value
 * will also be the *highest* permission the `request` is allowed to execute.
 */
public suspend fun Permissions.getPermissionOrNull(request: PermissionRequest): Permission? =
    entries.firstOrNull { it.checkPermission(request) }

/**
 * Returns the first permission that the given [user] is allowed to execute, or `null` if none can be found.
 *
 * As [entries][Permissions.entries] is sorted from the highest permission level to lowest, the first found value
 * will also be the *highest* permission the `user` is allowed to execute.
 */
context(KommandoAware)
        public suspend fun Permissions.getPermissionOrNull(user: User): Permission? =
    getPermissionOrNull(user.toPermissionRequest())

@KommandoDsl
public class PermissionsBuilder @PublishedApi internal constructor() : PermissionRequestResultMarker {
    private val entries = mutableListOf<Permission>()

    private var default: Permission? = null

    /**
     * Sets the default permission to the given [handler] with [name].
     *
     * @throws IllegalArgumentException if `default` has already been set
     */
    @KommandoDsl
    public fun defaultPermission(name: String, handler: PermissionHandler) {
        require(default == null) { "'defaultPermission' has already been invoked." }
        val permission = Permission(name, handler, 0)
        entries.add(0, permission)
        default = permission
    }

    /**
     * Adds a new permission with the given [name] and [handler].
     */
    @KommandoDsl
    public fun permission(name: String, handler: PermissionHandler) {
        entries += Permission(name, handler, entries.size - 1)
    }

    @PublishedApi
    internal fun build(): Permissions = Permissions(
        entries = entries.sortedDescending(),
        default = default ?: error("Missing required 'defaultPermission'."),
    )
}

/**
 * Returns [permissions][Permissions] by invoking the given [builder] function.
 *
 * @throws IllegalStateException if [defaultPermission][PermissionsBuilder.defaultPermission] is never invoked
 */
@KommandoDsl
public inline fun permissions(builder: PermissionsBuilder.() -> Unit): Permissions =
    PermissionsBuilder().apply(builder).build()

/**
 * Returns [permissions][Permissions] from the constants of [T], with the `default` permission being the first
 * constant of `T`.
 *
 * @throws IllegalArgumentException if [T] has no constants
 */
@KommandoDsl
public inline fun <reified T> permissions(): Permissions
        where T : Enum<T>,
              T : PermissionHandler {
    val entries = enumValues<T>()
        .map { Permission(it.name.lowercase(), it, it.ordinal) }
    require(entries.isNotEmpty()) { "${typeOf<T>()} has no constants." }
    return Permissions(entries, entries.first())
}

/**
 * Builds [permissions][Permissions] by invoking the given [builder] function, and sets
 * [commandPermissions][WithPermissionsBuilder.permissions] to the result.
 *
 * @throws IllegalStateException if [defaultPermission][PermissionsBuilder.defaultPermission] is never invoked
 */
@KommandoDsl
public inline fun WithPermissionsBuilder.permissions(builder: PermissionsBuilder.() -> Unit): Permissions {
    val permissions = PermissionsBuilder().apply(builder).build()
    this.permissions = permissions
    return permissions
}

/**
 * Builds [permissions][Permissions] from the constants of [T], with the `default` permission being the first
 * constant of `T`, and sets [commandPermissions][WithPermissionsBuilder.permissions] to the result.
 *
 * @throws IllegalArgumentException if [T] has no constants
 */
@KommandoDsl
public inline fun <reified T> WithPermissionsBuilder.permissions(): Permissions
        where T : Enum<T>,
              T : PermissionHandler {
    val permissions = net.ormr.kommando.permissions.permissions<T>()
    this.permissions = permissions
    return permissions
}