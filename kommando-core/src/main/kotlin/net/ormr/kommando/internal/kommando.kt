/*
 * Copyright 2026 Oliver Berg
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

package net.ormr.kommando.internal

import dev.kord.core.Kord
import net.ormr.kommando.Kommando
import net.ormr.kommando.KommandoExceptionHandler
import net.ormr.kommando.command.Commands
import net.ormr.kommando.component.ComponentStorage
import net.ormr.kommando.filter.EventFilter
import net.ormr.kommando.listener.EventListener
import net.ormr.kommando.modal.ModalStorage
import org.kodein.di.DirectDI
import org.kodein.di.direct

context(kommando: Kommando)
internal inline val direct: DirectDI
    get() = kommando.direct

context(kommando: Kommando)
internal inline val kord: Kord
    get() = kommando.kord

context(kommando: Kommando)
internal inline val exceptionHandler: KommandoExceptionHandler?
    get() = kommando.exceptionHandler

context(kommando: Kommando)
internal inline val commands: Commands
    get() = kommando.commands

context(kommando: Kommando)
internal inline val modalStorage: ModalStorage
    get() = kommando.modalStorage

context(kommando: Kommando)
internal inline val componentStorage: ComponentStorage
    get() = kommando.componentStorage

context(kommando: Kommando)
internal inline val eventFilters: List<EventFilter<*>>
    get() = kommando.eventFilters

context(kommando: Kommando)
internal inline val eventListeners: List<EventListener>
    get() = kommando.eventListeners