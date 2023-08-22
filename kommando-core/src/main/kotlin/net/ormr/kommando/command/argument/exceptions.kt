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

package net.ormr.kommando.command.argument

import kotlin.reflect.typeOf

@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
public class NoSuchArgumentWithTypeException(
    public val name: String,
    public val type: ArgumentType<*>,
) : RuntimeException("Could not find argument '$name' of type $type.name")

context(Arg)
internal inline fun <reified Arg> noConversionSupport(): Nothing
        where Arg : Argument<*, *, *> =
    throw UnsupportedOperationException("'convertArgumentValue' for ArgValue should never be called by ${typeOf<Arg>()}")

context(Arg)
internal inline fun <reified Arg> noNullableConversionSupport(): Nothing
        where Arg : Argument<*, *, *> =
    throw UnsupportedOperationException("'convertNullableArgumentValue' for ArgValue should never be called by ${typeOf<Arg>()}")