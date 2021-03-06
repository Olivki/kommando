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

package net.ormr.kommando.components

import dev.kord.rest.builder.component.ActionRowBuilder

// TODO: make a custom select menu type for just adapting an enum, the enum should inherit a custom class containing
//       value name and description
public sealed interface Component {
    /**
     * The width of the component.
     *
     * This does not represent the actual visual width of a component, but rather how much space it takes up per row.
     * Each row can only contain a total width of 5.
     */
    public val width: Int

    /**
     * Whether the component is disabled or not.
     *
     * A disabled component can't be interacted with and is also rendered differently.
     */
    public val isDisabled: Boolean

    /**
     * Builds this component and adds it to the [action row][ActionRowBuilder].
     */
    public fun ActionRowBuilder.buildComponent()
}