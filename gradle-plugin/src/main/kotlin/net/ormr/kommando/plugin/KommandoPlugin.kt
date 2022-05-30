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

import com.google.devtools.ksp.gradle.KspExtension
import com.google.devtools.ksp.gradle.KspGradleSubplugin
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

class KommandoPlugin : Plugin<Project> {
    private companion object {
        private const val PROCESSOR_VERSION = "0.10.0"
    }

    override fun apply(project: Project) {
        project.plugins.apply(ApplicationPlugin::class.java)
        project.plugins.apply(KspGradleSubplugin::class.java)

        val extension = project.extensions.create("kommando", KommandoExtension::class.java)
        val processorExtension =
            (extension as ExtensionAware).extensions.create("processor", KommandoProcessorExtension::class.java)

        project.extensions.configure<KotlinJvmProjectExtension> {
            (this as ExtensionAware).extensions.configure<NamedDomainObjectContainer<KotlinSourceSet>>("sourceSets") {
                named("main") {
                    kotlin.srcDir("build/generated/ksp/main/kotlin")
                    resources.srcDir("build/generated/ksp/main/resources")
                }
            }
        }

        project.dependencies {
            val processorDependency = "net.ormr.kommando:kommando-processor:${PROCESSOR_VERSION}"
            add("compileOnly", processorDependency)
            add("ksp", processorDependency)
        }

        project.afterEvaluate {
            project.dependencies {
                add("implementation", "net.ormr.kommando:kommando-core:${extension.version}")
            }

            project.extensions.configure<KspExtension> {
                arg("kommando.packageName", processorExtension.packageName)
                arg("kommando.fileName", processorExtension.fileName)
                arg("kommando.autoSearch", processorExtension.autoSearch.toString())
            }
        }
    }
}