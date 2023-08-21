plugins {
    id("me.him188.maven-central-publish") version "1.0.0-dev-3"
    kotlin("jvm") version "1.6.21"
}

val kotestVersion: String by project

repositories {
    mavenLocal()
    mavenCentral()
}

subprojects {
    if (name == "core" || name == "processor") {
        apply(plugin = "me.him188.maven-central-publish")
    }
    apply(plugin = "kotlin")

    group = "net.ormr.kommando"

    repositories {
        mavenLocal()
        mavenCentral()
    }

    pluginManager.withPlugin("me.him188.maven-central-publish") {
        mavenCentralPublish {
            artifactId = "kommando-${project.name}"
            useCentralS01()
            singleDevGithubProject("Olivki", "kommando")
            licenseFromGitHubProject("mit")
        }
    }

    kotlin {
        explicitApi()
    }

    dependencies {
        testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
        testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
        testImplementation("io.kotest:kotest-property:$kotestVersion")
        testImplementation("io.mockk:mockk:1.12.2")
    }

    tasks {
        compileKotlin {
            kotlinOptions {
                jvmTarget = "17"
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-opt-in=kotlin.RequiresOptIn",
                    "-opt-in=kotlin.contracts.ExperimentalContracts",
                )
            }
        }
        test {
            useJUnitPlatform()
        }
    }
}