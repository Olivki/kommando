/*
 * Copyright 2022 Oliver Berg
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

package net.ormr.kommando.commands.delegates

import net.ormr.kommando.commands.Command
import net.ormr.kommando.commands.CustomizableCommand
import net.ormr.kommando.commands.arguments.Argument
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

// general design of this is heavily based on 'SynchronizedLazyImpl' from the Kotlin std-lib
@Suppress("ClassName")
internal class ArgumentPropertyDelegate<Cmd, T>(argument: Argument<T, *>) : ReadOnlyProperty<Cmd, T>
        where Cmd : CustomizableCommand,
              Cmd : Command<*> {
    private var argument: Argument<T, *>? = argument
    private val lock = this

    private var value: Any? = NOT_SET

    private object NOT_SET

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Cmd, property: KProperty<*>): T {
        val v1 = value

        if (v1 !== NOT_SET) {
            return v1 as T
        }

        return synchronized(lock) {
            val v2 = value

            if (v2 !== NOT_SET) {
                v2 as T
            } else {
                val retrievedValue = thisRef.getResolvedArgument(property, argument!!.name) as T
                value = retrievedValue
                argument = null
                retrievedValue
            }
        }
    }
}