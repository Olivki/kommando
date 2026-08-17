import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.mavenDeployer)
}

group = "net.ormr.kommando"
version = "0.1.0-dev-2"

kotlin {
    jvmToolchain(21)
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

    withType<KotlinJvmCompile>().all {
        compilerOptions {
            freeCompilerArgs.add("-Xwarning-level=DSL_MARKER_APPLIED_TO_WRONG_TARGET:disabled")

            freeCompilerArgs.add("-opt-in=kotlin.contracts.ExperimentalContracts")

            freeCompilerArgs.add("-Xcontext-parameters")
            freeCompilerArgs.add("-Xjvm-default=all")
            freeCompilerArgs.add("-Xcontext-sensitive-resolution")
            freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")
            freeCompilerArgs.add("-Xcollection-literals")
            freeCompilerArgs.add("-Xname-based-destructuring=complete")
        }
    }
}

deployer {
    content {
        kotlinComponents {
            emptyDocs()
        }
    }

    projectInfo {
        url.set("https://github.com/Olivki/kommando")
        license(apache2)
        developer("Olivki", "oliver@berg.moe")

        scm {
            fromGithub("Olivki", "kommando")
        }
    }

    localSpec()

    centralPortalSpec {
        allowMavenCentralSync = false

        auth {
            user.set(secret("CENTRAL_PORTAL_USER"))
            password.set(secret("CENTRAL_PORTAL_PASSWORD"))
        }

        signing {
            key.set(secret("OSSRH_GPG_SECRET_KEY"))
            password.set(secret("OSSRH_GPG_SECRET_KEY_PASSWORD"))
        }
    }
}
