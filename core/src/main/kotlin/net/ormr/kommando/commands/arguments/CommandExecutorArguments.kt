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

package net.ormr.kommando.commands.arguments

// TODO: better name
public sealed class CommandExecutorArguments {
    public object Args0 : CommandExecutorArguments()

    public data class Args1<A>(public val a: A) : CommandExecutorArguments()

    public data class Args2<A, B>(public val a: A, public val b: B) : CommandExecutorArguments()

    public data class Args3<A, B, C>(public val a: A, public val b: B, public val c: C) : CommandExecutorArguments()

    public data class Args4<A, B, C, D>(
        public val a: A,
        public val b: B,
        public val c: C,
        public val d: D,
    ) : CommandExecutorArguments()

    public data class Args5<A, B, C, D, E>(
        public val a: A,
        public val b: B,
        public val c: C,
        public val d: D,
        public val e: E,
    ) : CommandExecutorArguments()
}
