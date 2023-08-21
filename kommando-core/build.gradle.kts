/*
 * Copyright 2023 Oliver Berg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// TODO: convert this to a multiplatform project once there's support for context receivers for multiplatform projects

@Suppress("DSL_SCOPE_VIOLATION")  // TODO: Remove once we upgrade to Gradle 8.x
plugins {
    alias(libs.plugins.mavenCentralPublish)
    alias(libs.plugins.kotlin.jvm)
}

group = "net.ormr.kommando"
version = "0.1.0-dev-1"

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("Olivki", "kommando")
    licenseApacheV2()
}

kotlin {
    jvmToolchain(17)
    explicitApi()
}

dependencies {
    implementation(libs.kotlinx.collections.immutable)
    api(libs.bundles.kodein)
    api(libs.bundles.kord)

    testImplementation(kotlin("test"))
}

tasks {
    test {
        useJUnitPlatform()
    }

    withType<KotlinCompile>().all {
        kotlinOptions {
            freeCompilerArgs += "-opt-in=kotlin.contracts.ExperimentalContracts"
            freeCompilerArgs += "-Xcontext-receivers"
            freeCompilerArgs += "-Xjvm-default=all"
        }
    }
}