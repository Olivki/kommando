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