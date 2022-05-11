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

package net.ormr.kommando.plugin

open class KommandoProcessorExtension {
    /**
     * The package name of the generated file, `net.ormr.kommando.generated` by default.
     */
    var packageName: String = "net.ormr.kommando.generated"

    /**
     * The file name of the generated file, `GeneratedKommandoSetup` by default.
     */
    var fileName: String = "GeneratedKommandoSetup"

    /**
     * Whether the processor should search all files for module containers, `false` by default.
     *
     * A "module container" is a function/property that returns *exactly* any of the following types:
     * - `net.ormr.kommando.structures.EventListener`
     * - `net.ormr.kommando.structures.MessageFilter`
     * - `net.ormr.kommando.commands.CommandContainer`
     *
     * By default the processor will be looking for two annotations:
     * - **`net.ormr.kommando.processor.Include`**
     *
     *    If the annotated element is a module container then it will be registered as one, otherwise the element will
     *    be bound as a singleton in the DI system.
     * - **`net.ormr.kommando.processor.Module`**
     *
     *    All the function/properties of the annotated file will be checked to see if they're a module container, if
     *    they are then they will be registered to the system.
     *
     * If `autoSearch` is set to `true` then the processor will behave as if every file in the project has been
     * annotated with `@Module`, note that the processor will still continue to work like normal besides that.
     *
     * `autoSearch` is `false` by default as it's rather intrusive as it checks *all* files in the project.
     */
    var autoSearch: Boolean = false
}