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

package net.ormr.kommando.command

import net.ormr.kommando.command.argument.Argument
import net.ormr.kommando.noSuchCommandArgument
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KProperty

internal class CommandArgumentRegistry(private val command: Command<*>) {
    private val isInitialized: AtomicBoolean = AtomicBoolean(false)
    private val arguments: MutableMap<String, Argument<*, *, *>> = mutableMapOf() // needs to retain the order
    private var populatedArguments: Map<String, Any?>? = null

    fun registerArgument(key: String, argument: Argument<*, *, *>) {
        isInitialized.set(true)
        arguments[key] = argument
    }

    fun findArgument(key: String): Argument<*, *, *> {
        checkInitialized()
        return arguments[key] ?: noSuchCommandArgument(key, command)
    }

    fun findValue(key: String, property: KProperty<*>): Any? {
        checkInitialized()
        return when (val arguments = populatedArguments) {
            null -> error(
                """
                    Registry has not yet been populated.
                    Property $property was most likely accessed outside of ${command::execute}.
                """.trimIndent()
            )
            else -> when (key) {
                in arguments -> arguments[key]
                else -> throw NoSuchElementException("No argument with key '$key' found")
            }
        }
    }

    fun populate(arguments: Map<String, Any?>) {
        populatedArguments = arguments
    }

    fun asMap(): Map<String, Argument<*, *, *>> = arguments

    fun toMap(): Map<String, Argument<*, *, *>> = arguments.toMap()

    override fun toString(): String = "CommandArgumentRegistry(command=$command)"

    private fun checkInitialized() {
        check(isInitialized.get()) { "Command argument registry has not been initialized yet" }
    }
}