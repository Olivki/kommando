## kommando

[Kommando Core](https://img.shields.io/maven-central/v/net.ormr.kommando/kommando-core?label=CORE&style=for-the-badge) [Gradle Plugin](https://img.shields.io/gradle-plugin-portal/v/net.ormr.kommando.plugin?label=gradle%20plugin&style=for-the-badge)

Kommando is a framework for making Discord bots in Kotlin.

Kommando serves as a wrapper around the [kord](https://github.com/kordlib/kord) library, with the goal being to make bot development easier by providing dependency injection and clean DSLs. It takes inspiration from [kordx.commands](https://github.com/kordlib/kordx.commands) and [DiscordKt](https://github.com/DiscordKt/DiscordKt).

The framework should *not* be considered stable, and is considered "use at your own risk", as things can, and will, break between releases, and as the framework is still in its development phase, no deprecation cycles are used. There are currently also no actual unit tests.

The structure of this repo will most likely change in the future.

---

Kommando currently has these features:
- DSL for creating Discord application commands: slash, user and message
- Allows setting default permission settings for guild and global slash commands.
  - If a command explicitly declares a `permission` block, then none of the default settings are inherited.
- Dependency injection powered by [kodein-di](https://kodein.org/di/)
  - Annotations such as `@Include` and `@Exclude` can be used to handle DI components at compile time
  - Annotations for DI are completely optional, and all DI setup can be done via `kodein-di` directly
- Uses [KSP](https://kotlinlang.org/docs/ksp-overview.html) to lookup and find Kommando components at *compile time*, reducing the boilerplate required by other frameworks to setup components, and allows us to offer errors at compile time, rather than at runtime.
  - Kommando can include only components that are *explicitly* defined to be included, or *implicitly* find all valid Kommando components and include them, depending on what the `autoSearch` value is set to on the processor.
  - Using `KSP` is *not* required to use Kommando, setting up a bot without using the KSP functionality is possible, and supported without too much of a hassle. It is however highly recommended to use the KSP way, as it's much cleaner.
- Has a gradle-plugin to remove the boilerplate needed to setup the bot in Gradle.
  - It is possible to setup Kommando without the use of the plugin, but it's discouraged as it's just a lot of boilerplate.
- DSL for creating [message components](https://discord.com/developers/docs/interactions/message-components).
  - The API for this is currently rather ugly, this is mainly due to [context receivers](https://github.com/Kotlin/KEEP/blob/master/proposals/context-receivers.md) being completely unusable in libraries *(due to IDE issues)* and due to the Kord API being rather questionable in its structuring of messages, leading to a lot of duplicated code where there really shouldn't be duplicated code.
  - Most likely going to be marked as experimental in a future release.
- DSL for creating [modals](https://discord.com/developers/docs/interactions/receiving-and-responding#interaction-response-object-modal).
  - The API for this isn't as bad as the `message components` api, but it can still be improved with the use of `context receivers`.
  - Might be marked as experimental in a future release.

Currently, while Kommando does have basic API support for creating chat commands, ie `.ping`, it does not provide any parsing facilities, so chat commands are, at the moment, not properly supported. Whether they will be supported or not is up for debate, as Discord recommends against the use of chat commands.

## Info

Only setup using the Gradle plugin will be explained, if you want to set it up manually, you can take a look at what the Gradle plugin actually does and adapt it.

The gradle plugin will setup everything needed for `KSP` to work properly in Gradle, and also include the required dependencies on the `processor` library, and implement `kommando-core`. It also provides a type-safe configuration for the `processor`. It also brings in the [application plugin](https://docs.gradle.org/current/userguide/application_plugin.html).

As the below code samples are only here to show off the available APIs with some brief explanations, for a more complete example, see the [example](./example) module.

### build.gradle.kts
```kotlin
plugins {
  id("net.ormr.kommando.plugin") version "${GRADLE_PLUGIN_VERSION}"
}

kommando {
    version = "${CORE_VERSION}"
    processor {
        // below are the options available to configure for the processor, for info on what they do,
        // check the kdoc on the properties
        packageName
        fileName
        autoSearch
    }
}
```

### Main file
```kotlin
// fun main() = runBlocking { ... } also works
suspend fun main() {
    bot(
        // the auth token to the Discord bot
        token = BOT_TOKEN,
        // the intents the bot needs, by default this is `Intents.nonPrivileged`
        // note that if the bot needs to read messages, the `Intent.MessageContent` is REQUIRED
        intents = intents,
        // the `PresenceBuilder` function to invoke when logging the bot in, by default this is a NOOP function.
        presence = presence,
        di = {
            // declare any DI bindings here 
            // any DI bindings/components that have been declared at compile time via @Include
            // will be automatically added to this block, so there is no need to call anything here
        },
    ) {
        // the builder block for constructing the bot
        // you can register Kommando components and modify how the bot works here
        // any Kommando components registered at compile time by the processor will be automatically added to this block
        // so there is no need to call anything to register those here
    }
}
```

For more information regarding DI bindings, [see here](https://docs.kodein.org/kodein-di/7.12.0/core/bindings.html).

---

As stated in the above code example, for the `di` block and the `builder` block, the components to those are *automatically* called.

The way the processor works it that it generates two functions, one for injecting the DI bindings declared at compile time into a `DI` instance, and one for injecting the Kommando components discovered at compile time.

Normally, to actually have these values be injected one would need to manually call those two functions in their appropriate scopes, however, the Kommando processor will 
also generate a file containing the exact locations of the two generated functions, which means that at runtime the Kommando system will
invoke those two functions by creating [MethodHandle](https://docs.oracle.com/javase/8/docs/api/java/lang/invoke/MethodHandle.html)s pointing to
the location of them.

Note that the system will *not* fail if the file generated by the processor can't be found, meaning that it's entirely possible to run the framework *without* using the processor.

This is also the only instance of reflection the Kommando framework makes use of, and `MethodHandle`s are much more performant than normal `Method` reflection.

---

### Components/Bindings argument resolution

If a Kommando component/DI binding is represented as a function, ie:
```kotlin
@Include
fun eventListener(name: String) = eventListener {
    // ...
}

@Include
fun diBinding(name: String): String = "hello"
```
Then when registering the component/binding, its arguments *(which in the above case is 1 argument of type `kotlin.String`)* 
will be resolved against the registered DI bindings, allowing for dynamic lookup of values registered to the DI module.

For more fine-grained control over parameter resolution, one can annotate a function argument with `@Tag`, which allows
one to define a specific [tag](https://docs.kodein.org/kodein-di/7.12.0/core/bindings.html#tagged-bindings) to use for
retrieving the value for that argument. If no value is specified for the `@Tag` annotation, then the name of the argument
it is annotated on will be used as it's value, meaning that `(@Tag name: String)` and `(@Tag(value = "name") name: String)`
produce the same code.

Note that while `kodein-di` allows a `tag` to be of the `Any` type, due to limitations in how annotations work, bindings
can only be tagged with `String`s, and retrieved via `String` tags when using annotations.

It is currently *not* possible to retrieve an instance of `Kommando` via a component/bindings arguments.

### Compile time DI bindings

If the `@Include` annotation is tagged on something that is *not* a Kommando component *(as defined in the [Kommando Components](#kommando-components) section)*,
then the processor will process it as a DI binding for the return type of the annotated element.

A binding ***ALWAYS*** needs to be explicitly annotated with `@Include` to be picked up the processor, even if `autoSearch` has been set to `true`.

For example, suppose we have a class `Thing` that has a no-arg constructor, below is how the return type is inferred:

| Element  | Statement                             | Return Type                                     |
|----------|---------------------------------------|-------------------------------------------------|
| Property | `val property: Thing get() = Thing()` | From the return type of the property            |
| Function | `fun function(): Thing = Thing()`     | From the return type of the function            |
| Class    | `class Thing`                         | The return type is the type of the class itself |

Specifically, for `class` elements annotated with `@Include`, the *[primary constructor](https://kotlinlang.org/docs/classes.html#constructors)* of the class
is taken as the producer, basically a `class` element is registered as a producer function of type `(*args) -> T`, where
`*args` is all the arguments the primary constructor takes and `T` is the type of the class.

The arguments of a registered binding are resolved as defined in [Components/Bindings argument resolution](#componentsbindings-argument-resolution).

To give a binding a specific [tag](https://docs.kodein.org/kodein-di/7.12.0/core/bindings.html#tagged-bindings), annotate the element with the `@BindingTag` annotation.

Note that registering a property as a binding is at the moment highly discouraged, and the processor *will* raise a warning
about this behavior, as it's a questionable decision at best, this operation may be entirely disallowed in the future.

It is currently *not* possible to have the processor [import modules](https://docs.kodein.org/kodein-di/7.12.0/core/modules-inheritance.html#modules) via the annotations, this will most likely be supported in the future.

### Kommando Components

These are the currently defined Kommando types:
- `net.ormr.kommando.structures.EventListener`
- `net.ormr.kommando.structures.MessageFilter`
- `net.ormr.kommando.commands.CommandContainer`

There are currently three different ways for the processor to locate Kommando components:
1. Annotating the components with `@Include`
  <details>
    ```kotlin
    @Include
    fun listener() = eventListener { }
    ```
  </details>
2. Annotating the file that the components are in with `@Module`
3. Having `autoSearch` set to `true`

#### Event Listener

#### Message Filter

#### Commands