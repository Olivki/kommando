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

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.0.0-rc-1"
}

version = "0.2.0"
description = "Gradle plugin for the Kommando framework"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly(kotlin("gradle-plugin-api"))
    compileOnly(kotlin("gradle-plugin"))
    compileOnly(gradleApi())

    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.6.21-1.0.5")
}

pluginBundle {
    website = "https://github.com/Olivki/kommando"
    vcsUrl = "https://github.com/Olivki/kommando"
    tags = listOf("helper", "utility")
}

gradlePlugin {
    plugins {
        create("kommando-plugin") {
            id = "net.ormr.kommando.plugin"
            displayName = "Kommando Gradle Plugin"
            description = project.description
            implementationClass = "net.ormr.kommando.plugin.KommandoPlugin"
        }
    }
}

kotlin {
    explicitApi = null
}