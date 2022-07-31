version = "0.0.17"

dependencies {
    api(kotlin("reflect"))

    api(Dependencies.kodein.di.di)
    api(Dependencies.kodein.di.diConf)

    api(Dependencies.kord.core)
    api(Dependencies.kord.x.emoji)

    implementation(Dependencies.kotlinInlineLogger)
}