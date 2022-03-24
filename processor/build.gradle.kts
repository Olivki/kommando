plugins {
    kotlin("kapt")
}

version = "0.0.1"

dependencies {
    implementation("com.squareup:kotlinpoet-ksp:1.10.2")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.6.10-1.0.4")
    implementation(project(":core"))

    kapt("com.google.auto.service:auto-service:1.0.1")
    compileOnly("com.google.auto.service:auto-service:1.0.1")
}