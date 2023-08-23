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

import net.ormr.kommando.KommandoBuilder
import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.StringConverter
import net.ormr.kommando.command.CommandMessageConverters.Companion.DEFAULT
import net.pearx.kasechange.CaseFormat
import net.pearx.kasechange.toSnakeCase

public data class CommandMessageConverters(
    public val commandNameConverter: StringConverter,
    public val argumentNameConverter: StringConverter,
) {
    internal companion object {
        internal val DEFAULT: CommandMessageConverters = CommandMessageConverters(
            commandNameConverter = CamelCaseToSnakeCaseConverter,
            argumentNameConverter = CamelCaseToSnakeCaseConverter,
        )
    }
}

private data object CamelCaseToSnakeCaseConverter : StringConverter {
    override fun convert(string: String): String = string.toSnakeCase(from = CaseFormat.CAMEL)
}

@KommandoDsl
public class CommandMessageConvertersBuilder @PublishedApi internal constructor() {
    private var commandNameConverter: StringConverter = DEFAULT.commandNameConverter
    private var argumentNameConverter: StringConverter = DEFAULT.argumentNameConverter

    @KommandoDsl
    public fun commandName(converter: StringConverter) {
        commandNameConverter = converter
    }

    @KommandoDsl
    public fun argumentName(converter: StringConverter) {
        argumentNameConverter = converter
    }

    @PublishedApi
    internal fun build(): CommandMessageConverters = CommandMessageConverters(
        commandNameConverter = commandNameConverter,
        argumentNameConverter = argumentNameConverter,
    )
}

context(KommandoBuilder)
@KommandoDsl
public inline fun commandMessageConverters(builder: CommandMessageConvertersBuilder.() -> Unit) {
    commandMessageConverters = CommandMessageConvertersBuilder().apply(builder).build()
}