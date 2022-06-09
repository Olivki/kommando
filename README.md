# kommando

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

# Info

Only setup using the Gradle plugin will be explained, if you want to set it up manually, you can take a look at what the Gradle plugin actually does and adapt it.

The gradle plugin will setup everything needed for `KSP` to work properly in Gradle, and also include the required dependencies on the `processor` library, and implement `kommando-core`. It also provides a type-safe configuration for the `processor`. It also brings in the [application plugin](https://docs.gradle.org/current/userguide/application_plugin.html).

As the below code samples are only here to show off the available APIs with some brief explanations, for a more complete example, see the [example](./example) module.

## build.gradle.kts
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

## Main file
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

## Components/Bindings argument resolution

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

## Compile time DI bindings

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

## Kommando Components

These are the currently defined Kommando types:
- `net.ormr.kommando.structures.EventListener`
- `net.ormr.kommando.structures.MessageFilter`
- `net.ormr.kommando.commands.CommandContainer`

The elements that are valid component holders are the following:
- Properties
- Functions

Unlike for DI bindings, classes are not supported to be used as component holders, for obvious reasons.

There are currently three different ways for the processor to locate Kommando components:
1. <details>
    <summary>Annotating the components with <code>@Include</code></summary>
   
    ```kotlin
    @Include
    fun listener() = eventListener { }
    ```
   </details>
2. <details>
    <summary>Annotating the file that the components are in with <code>@Module</code></summary>

    A file annotated with `@Module` will cause the processor to look at all the elements in the file that are valid component holders, and have `public`/`internal` visibility.
    If the return type of the element is that of a component, then it'll be registered as if it was annotated with `@Include`.

    ```kotlin
    @file:Module
   
    fun listener() = eventListener { }
    ```
   </details>
4. <details>
    <summary>Having <code>autoSearch</code> set to <code>true</code></summary>

    If `autoSearch` is `true` then the processor will behave as if *every* file in the project has been annotated with the `@Module` annotation, and therefore exhibit the same behavior. 

    ```kotlin
    fun listener() = eventListener { }
    ```
   </details>

There should be no problems from mixing the different styles, as the processor should try to avoid duplicate entries, but it's still best practice to stick to one style, to avoid unforeseen bugs, and to have a consistent code base.

If using approach 2 or 3, then a valid component holder can be excluded from the processor lookup by annotating it with `@Exclude`.

Kommando does not have a specific bias towards any of the approaches, as it is merely up to personal preference.

For consistency’s sake, the below examples all assume approach 1 is used.

### Event Listener

Event listeners allow you to listen to Kord events.

The below code showcases an event listener that listens for any `MessageCreateEvent`s and prints their content out
to std-out.

```kotlin
@Include
fun messagePrinter() = eventListener {
    on<MessageCreateEvent> {
        println(message.content)
    }
}
```

Note that to listen for `MessageCreateEvent` the bot needs to specify `Intent.MessageContent`.

### Message Filter

A message filter will filter any `MessageCreateEvent`s that do not match the given predicate.

By default, the bot will filter out any messages created by itself, and any messages created by bots, this can be
disabled by doing `-ignoreSelf` and `-ignoreBots`, respectively, in the `bot` builder scope.

Message filters *only* work as long as you use [event listeners from Kommando itself](#event-listener), if you directly
register events on a `kord` instance, then Kommando has no way to intercept those.

Note that for message filters to work the bot needs to specify `Intent.MessageContent`.

The below code will make it so that that only messages that contain `dave` will be allowed through.

```kotlin
@Include
fun daveFilter() = messageFilter { "dave" in message.content }
```

### Commands

| Command Type | Guild                 | Global                 |
|--------------|-----------------------|------------------------|
| Slash        | `guildSlashCommand`   | `globalSlashCommand`   |
| User         | `guildUserCommand`    | `globalUserCommand`    |
| Message      | `guildMessageCommand` | `globalMessageCommand` |

All slash commands require a name and description to be defined, while user and message commands only require a description.

All guild commands require a `guildId` to be defined, this is the guild that the command will be registered to.

To make a global command not work in dms, set `isAllowedInDms` to `false` in the `permission` block, like:

```kotlin
permission { 
    isAllowedInDms = false
}
```

A command *requires* a [execute](#the-execute-block) to be defined, unless it has [subCommands](#subcommands) and/or [groups](#command-groups) defined.

The below code registers a global command with the name `ping` that will respond with `Pong!` via an ephemeral 
*(a private)* message to the user whenever they invoke the command.

```kotlin
@Include
fun ping() = commands {
    globalSlashCommand("ping", "It pings you!") {
        execute {
            interaction.respondEphemeral("Pong!")
        }
    }
}
```

The below code registers a global user command that will send the user who invoked the command the user id of the user
that the command was invoked upon, via an ephemeral message.

```kotlin
@Include
fun grabUserId() = commands {
    globalUserCommand("User ID") {
        execute { (user) ->
            interaction.respondEphemeral(user.id.toString())
        }
    }
}
```

The below code registers a global message command that will send the user who invoked the command the message id of the message
that the command was invoked upon, via an ephemeral message.

```kotlin
@Include
fun grabUserId() = commands {
    globalMessageCommand("Message ID") {
        execute { (message) ->
            interaction.respondEphemeral(message.id.toString())
        }
    }
}
```

#### Localization

Kommando currently has no actual support for the localization of commands, this is however, planned to be included in the future.

#### The `execute` block

The `execute` block of a command defines the function that will be executed whenever the command is invoked by a user.

A `execute` function takes `n` arguments and returns a data class containing `n` arguments with the type of the given argument at `n` position.

All available arguments and their respective types can be found in [the arguments section](#arguments).

For example, this `execute` function `execute(string(.., ..))` takes a `StringSlashArgument`, and will thus return a `Args1<String>` instance.

Knowing the above, we can deduce that in the following code:

```kotlin
execute(string("name", "A name")) { (name) ->
    // ...
}
```

that the destructured value `name` is of type `String`, as we passed in a `string` argument to the execute function.

If no arguments is passed into the `execute` function then a `Args0` instance, which is an object, will be returned, and
no value can be extracted from it.

For `user` and `message` commands, the `execute` function does *not* accept arguments, as those commands can only return `User` and `Message`, respectively.

Note that currently there are only functions supporting `execute` blocks with up to 5 arguments, as Discord allows a max
of 25 arguments, more arguments will be supported in the future.

#### Arguments

As of writing, Kommando supports all the [defined Discord application command arguments](https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-option-type), plus some extra:

| Class                      | Shorthand Function | Argument Type                         | Discord Type  |
|----------------------------|--------------------|---------------------------------------|---------------|
| `AttachmentSlashArgument`  | `attachment`       | `Attachment`                          | `ATTACHMENT`  |
| `BooleanSlashArgument`     | `boolean`          | `Boolean`                             | `BOOLEAN`     |
| `ChannelSlashArgument`     | `channel`          | `ResolvedChannel`                     | `CHANNEL`     |
| `DoubleSlashArgument`      | `double`           | `Double`                              | `NUMBER`      |
| `EnumChoiceSlashArgument`  | `enumChoice`       | `Enum<T> & EnumChoiceArgumentAdapter` | `STRING`      |
| `LongSlashArgument`        | `long`             | `Long`                                | `INTEGER`     |
| `MentionableSlashArgument` | `mentionable`      | `Entity`                              | `MENTIONABLE` |
| `RoleSlashArgument`        | `role`             | `Role`                                | `ROLE`        |
| `StringSlashArgument`      | `string`           | `String`                              | `STRING`      |
| `UserSlashArgument`        | `user`             | `User`                                | `USER`        |

All arguments are located in the `net.ormr.kommando.commands.arguments.slash` package.

The shorthand functions can be used as long as one is inside a `SlashContext`. They take the exact same arguments as the
class constructor that they're representing takes, meaning that all examples below can have its shorthand variant 
replaced with its class variant, and they'd exactly the same.

Kommando also supports [the autocomplete feature](https://discord.com/developers/docs/interactions/application-commands#autocomplete) for the following arguments:

- `StringSlashArgument`
- `DoubleSlashArgument`
- `LongSlashArgument`

`EnumChoiceSlashArgument` is currently not supported, and it's debatable if it ever will be supported due to the nature of that argument.

To enable auto-complete for a valid argument, just add a trailing function at the end of constructor, like so:

```kotlin
string("name", "The name") { suggestString { choice("Good name", "Dave") } }
```

The way auto-complete functions are created may be adjusted in the future.

##### Optional Arguments

Kommando natively supports optional arguments, and adds its own type to support arguments with default values.

An argument can also be made optional, by invoking `optional()` on it, like so:

```kotlin
string("name", "The name").optional()
```

When an argument is marked `optional` the argument type is changed from `T` to `T?`. For example, the type of `execute(string(.., ..))` is `Args1<String>`, and the type of `execute(string(.., ..).optional())` if `Args1<String?>`.

The value given to the `execute` function when executed will be `null` if the argument was not supplied by the user invoking the command.

If you want to make an argument optional *and* supply a default value, you can invoke `default` on an argument, like so:

```kotlin
string("name", "The name").default { "Dave" }
```

The `default` value is not something that Discord knows of, it is only known to the bot itself, all that Discord knows is that it's an optional argument.

Note that optional, and default arguments, ***MUST*** be defined at the end of the `execute` arguments, for example `execute(string(.., ..,).optional(), boolean(.., ..))` is invalid, but `execute(boolean(.., ..), string(.., ..).optional())` is valid.
Kommando currently does *not* verify that this is true, however, Discord will reject the command themselves if isn't true.

##### Choice Arguments

Kommando supports arguments with choices, and implements a specific argument for handling enums as string choice arguments.

The following arguments can have choices appended on to them:

- `StringSlashArgument`
- `DoubleSlashArgument`
- `LongSlashArgument`

To create a `string` argument with the choices `jonathan`, `jesper` and `johan` one can do it in two ways:

1. <details>
    <summary>Passing in a list of Strings</summary>

    ```kotlin
    string("cool_names", "Pick a cool name from the list").choices(listOf("Jonathan", "Jesper", "Johan"))
    ```
   
    It of course doesn't matter *how* the given list is created, the only thing that matters is that it's of the type 
    `List<String>`.

    Note that the given list *must not* be empty, or an exception will be thrown.
   </details>
2. <details>
    <summary>Passing in the arguments one by one</summary>

    This option is a cleaner to do if you already know the exact choices to give directly. However, if that is the case,
    it might be smarter to use a `enumChoice` argument.

    ```kotlin
    string("cool_names", "Pick a cool name from the list").choices("Jonathan", "Jesper", "Johan")
    ```
   
    Note that the above variant *requires* the first parameter to be present, `.choices()` will *not* compile.
   </details>

The above options work the same for `DoubleSlashArgument` and `LongSlashArgument` choices, just replace `String` with `Double` or `Long`, respectively.

If you already know the exact choice values at compile time, it's probably better to opt for an `enumChoice` argument instead.

Recreating the above examples with an `enumChoice` arguments would look like this:

```kotlin
enum class CoolName : EnumChoiceArgumentAdapter {
    JONATHAN,
    JESPER,
    JOHAN;
    
    override val choiceName: String = name.capitalize()
}

enumChoice<CoolName>("cool_names", "Pick a cool name from the list")
```

An `enumChoice` argument can be thought of as a specialized variant of a `string` argument, which can convert to and from a given `Enum`, and already has `choices` registered.

`enumChoice` argument will adapt the `enumValues` of the given `Enum` as the choices it uses.

Note that Discord only allows up to 25 choices, and Kommando will throw an exception if given choices that exceed that limit.

#### Subcommands

Kommando natively supports creating [subcommands](https://discord.com/developers/docs/interactions/application-commands#subcommands-and-subcommand-groups).

Command groups are created by invoking the `subCommand` function, which requires a name and description, on a `guildSlashCommand` or a `globalSlashCommand`.

A command can contain both [subcommands](#subcommands) and [command groups](#command-groups) at the same time, but
that is the furthest nesting that is allowed. A `subcommand` can *not* contain a `group` or a `subCommand`, and a
`group` can not contain a `group`.

The below code represents the following behavior:

| Command      | Message Response |
|--------------|------------------|
| `/command a` | `It was 'a'!`    |
| `/command b` | `It was 'b'!`    |

```kotlin
@Include
fun command() = commands {
    globalSlashCommand("command", "It's a command") {
        subCommand("a", "a!") {
            execute {
                interaction.respondEphemeral("It was 'a'!")
            }
        }
        subCommand("b", "b!") {
            execute {
                interaction.respondEphemeral("It was 'b'!")
            }
        }
    }
}
```

Note that if a command has a group and/or a subcommand defined on it, then Kommando will throw an exception if it also has a `execute` block defined.

#### Command Groups

Kommando natively supports creating [command groups](https://discord.com/developers/docs/interactions/application-commands#subcommands-and-subcommand-groups).

Command groups are created by invoking the `group` function, which requires a name and description, on a `guildSlashCommand` or a `globalSlashCommand`.

A command group can only contain [subcommands](#subcommands).

A command can contain both [subcommands](#subcommands) and [command groups](#command-groups) at the same time, but
that is the furthest nesting that is allowed. A `subcommand` can *not* contain a `group` or a `subCommand`, and a
`group` can not contain a `group`.

The below code represents the following behavior:

| Command             | Message Response |
|---------------------|------------------|
| `/command things a` | `It was 'a'!`    |
| `/command things b` | `It was 'b'!`    |

```kotlin
@Include
fun command() = commands {
    globalSlashCommand("command", "It's a command") {
        group("things", "It's things") {
            subCommand("a", "a!") {
                execute {
                    interaction.respondEphemeral("It was 'a'!")
                }
            }
            subCommand("b", "b!") {
                execute {
                    interaction.respondEphemeral("It was 'b'!")
                }
            }
        }
    }
}
```

Note that if a command has a group and/or a subcommand defined on it, then Kommando will throw an exception if it also has a `execute` block defined.

## Message Components

TODO

## Modals

TODO