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

package net.ormr.kommando.processor

import net.ormr.kommando.KommandoBuilder
import net.ormr.kommando.commands.CommandContainer
import net.ormr.kommando.structures.EventListener

internal const val INCLUDE = "net.ormr.kommando.processor.Include"

/**
 * Includes the declaration in a module.
 *
 * The semantics of what an inclusion is depends on the return type of the declaration:
 * - **Kommando Type** *([EventListener], [CommandContainer])*: The declaration will be added to the appropriate collection
 * *(i.e; `EventListener` goes into [eventListeners][KommandoBuilder.eventListeners])*. In case the declaration is a
 * function, then all of its parameters will be retrieved from the DI module tied to the builder.
 * - **Everything else**: The declaration will be bound as a **singleton** to the DI storage. In case the declaration
 * is a function, then all of its parameters will be retrieved from the DI module.
 *
 * See the [Tag] annotation for more advanced use, and the [Module] annotation for automatic resolution of kommando
 * types.
 *
 * For more advanced control over the DI bindings, it's highly recommended to manually define all DI bindings inside
 * the DI block. This annotation is only for meant for *very simple* usage when used for DI binding purposes.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
public annotation class Include

internal const val EXCLUDE = "net.ormr.kommando.processor.Exclude"

/**
 * Excludes the declaration from a module.
 *
 * This only matters if the annotated declaration has a kommando type *([EventListener], [CommandContainer])* as its return
 * type and the file its stored in is annotated with [Module].
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
public annotation class Exclude

internal const val TAG = "net.ormr.kommando.processor.Tag"

/**
 * Sets the [tag](https://docs.kodein.org/kodein-di/7.10/core/bindings.html#tagged-bindings) that should be used for
 * retrieving a binding of the same type as the annotated parameter.
 *
 * Note that this *only* supports retrieving bindings declared with a tag of type `String`. This is important to keep
 * in mind when setting up tagged bindings manually in the DI block.
 *
 * By default, if a parameter is not annotated then no tag will be used when retrieving the binding.
 *
 * If [value] is left empty then the name of the parameter will be used, i.e; `@Tag guildId: Snowflake` will act the
 * same as `@Tag("guildId") guildId: Snowflake`.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
public annotation class Tag(val value: String = "")

internal const val BINDING_TAG = "net.ormr.kommando.processor.BindingTag"

/**
 * Sets the [tag](https://docs.kodein.org/kodein-di/7.10/core/bindings.html#tagged-bindings) that should be used when
 * binding the declaration to the DI system.
 *
 * By default, if a declaration is not annotated then they will not be given a tag.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
public annotation class BindingTag(val value: String)

/**
 * Marks the entire file as a kommando module file.
 *
 * A Kommando module file will have all properties and functions that return a kommando type *([EventListener],
 * [CommandContainer])* automatically registered. It works the same if one were to manually annotate them
 * all with the [Include] annotation.
 *
 * For more information on what inclusion entails, see [Include].
 *
 * To exclude a kommando type from automatic inclusion, annotate it with [Exclude].
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FILE)
public annotation class Module

internal const val MODULE = "net.ormr.kommando.processor.Module"