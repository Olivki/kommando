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

package net.ormr.kommando.modal.component

import net.ormr.kommando.modal.Modal
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class ModalComponentPropertyDelegate<Value>(
    component: ModalComponent<Value, *, *>,
) : ReadOnlyProperty<Modal<*>, Value> {
    private var component: ModalComponent<Value, *, *>? = component
    private val lock = this

    private var value: Any? = NOT_SET

    @Suppress("ClassName")
    private object NOT_SET

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Modal<*>, property: KProperty<*>): Value {
        val v1 = value

        if (v1 !== NOT_SET) {
            return v1 as Value
        }

        return synchronized(lock) {
            val v2 = value

            if (v2 !== NOT_SET) {
                v2 as Value
            } else {
                val retrievedValue = thisRef.modalRegistry.findValue(component!!.customId, property) as Value
                value = retrievedValue
                component = null
                retrievedValue
            }
        }
    }
}