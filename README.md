# kommando

[Kommando Core](https://img.shields.io/maven-central/v/net.ormr.kommando/kommando-core?label=CORE&style=for-the-badge) [Gradle Plugin](https://img.shields.io/gradle-plugin-portal/v/net.ormr.kommando.plugin?label=gradle%20plugin&style=for-the-badge)

Kommando is a framework for making Discord bots in Kotlin.

This branch is a rewrite that is in progress to make the process of creating commands more like that of [Clikt](https://ajalt.github.io/clikt/).

The main motivation behind changing away from the DSL syntax is because the DSL syntax starts to move towards the right with the indentation very quickly if one needs to create an even slighlty complex command structure, and as such it makes it harder to read the code.

With a structure like that of `Clikt` the larger chunks would be split up into separate classes and as such it would be
easier to see the different functionalities.

This rewrite is currently on an indefinite pause because it *heavily* relies on [context receivers](https://github.com/Kotlin/KEEP/blob/master/proposals/context-receivers.md) to create a nice API for the user. While context receivers no longer poison the binaries as of Kotlin 1.7.20-Beta, the IDE will still start spewing *a lot* of errors when one attempts to use an external library built with context receivers, so until that is fixed, this is on hold. 