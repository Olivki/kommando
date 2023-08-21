pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
    }
}

include("core", "processor", "gradle-plugin")

rootProject.name = "kommando"