plugins {
    id("me.him188.maven-central-publish") version "1.0.0-dev-3"
    kotlin("jvm") version "1.6.10"
}

val kotestVersion: String by project

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "me.him188.maven-central-publish")
    apply(plugin = "kotlin")

    group = "net.ormr.kommando"

    repositories {
        mavenCentral()
    }

    mavenCentralPublish {
        useCentralS01()
        singleDevGithubProject("Olivki", "kommando")
        licenseFromGitHubProject("mit")
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
                freeCompilerArgs = freeCompilerArgs + listOf("-opt-in=kotlin.RequiresOptIn")
            }
        }
        test {
            useJUnitPlatform()
        }
    }
}