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

public sealed class SelectMenuBuilder<D : ComponentData<SelectMenuComponentEvent>, out R : ExecutableComponent<SelectMenuComponentEvent, D>> :
    ExecutableComponentBuilder<SelectMenuComponentEvent, D, R>() {
    /**
     * The range of options that can be selected by the user, `1..1` by default.
     *
     * This accepts any range as long as it's within the range of `0..25`.
     */
    public var allowedValues: ClosedRange<Int> = 1..1

    /**
     * The placeholder string to show if no option is selected.
     *
     * Note that if any option is registered with [isDefault][SelectMenuOptionComponentBuilder.isDefault] as `true` then
     * that will take priority over this placeholder string.
     */
    public var placeholder: String? = null
}
