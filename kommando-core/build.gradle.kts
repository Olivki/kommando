@Suppress("DSL_SCOPE_VIOLATION")  // TODO: Remove once we upgrade to Gradle 8.x
plugins {
    alias(libs.plugins.mavenCentralPublish)
    alias(libs.plugins.kotlin.multiplatform)
}

group = "net.ormr.kommando"
version = "0.1.0-dev-1"

kotlin {
    explicitApi()

    jvm {
        jvmToolchain(17)
        withJava()
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }

    js(IR) {
        nodejs()
    }

    // kord only has support for jvm and js so far, so we only support those targets too
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.bundles.kodein)
                api(libs.bundles.kord)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
    }
}

mavenCentralPublish {
    useCentralS01()
    singleDevGithubProject("Olivki", "kommando")
    licenseApacheV2()
}