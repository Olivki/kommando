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

package net.ormr.kommando.modal

import dev.kord.core.behavior.interaction.ModalParentInteractionBehavior
import dev.kord.core.behavior.interaction.response.PopupInteractionResponseBehavior
import net.ormr.kommando.Element
import net.ormr.kommando.internal.ModalResponseCallback
import net.ormr.kommando.internal.showModal0
import kotlin.time.Duration


/**
 * Shows the given [modal] to the user and suspends until the user has interacted with it, or until the request times
 * out.
 *
 * If the request times out, then `null` will be returned, otherwise the value returned by [Modal.execute] will be
 * returned.
 *
 * @param [modal] the modal to show
 * @param [timeout] the timeout for the request, if `null` then the [default timeout][ModalStorage.timeout] will be
 * used
 */
context(Element)
public suspend fun <Value> ModalParentInteractionBehavior.showModal(
    modal: Modal<Value>,
    timeout: Duration? = null,
): ModalResult<Value>? = showModal0(modal, timeout, {}, this)

/**
 * Shows the given [modal] to the user and suspends until the user has interacted with it, or until the request times
 * out.
 *
 * If the request times out, then `null` will be returned, otherwise the value returned by [Modal.execute] will be
 * returned.
 *
 * @param [modal] the modal to show
 * @param [timeout] the timeout for the request, if `null` then the [default timeout][ModalStorage.timeout] will be
 * used
 * @param [onResponse] callback to allow one to use the [PopupInteractionResponseBehavior] returned by the request
 */
context(Element)
public suspend inline fun <Value> ModalParentInteractionBehavior.showModal(
    modal: Modal<Value>,
    timeout: Duration? = null,
    onResponse: ModalResponseCallback,
): ModalResult<Value>? = showModal0(modal, timeout, onResponse, this)