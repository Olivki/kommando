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

@file:Suppress("NOTHING_TO_INLINE")

package net.ormr.kommando.component

/**
 * The width of a component.
 *
 * This is not the actual visual width of a component, but rather how much space it takes up per row.
 *
 * The minimum width of a element is [1][ONE], and the maximum is [5][FIVE].
 */
@JvmInline
public value class ComponentWidth private constructor(private val value: Int) {
    public val isMinimum: Boolean get() = value == MIN.asInt()
    public val isMaximum: Boolean get() = value == MAX.asInt()

    public operator fun compareTo(other: ComponentWidth): Int = value.compareTo(other.value)

    public operator fun compareTo(other: Int): Int = value.compareTo(other)

    public fun asInt(): Int = value

    override fun toString(): String = value.toString()

    public companion object {
        public val ONE: ComponentWidth = ComponentWidth(1)
        public val TWO: ComponentWidth = ComponentWidth(2)
        public val THREE: ComponentWidth = ComponentWidth(3)
        public val FOUR: ComponentWidth = ComponentWidth(4)
        public val FIVE: ComponentWidth = ComponentWidth(5)
        public inline val MIN: ComponentWidth get() = ONE
        public inline val MAX: ComponentWidth get() = FIVE
    }
}

public inline operator fun Int.compareTo(other: ComponentWidth): Int = compareTo(other.asInt())