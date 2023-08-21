plugins {
    id("me.him188.maven-central-publish") version "1.0.0-dev-3"
    kotlin("jvm") version "1.9.0"
}

group = "net.ormr.kommando"
version = "0.0.17-dev-3"

repositories {
    mavenLocal()
    mavenCentral()
}

mavenCentralPublish {
    artifactId = "kommando-core"
    useCentralS01()
    singleDevGithubProject("Olivki", "kommando")
    licenseApacheV2()
}

kotlin {
    explicitApi()
}

dependencies {
    api(kotlin("reflect"))

    api(Dependencies.kodein.di.di)
    api(Dependencies.kodein.di.diConf)

    api(Dependencies.kord.core)
    api(Dependencies.kord.x.emoji)

    implementation(Dependencies.kotlinInlineLogger)

    testImplementation(Dependencies.kotest.runnerJUnit5)
    testImplementation(Dependencies.kotest.assertionsCore)
    testImplementation(Dependencies.kotest.property)

    testImplementation(Dependencies.mockk)
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.contracts.ExperimentalContracts",
                "-Xcontext-receivers",
            )
        }
    }

    test {
        useJUnitPlatform()
    }
}