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

package net.ormr.kommando.commands.arguments.chat

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ErrorResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.negativeByte
import io.kotest.property.arbitrary.negativeShort
import io.kotest.property.arbitrary.short
import io.kotest.property.checkAll

class ChatShortArgumentGrammarTest : FunSpec({
    val grammar = ChatShortArgument.ArgumentGrammar

    test("Positive short input") {
        checkAll(1_000, Arb.short(min = 0)) {
            grammar.parseToEnd("$it") shouldBe it
        }
    }

    test("Negative short input") {
        checkAll(1_000, Arb.negativeShort()) {
            grammar.parseToEnd("$it") shouldBe it
        }
    }

    test("Decimal number input") {
        grammar.tryParseToEnd("13.37").shouldBeInstanceOf<ErrorResult>()
    }

    test("Out-of-bounds value") {
        testOutOfBounds(grammar, Short.MIN_VALUE, Short.MAX_VALUE)
    }
})