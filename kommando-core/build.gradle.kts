import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// TODO: convert this to a multiplatform project once there's support for context receivers for multiplatform projects

@Suppress("DSL_SCOPE_VIOLATION")  // TODO: Remove once we upgrade to Gradle 8.x
plugins {
    alias(libs.plugins.mavenCentralPublish)
    alias(libs.plugins.kotlin.jvm)
}

group = "net.ormr.kommando"
version = "0.1.0-dev-2"

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
    implementation(libs.cache4k)
    implementation(libs.inlineLogger)
    implementation(libs.kasechange)
    implementation(libs.jackson.dataformat.yaml)
    // explicit snakeYaml dependency, because the one jackson uses has vulnerabilities
    implementation(libs.snakeYaml)
    api(libs.kotlinx.collections.immutable)
    api(libs.bundles.kodein)
    api(libs.bundles.kord)

    implementation(kotlin("reflect"))

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