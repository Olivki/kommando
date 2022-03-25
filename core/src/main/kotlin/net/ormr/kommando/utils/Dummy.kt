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

package net.ormr.kommando.utils

/**
 * A type that only exists for making overload ambiguity work for constructors and factory functions.
 *
 * The below code will not compile as the compiler will complain about overload ambiguity between the constructor and
 * the factory function.
 * ```kotlin
 *  sealed class Example(val value: Any)
 *
 *  private class ExampleImpl(value: Any) : Example(value)
 *
 *  fun Example(value: Any): Example = ExampleImpl(value)
 * ```
 * However, the below code will compile just fine, as there is now an actual difference between the two.
 * ```kotlin
 *  sealed class Example(val value: Any, dummy: Dummy)
 *
 *  private class ExampleImpl(value: Any) : Example(value, Dummy)
 *
 *  fun Example(value: Any): Example = ExampleImpl(value)
 * ```
 * One could just use any other `object` for this purpose, like [Unit], but using this type its clearer what the actual
 * purpose of the unused parameter is.
 */
public object Dummy