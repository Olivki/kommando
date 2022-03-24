plugins {
    id("com.google.devtools.ksp") version "1.6.10-1.0.2"
    kotlin("plugin.serialization") version "1.6.10"
}

kotlin {
    explicitApi = null

    sourceSets {
        main {
            kotlin.srcDir("build/generated/ksp/main/kotlin")
        }
    }
}

dependencies {
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("com.github.ajalt.clikt:clikt:3.4.0")
    implementation(project(":core"))

    implementation("org.kodein.db:kodein-db-jvm:0.8.1-beta")
    implementation("org.kodein.db:kodein-db-serializer-kotlinx:0.8.1-beta")
    implementation("org.kodein.db:kodein-leveldb-jni-jvm:0.8.1-beta")

    compileOnly(project(":processor"))
    ksp(project(":processor"))
}

ksp {
    arg("kommando.autoSearch", "true")
}