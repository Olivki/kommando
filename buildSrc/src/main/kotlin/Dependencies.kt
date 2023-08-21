@file:Suppress("ClassName", "SpellCheckingInspection")

object Dependencies {
    const val mockk = "io.mockk:mockk:1.12.2"
    const val kotlinInlineLogger = "com.michael-bull.kotlin-inline-logger:kotlin-inline-logger:1.0.4"

    object kotest {
        private const val version = "5.1.0"

        val runnerJUnit5 = create("runner-junit5")
        val assertionsCore = create("assertions-core")
        val property = create("property")

        private fun create(name: String) = "io.kotest:kotest-$name:$version"
    }

    object kodein {
        object di {
            private const val version = "7.20.2"

            val di = create()
            val diConf = create("conf")

            private fun create(name: String? = null) = "org.kodein.di:kodein-di${name?.let { "-$it" } ?: ""}:$version"
        }
    }

    object kord {
        private const val version = "0.10.0"

        val core = create("core")

        private fun create(name: String) = "dev.kord:kord-$name:$version"

        object x {
            val emoji = "dev.kord.x:emoji:0.5.0"
        }
    }
}