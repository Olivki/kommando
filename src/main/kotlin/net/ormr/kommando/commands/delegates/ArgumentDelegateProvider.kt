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
import net.ormr.kommando.commands.CommandArgumentCache
import net.ormr.kommando.commands.CustomizableCommand
import net.ormr.kommando.commands.arguments.Argument
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public class ArgumentDelegateProvider<Cmd, T, Arg : Argument<T, *>>(
    public val name: String?,
    public val argumentCreator: (String) -> Arg,
) : PropertyDelegateProvider<Cmd, ReadOnlyProperty<Cmd, T>>
        where Cmd : CustomizableCommand,
              Cmd : Command<*> {
    @Suppress("UNCHECKED_CAST")
    override fun provideDelegate(thisRef: Cmd, property: KProperty<*>): ReadOnlyProperty<Cmd, T> {
        val name = this.name ?: property.name
        return when {
            CommandArgumentCache.isHydrated(thisRef.javaClass) -> {
                val argument = CommandArgumentCache.getArgument(thisRef.javaClass, name) as Arg
                ArgumentPropertyDelegate(argument)
            }
            else -> {
                val argument = argumentCreator(name)
                CommandArgumentCache.addArgument(thisRef.javaClass, argument)
                ArgumentPropertyDelegate(argument)
            }
        }
    }
}

context(Cmd)
        public fun <T, Cmd, Arg : Argument<T, *>> createDelegate(
    name: String?,
    argumentCreator: (String) -> Arg,
): ArgumentDelegateProvider<Cmd, T, Arg>
        where Cmd : CustomizableCommand,
              Cmd : Command<*> = ArgumentDelegateProvider(name, argumentCreator)