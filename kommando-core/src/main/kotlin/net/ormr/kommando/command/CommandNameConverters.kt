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

import net.ormr.kommando.KommandoDsl
import net.ormr.kommando.StringConverter
import net.ormr.kommando.command.CommandNameConverters.Companion.DEFAULT
import net.pearx.kasechange.CaseFormat
import net.pearx.kasechange.toSnakeCase
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public data class CommandNameConverters(
    public val forCommand: StringConverter,
    public val forArgument: StringConverter,
) {
    internal companion object {
        internal val DEFAULT: CommandNameConverters = CommandNameConverters(
            forCommand = CamelCaseToSnakeCaseConverter,
            forArgument = CamelCaseToSnakeCaseConverter,
        )
    }
}

private data object CamelCaseToSnakeCaseConverter : StringConverter {
    override fun convert(string: String): String = string.toSnakeCase(from = CaseFormat.CAMEL)
}

@KommandoDsl
public class CommandNameConvertersBuilder @PublishedApi internal constructor() {
    private var commandNameConverter: StringConverter = DEFAULT.forCommand
    private var argumentNameConverter: StringConverter = DEFAULT.forArgument

    @KommandoDsl
    public fun command(converter: StringConverter) {
        commandNameConverter = converter
    }

    @KommandoDsl
    public fun argument(converter: StringConverter) {
        argumentNameConverter = converter
    }

    @PublishedApi
    internal fun build(): CommandNameConverters = CommandNameConverters(
        forCommand = commandNameConverter,
        forArgument = argumentNameConverter,
    )
}

@KommandoDsl
public inline fun CommandsBuilder.nameConverters(builder: CommandNameConvertersBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    
    nameConverters = CommandNameConvertersBuilder().apply(builder).build()
}