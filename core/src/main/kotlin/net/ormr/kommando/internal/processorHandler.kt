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

package net.ormr.kommando.internal

import com.github.michaelbull.logging.InlineLogger
import io.ktor.utils.io.core.*
import net.ormr.kommando.KommandoBuilder
import org.kodein.di.DI
import java.lang.ClassLoader.getSystemResourceAsStream
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles.Lookup
import java.lang.invoke.MethodType.methodType
import java.nio.ByteBuffer

private val logger = InlineLogger()

@PublishedApi
internal fun readProcessorMetadata(lookup: Lookup): ProcessorMetadata {
    val bytes = getSystemResourceAsStream(".kommando_processor_metadata")?.use { it.readBytes() } ?: return run {
        logger.warn { "Could not find processor metadata file, was project created without the processor?" }
        ProcessorMetadata.EMPTY
    }
    val buffer = ByteBuffer.wrap(bytes)

    fun readString(): String {
        val size = buffer.int
        val array = ByteArray(size)
        buffer.get(array)
        return array.decodeToString()
    }

    val clz = Class.forName(readString())
    val diSetup = lookup.findStatic(clz, readString(), methodType(Void.TYPE, DI.MainBuilder::class.java))
    val kommandoSetup = lookup.findStatic(clz, readString(), methodType(Void.TYPE, KommandoBuilder::class.java))
    return ProcessorMetadata(diSetup, kommandoSetup)
}

@PublishedApi
internal data class ProcessorMetadata(val diSetup: MethodHandle?, val kommandoSetup: MethodHandle?) {
    companion object {
        val EMPTY: ProcessorMetadata = ProcessorMetadata(null, null)
    }
}