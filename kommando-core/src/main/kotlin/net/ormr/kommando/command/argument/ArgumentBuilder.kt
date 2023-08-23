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

import com.github.michaelbull.logging.InlineLogger
import net.ormr.kommando.command.CustomizableCommand
import net.ormr.kommando.internal.fixCommand
import net.ormr.kommando.localeBundle
import net.ormr.kommando.localization.BasicMessage
import net.ormr.kommando.localization.Message
import net.ormr.kommando.localization.forEach
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

// TODO: we could probably eliminate the generic for 'Cmd' and just use 'CustomizableCommand<*>'
public class ArgumentBuilder<Cmd, Value, Arg>(
    public val name: Message?,
    public val description: Message?,
    private val argumentFactory: ArgumentFactory<Value, Arg>,
) : PropertyDelegateProvider<Cmd, ReadOnlyProperty<Cmd, Value>>
        where Cmd : CustomizableCommand<*>,
              Arg : Argument<Value, *, *> {
    public fun createArgument(key: String, name: Message, description: Message): Arg =
        argumentFactory.create(key, name, description)

    override fun provideDelegate(thisRef: Cmd, property: KProperty<*>): ReadOnlyProperty<Cmd, Value> {
        // TODO: we only really need to do all this preamble stuff once per launch of the bot
        //       so we could probably implement some sort of cache system to just use a fast path
        //       after registration has been done? We'd need to use mutex rather than synchronization because
        //       coroutines
        //       If we implement some sort of caching system, we can't just naively cache arguments,
        //       because if they have an autoCompleteAction defined that can capture variables that may be
        //       specific to each command instance, so instance we should just cache the resolved messages,
        //       like the name and description
        // TODO: verify that argument name is valid
        //       https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-naming
        val kommando = thisRef.kommando
        val nameConverter = kommando.commandMessageConverters.argumentNameConverter
        val key = nameConverter.convert(property.name)
        val bundle = thisRef.localeBundle
        val argPath = thisRef.componentPath / "arguments" / key
        val name = this.name
            ?: bundle.getMessageOrNull(thisRef, argPath, "name")
            ?: BasicMessage(key)
        name.forEach { _, s ->
            require(s.isNotBlank()) { "Argument name must not be blank" }
            if (s.length !in 1..32) {
                // TODO: custom exception
                throw IllegalArgumentException("Argument name ($s) length (${s.length}) !in 1..32")
            }
        }
        val description = this.description ?: bundle.getMessage(thisRef, argPath, "description")
        val argument = argumentFactory.create(key, name, description)
        val fixedCmd = thisRef.fixCommand()
        logger.debug { "Registered argument $argument for command ${thisRef::class.qualifiedName}#${thisRef.defaultCommandName}" }
        fixedCmd.registry.registerArgument(key, argument)
        return ArgumentPropertyDelegate(argument)
    }

    private companion object {
        private val logger = InlineLogger()
    }
}