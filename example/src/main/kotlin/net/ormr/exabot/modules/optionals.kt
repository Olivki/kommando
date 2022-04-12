/*
 * MIT License
 *
 * Copyright (c) 2022 Oliver Berg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.ormr.exabot.modules

import dev.kord.common.entity.Snowflake
import net.ormr.kommando.commands.arguments.slash.LongSlashArgument
import net.ormr.kommando.commands.arguments.slash.StringSlashArgument
import net.ormr.kommando.commands.arguments.slash.default
import net.ormr.kommando.commands.arguments.slash.optional
import net.ormr.kommando.commands.commands
import net.ormr.kommando.commands.execute
import net.ormr.kommando.commands.guildSlashCommand
import net.ormr.kommando.processor.Tag
import net.ormr.kommando.utils.respondEphemeral

fun optionalCommand(@Tag guildId: Snowflake) = commands("Optionals") {
    guildSlashCommand("optional", "A command with optional arguments", guildId) {
        execute(
            StringSlashArgument("not_optional", "A non optional argument."),
            LongSlashArgument("long", "A long!!"),
            StringSlashArgument("optional", "An optional argument.").optional(),
        ) { (nonOptional, long, optional) ->
            interaction.respondEphemeral("Non-Optional: '$nonOptional' - Optional: '$optional', Long: $long")
        }
    }

    guildSlashCommand("default", "A command with default arguments.", guildId) {
        execute(
            StringSlashArgument("not_default", "A non default argument."),
            StringSlashArgument("default", "A default argument.") default { "hello" },
        ) { (notDefault, default) ->
            interaction.respondEphemeral("Not-Default: '$notDefault' - Default: '$default'")
        }
    }
}