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

package net.ormr.exabot

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import dev.kord.common.entity.Snowflake
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.runBlocking
import net.ormr.kommando.bot
import net.ormr.kommando.commands.prefix.context
import net.ormr.kommando.commands.prefix.literal
import org.kodein.db.DB
import org.kodein.db.Value
import org.kodein.db.ValueConverter
import org.kodein.db.impl.open
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

fun main(args: Array<String>) {
    ExabotCli().main(args)
}

class ExabotCli : CliktCommand() {
    val botToken: String by option(help = "The Discord bot token.", envvar = "EXABOT_TOKEN").required()
    val dbFile: Path by option("-db", "--database-file", help = "The file where the DB is stored.")
        .path(canBeDir = false, canBeSymlink = false)
        .defaultLazy { Path("./data/").createDirectories().resolve("db") }

    @OptIn(PrivilegedIntent::class)
    override fun run() = runBlocking {
        bot(
            token = botToken,
            intents = Intents.nonPrivileged + Intent.MessageContent,
            di = {
                bindSingleton { this@ExabotCli }
                bindSingleton(tag = "guildId") { Snowflake(919240592813359105) }
                bindSingleton {
                    DB.open(
                        dbFile.toAbsolutePath().toString(),
                        ValueConverter.forClass<Snowflake> {
                            // kodein-db doesn't support unsigned numbers yet, so we'll store it as a string to prevent
                            // losing any data
                            Value.of(it.toString())
                        }
                    )
                }
            },
        ) {
            prefix { context { literal("!") } }
            Runtime.getRuntime().addShutdownHook(Thread { directDI.instance<DB>().close() })
        }
    }
}