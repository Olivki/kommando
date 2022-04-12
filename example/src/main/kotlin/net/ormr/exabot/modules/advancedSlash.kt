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

import net.ormr.kommando.commands.*
import net.ormr.kommando.commands.arguments.slash.DoubleSlashArgument
import net.ormr.kommando.commands.arguments.slash.LongSlashArgument
import net.ormr.kommando.commands.arguments.slash.StringSlashArgument
import net.ormr.kommando.utils.respondPublic

fun advancedSlash() = commands("Baby!") {
    globalSlashCommand("advanced", "Some more complex/advanced slash commands!") {
        group("type", "Basic group") {
            subCommand("int", "Accepts integers") {
                execute(LongSlashArgument("value", "It's an integer!")) { (value) ->
                    interaction.respondPublic("It's $value")
                }
            }
            subCommand("double", "Accepts doubles!") {
                execute(DoubleSlashArgument("value", "It's a double!")) { (value) ->
                    interaction.respondPublic("It's $value")
                }
            }
            subCommand("string", "Accepts strings!") {
                execute(StringSlashArgument("value", "It's a string!")) { (value) ->
                    interaction.respondPublic("It's '$value'")
                }
            }
        }
        subCommand("steven", "It's Steve!") {
            execute {
                interaction.respondPublic("'ello there!")
            }
        }
    }
}