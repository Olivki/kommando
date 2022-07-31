@file:Suppress("ClassName", "SpellCheckingInspection")

object Dependencies {
    object kotest {
        const val version = "5.1.0"

        val runnerJUnit5 = create("runner-junit5")
        val assertionsCore = create("assertions-core")
        val property = create("property")

        private fun create(name: String): String = "io.kotest:kotest-$name:$version"
    }

    const val mockk = "io.mockk:mockk:1.12.2"
}