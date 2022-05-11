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

@file:Module

package net.ormr.exabot.modules

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import net.ormr.kommando.commands.commands
import net.ormr.kommando.commands.execute
import net.ormr.kommando.commands.guildSlashCommand
import net.ormr.kommando.processor.Module
import net.ormr.kommando.processor.Tag
import net.ormr.kommando.utils.respond
import org.kodein.db.DB
import org.kodein.db.getById
import org.kodein.db.model.Id

fun greetings(@Tag guildId: Snowflake, db: DB) = commands {
    guildSlashCommand("greet", "Greet the bot!", guildId) {
        execute {
            val deferred = interaction.deferEphemeralResponse()
            val timesGreeted = db.getById<GreetedUser>(user.id)?.timesGreeted ?: 0

            if (timesGreeted < 0) {
                deferred.respond("This is the first time you've greeted me, hello!")
            } else {
                deferred.respond("Hello! You've greeted me ${timesGreeted + 1} times so far!")
            }

            db.put(GreetedUser(user.id, timesGreeted + 1))
        }
    }
}

@Serializable
data class GreetedUser(
    @Id val id: Snowflake,
    val timesGreeted: Int,
)