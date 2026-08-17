@Suppress("DSL_SCOPE_VIOLATION")  // TODO: Remove once we upgrade to Gradle 8.x
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

repositories {
    mavenCentral()
}

allprojects {
    repositories {
        mavenCentral()
    }
}