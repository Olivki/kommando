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

package net.ormr.kommando.internal

import dev.kord.core.event.interaction.ComponentInteractionCreateEvent
import dev.kord.core.on
import net.ormr.kommando.Kommando
import net.ormr.kommando.components.*

internal suspend fun Kommando.handleComponents() {
    kord.on<ComponentInteractionCreateEvent> {
        // TODO: pass to listener if we implement that system
        when (val component = componentStorage[interaction.componentId] ?: return@on) {
            is ButtonComponent -> {
                check(this is ButtonComponentEvent) { "Mismatch of component and event type. ($component x $this)" }
                component.execute(ButtonComponentData(kommando, this))
            }
            is SelectMenuComponent -> {
                check(this is SelectMenuComponentEvent) { "Mismatch of component and event type. ($component x $this)" }
                component.execute(SelectMenuComponentData(kommando, this))
            }
            is EnumSelectMenuComponent<*> -> {
                check(this is SelectMenuComponentEvent) { "Mismatch of component and event type. ($component x $this)" }
                component.execute(EnumSelectMenuComponentData(kommando, this, component.optionMappings))
            }
        }
    }
}

private suspend fun <E : ComponentInteractionCreateEvent, D : ComponentData<E>> ExecutableComponent<E, D>.execute(
    data: D,
) {
    executor(data)
}