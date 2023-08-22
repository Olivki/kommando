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

package net.ormr.kommando.command.argument

import net.ormr.kommando.command.CustomizableCommand
import net.ormr.kommando.localeBundle
import net.ormr.kommando.localization.BasicMessage
import net.ormr.kommando.localization.Message
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public class ArgumentBuilder<Cmd, Value, Arg>(
    public val name: Message?,
    public val description: Message?,
    public val argumentFactory: ArgumentFactory<Value, Arg>,
) : PropertyDelegateProvider<Cmd, ReadOnlyProperty<Cmd, Value>>
        where Cmd : CustomizableCommand<*>,
              Arg : Argument<Value, *, *> {
    override fun provideDelegate(thisRef: Cmd, property: KProperty<*>): ReadOnlyProperty<Cmd, Value> {
        // TODO: we only really need to do all this preamble stuff once per launch of the bot
        //       so we could probably implement some sort of cache system to just use a fast path
        //       after registration has been done? We'd need to use mutex rather than synchronization because
        //       coroutines
        val bundle = thisRef.localeBundle
        val argPath = thisRef.componentPath / "arguments" / property.name
        // TODO: check if this works properly
        val name = this.name
            ?: bundle.getMessageOrNull(thisRef, argPath, "name")
            ?: BasicMessage(property.name)
        val description = this.description ?: bundle.getMessage(thisRef, argPath, "description")
        val argument = argumentFactory.create(name, description)
        TODO("registerArgument")
        //thisRef.registerArgument(name, argument)
        return ArgumentPropertyDelegate(argument)
    }
}