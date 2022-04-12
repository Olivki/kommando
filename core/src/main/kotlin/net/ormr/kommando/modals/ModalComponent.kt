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

package net.ormr.kommando.modals

import dev.kord.rest.builder.component.ActionRowBuilder

/*
 * Modal components are similar enough to message components to where there'll be some duplicated code, but they're
 * different enough to where shoving them into the message component system would be ugly and not a nice experience,
 * so we're just making it its own system.
 */

/**
 * Represents a component unique to a [popup modal][Modal].
 */
public sealed interface ModalComponent {
    /**
     * The custom id of this component, needs to be unique for the modal it belongs to.
     */
    public val customId: String

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