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
import dev.kord.core.behavior.interaction.respondPublic
import net.ormr.kommando.commands.*
import net.ormr.kommando.commands.arguments.slash.SlashDoubleArgument
import net.ormr.kommando.commands.arguments.slash.SlashLongArgument
import net.ormr.kommando.commands.arguments.slash.SlashStringArgument
import net.ormr.kommando.processor.Tag

fun advancedSlash(@Tag guildId: Snowflake) = commands("Baby!") {
    guildSlashCommand("advanced", "Some more complex/advanced slash commands!", guildId) {
        group("type", "Basic group") {
            subCommand("int", "Accepts integers") {
                execute(SlashLongArgument("value", "It's an integer!")) { (value) ->
                    interaction.respondPublic { content = "It's $value" }
                }
            }
            subCommand("double", "Accepts doubles!") {
                execute(SlashDoubleArgument("value", "It's a double!")) { (value) ->
                    interaction.respondPublic { content = "It's $value" }
                }
            }
            subCommand("string", "Accepts strings!") {
                execute(SlashStringArgument("value", "It's a string!")) { (value) ->
                    interaction.respondPublic { content = "It's '$value'" }
                }
            }
        }
        subCommand("steven", "It's Steve!") {
            execute {
                interaction.respondPublic { content = "'ello there!" }
            }
        }
    }
}