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
import net.ormr.kommando.localization.Message
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public class ArgumentBuilder<Cmd, Value, Arg>(
    public val name: String?,
    public val description: Message?,
    public val argumentFactory: ArgumentFactory<Value, Arg>,
) : PropertyDelegateProvider<Cmd, ReadOnlyProperty<Cmd, Value>>
        where Cmd : CustomizableCommand<*>,
              Arg : Argument<Value, *, *> {
    override fun provideDelegate(thisRef: Cmd, property: KProperty<*>): ReadOnlyProperty<Cmd, Value> {
        val name = this.name ?: property.name // TODO: this doesn't work?
        // TODO: do we register localized names here?
        require(name.isNotBlank()) { "Argument name may not be blank" }
        val description = this.description ?: run {
            val path = thisRef.componentPath / "arguments" / name
            thisRef.localeBundle.getMessage(thisRef, path, "description")
        }
        val argument = argumentFactory.create(name, description)
        TODO("registerArgument")
        //thisRef.registerArgument(name, argument)
        return ArgumentPropertyDelegate(argument)
    }
}