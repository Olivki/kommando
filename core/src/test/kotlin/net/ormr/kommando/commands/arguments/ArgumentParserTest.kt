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

package net.ormr.kommando.commands.arguments

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty

/*
class ArgumentParserTest : DescribeSpec({
    describe("ArgumentParser.peek()") {
        it("should return 'NULL' when at end") {
            val parser = ArgumentParser("")
            parser.isAtEnd().shouldBeTrue()
            parser.peek() shouldBe ArgumentParser.NULL
        }

        it("should return the character the cursor is currently at") {
            val parser = ArgumentParser("hello")
            parser.peek() shouldBe 'h'
            parser.skip()
            parser.peek() shouldBe 'e'
            parser.skip()
            parser.peek() shouldBe 'l'
            parser.skip()
            parser.peek() shouldBe 'l'
            parser.skip()
            parser.peek() shouldBe 'o'
            parser.skip()
            parser.peek() shouldBe ArgumentParser.NULL
        }
    }

    describe("ArgumentParser.consume()") {
        it("should throw an exception when at end") {
            val parser = ArgumentParser("")
            shouldThrow<ArgumentParsingException> { parser.consume() }
        }

        it("should return current character and move cursor ahead by one") {
            val parser = ArgumentParser("hello")
            parser.consume() shouldBe 'h'
            parser.consume() shouldBe 'e'
            parser.consume() shouldBe 'l'
            parser.consume() shouldBe 'l'
            parser.consume() shouldBe 'o'
            shouldThrow<ArgumentParsingException> { parser.consume() }
        }
    }

    describe("ArgumentParser.consume(Int)") {
        it("should fail with 'ArgumentParsingException' when at end") {
            val parser = ArgumentParser("")
            shouldThrow<ArgumentParsingException> { parser.consume(1) }
        }

        it("should fail with 'IllegalArgumentException' if cursor overflows text length") {
            val parser = ArgumentParser("hel")
            shouldThrow<IllegalArgumentException> { parser.consume(4) }
        }

        it("should return correct sequence") {
            val parser = ArgumentParser("hello")
            parser.consume(4) shouldBe "hell"
            parser.remainingText() shouldBe "o"
        }
    }

    describe("ArgumentParser.consumeUntil((Char) -> Boolean))") {
        val text = "this is text"

        it("consume until end is reached") {
            val parser = ArgumentParser(text)
            parser.consumeUntil { false } shouldBe text
            parser.isAtEnd().shouldBeTrue()
        }

        it("stop at first occurrence of 's'") {
            val parser = ArgumentParser(text)
            parser.consumeUntil { it == 's' } shouldBe "thi"
            parser.remainingText() shouldBe "s is text"
        }

        it("none if true predicate") {
            val parser = ArgumentParser(text)
            parser.consumeUntil { true }.shouldBeEmpty()
            parser.remainingText() shouldBe text
        }
    }

    describe("ArgumentParser.skip()") {
        it("should throw an exception when at end") {
            val parser = ArgumentParser("")
            shouldThrow<ArgumentParsingException> { parser.skip() }
        }

        it("should move the cursor ahead by one") {
            val parser = ArgumentParser("hello")
            parser.skip()
            parser.skip()
            parser.skip()
            parser.skip()
            parser.skip()
            shouldThrow<ArgumentParsingException> { parser.skip() }
        }
    }

    describe("ArgumentParser.skip(Int)") {
        it("should fail with 'ArgumentParsingException' when at end") {
            val parser = ArgumentParser("")
            shouldThrow<ArgumentParsingException> { parser.skip(1) }
        }

        it("should fail with 'IllegalArgumentException' if cursor overflows text length") {
            val parser = ArgumentParser("hel")
            shouldThrow<IllegalArgumentException> { parser.skip(4) }
        }

        it("should move the cursor ahead by the correct amount") {
            val parser = ArgumentParser("hello")
            parser.skip(4)
            parser.remainingText() shouldBe "o"
        }
    }

    describe("ArgumentParser.skipUntil((Char) -> Boolean))") {
        val text = "this is text"

        it("skip until end is reached") {
            val parser = ArgumentParser(text)
            parser.skipUntil { false }
            parser.consumedText() shouldBe text
            parser.isAtEnd().shouldBeTrue()
        }

        it("stop at first occurrence of 's'") {
            val parser = ArgumentParser(text)
            parser.skipUntil { it == 's' }
            parser.consumedText() shouldBe "thi"
            parser.remainingText() shouldBe "s is text"
        }

        it("none if true predicate") {
            val parser = ArgumentParser(text)
            parser.skipUntil { true }
            parser.consumedText().shouldBeEmpty()
            parser.remainingText() shouldBe text
        }
    }

    // TODO: tests for 'skipWhile', 'consumeWhile' and 'peek(() -> T)'
})*/
