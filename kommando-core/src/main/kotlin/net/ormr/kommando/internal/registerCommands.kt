/*
 * Copyright 2023 Oliver Berg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ormr.kommando.internal

import com.github.michaelbull.logging.InlineLogger
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.entity.application.ApplicationCommand
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.rest.builder.interaction.*
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.toPersistentHashMap
import net.ormr.kommando.Kommando
import net.ormr.kommando.command.*
import net.ormr.kommando.command.argument.Argument
import net.ormr.kommando.command.argument.ArgumentBuildContext
import net.ormr.kommando.command.factory.*
import net.ormr.kommando.command.permission.DefaultCommandPermissions
import net.ormr.kommando.defaultComponentDescription
import net.ormr.kommando.localization.toMutableMapOrNull
import org.kodein.di.DirectDI
import org.kodein.di.direct
import kotlin.reflect.KType
import dev.kord.rest.builder.interaction.BaseInputChatBuilder as KordBaseInputChatBuilder
import dev.kord.rest.builder.interaction.SubCommandBuilder as KordSubCommandBuilder

// Here be dragons

// TODO: verify command names?
//       https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-naming
//       localized names follow the same rules as normal names

// TODO: verify that arguments are ordered correctly (required before optional)

private val logger = InlineLogger()

context(Kommando)
internal suspend fun registerCommands(
    factories: List<CommandFactory<*>>,
): Map<Snowflake, RegisteredCommand> {
    val commandWrappers = createWrappers(direct, factories)
    val globalCommandWrappers = commandWrappers
        .filter { it.instance is GlobalTopLevelCommand }
    val guildCommandWrappers = commandWrappers
        .filter { it.instance is GuildTopLevelCommand }
        .mergeGuildCommands(kord)
    check(globalCommandWrappers.size + guildCommandWrappers.size == factories.size) { unknownCommandType(commandWrappers) }
    val commandCache = commandWrappers
        .associateByTo(hashMapOf(), { it.instance.toCommandKey() }, CommandWrapper::factory)
    val commandGroupCache = commandWrappers
        .filterIsInstance<ParentCommandWrapper>()
        .associateByTo(
            hashMapOf(),
            { it.instance.toCommandKey() },
            { parent ->
                parent
                    .children
                    .filterIsInstance<CommandGroupWrapper>()
                    .associateByTo(
                        hashMapOf(),
                        { it.instance.defaultGroupName },
                        {
                            RegisteredGroup(
                                factory = it.factory,
                                subCommands = (it.subCommands zip it.subCommandFactories)
                                    .associateTo(hashMapOf()) { (command, factory) -> command.defaultCommandName to factory },
                            )
                        },
                    )
            }
        )
    val subCommandCache = commandWrappers.filterIsInstance<ParentCommandWrapper>()
        .associateBy(
            { it.instance.toCommandKey() },
            { parent ->
                parent
                    .children
                    .filterIsInstance<SubCommandWrapper>()
                    .associateByTo(hashMapOf(), { it.instance.defaultCommandName }, { it.factory })
            }
        )
    val defaultPerms = commands.defaultCommandPermissions

    fun MutableMap<Snowflake, RegisteredCommand>.collectCommands(command: ApplicationCommand) {
        val key = command.toCommandKey()
        val factory = commandCache.getValue(key)
        val groups = commandGroupCache[key]?.toPersistentHashMap() ?: persistentHashMapOf()
        val subCommands = subCommandCache[key]?.toPersistentHashMap() ?: persistentHashMapOf()
        put(command.id, RegisteredCommand(factory, groups, subCommands))
    }

    return buildMap {
        kord.createGlobalApplicationCommands {
            for (wrapper in globalCommandWrappers) {
                logger.info { "Registering global command: ${wrapper.type}" }
                when (val command = wrapper.instance as GlobalTopLevelCommand) {
                    is GlobalCommand -> input(command.defaultCommandName, command.defaultComponentDescription) {
                        applyNsfw(command)
                        applyPermissions(defaultPerms, command)
                        buildCommand(wrapper)
                    }
                    is GlobalMessageCommand -> message(command.defaultCommandName) {
                        applyNsfw(command)
                        applyPermissions(defaultPerms, command)
                    }
                    is GlobalUserCommand -> user(command.defaultCommandName) {
                        applyNsfw(command)
                        applyPermissions(defaultPerms, command)
                    }
                }
            }
        }.collect(::collectCommands)

        for ((guildId, commandData) in guildCommandWrappers) {
            val (_, wrappers) = commandData
            kord.createGuildApplicationCommands(guildId) {
                for (wrapper in wrappers) {
                    logger.info { "Registering guild command: ${wrapper.type} @$guildId" }
                    when (val command = wrapper.instance as GuildTopLevelCommand) {
                        is GuildCommand -> input(command.defaultCommandName, command.defaultComponentDescription) {
                            applyNsfw(command)
                            applyPermissions(defaultPerms, command)
                            buildCommand(wrapper)
                        }
                        is GuildMessageCommand -> message(command.defaultCommandName) {
                            applyNsfw(command)
                            applyPermissions(defaultPerms, command)
                        }
                        is GuildUserCommand -> user(command.defaultCommandName) {
                            applyNsfw(command)
                            applyPermissions(defaultPerms, command)
                        }
                    }
                }
            }.collect(::collectCommands)
        }
    }
}

context(ApplicationCommandCreateBuilder)
private fun applyNsfw(command: Command<*>) {
    nsfw = command.isNsfw
}

private suspend fun <Cmd> GlobalApplicationCommandCreateBuilder.applyPermissions(
    default: DefaultCommandPermissions?,
    command: Cmd,
) where Cmd : GlobalTopLevelCommand {
    val permissions = command.defaultMemberPermissions ?: default?.getGlobalPermissions(command)
    defaultMemberPermissions = permissions?.defaultMemberPermissions
    dmPermission = permissions?.isAllowedInDms
}

private suspend fun <Cmd> ApplicationCommandCreateBuilder.applyPermissions(
    default: DefaultCommandPermissions?,
    command: Cmd,
) where Cmd : GuildTopLevelCommand {
    val permissions = command.defaultMemberPermissions ?: default?.getGuildPermissions(command)
    defaultMemberPermissions = permissions?.defaultMemberPermissions
}

private data class ArgumentBuildContextImpl(override val parentCommand: Command<*>) : ArgumentBuildContext

context(RootInputChatBuilder)
private fun buildCommand(wrapper: CommandWrapper) {
    val (command) = wrapper
    val fixedCommand = command.fixCommand()
    val builder = this@RootInputChatBuilder
    val buildContext = ArgumentBuildContextImpl(command)
    with(buildContext) {
        buildArguments(fixedCommand.findDirectArguments().values)
    }

    if (builder is LocalizedNameBuilder) {
        builder.nameLocalizations = command.commandName.toMutableMapOrNull()
    }

    if (command is RootCommand<*, *> && builder is LocalizedDescriptionBuilder) {
        builder.descriptionLocalizations = command.componentDescription.toMutableMapOrNull()
    }

    if (wrapper is ParentCommandWrapper) {
        for (child in wrapper.children) {
            when (child) {
                is CommandGroupWrapper -> {
                    val group = child.instance
                    group(group.defaultGroupName, group.defaultComponentDescription) {
                        nameLocalizations = group.groupName.toMutableMapOrNull()
                        descriptionLocalizations = group.componentDescription.toMutableMapOrNull()
                        for (subCommand in child.subCommands) {
                            subCommand(subCommand.defaultCommandName, subCommand.defaultComponentDescription) {
                                with(buildContext) {
                                    buildSubCommand(subCommand)
                                }
                            }
                        }
                    }
                }
                is SubCommandWrapper -> {
                    val subCommand = child.instance
                    subCommand(subCommand.defaultCommandName, subCommand.defaultComponentDescription) {
                        with(buildContext) {
                            buildSubCommand(subCommand)
                        }
                    }
                }
            }
        }
    }
}

context(ArgumentBuildContext, KordSubCommandBuilder)
private fun buildSubCommand(subCommand: SubCommand<*, *>) {
    buildArguments(subCommand.findDirectArguments().values)
}

context(ArgumentBuildContext, KordBaseInputChatBuilder)
private fun buildArguments(arguments: Iterable<Argument<*, *, *>>) {
    for (argument in arguments) {
        argument.buildArgument(isRequired = true)
    }
}

private suspend fun List<CommandWrapper>.mergeGuildCommands(
    kord: Kord,
): Map<Snowflake, Pair<Guild, List<CommandWrapper>>> =
    buildMap<_, Pair<Guild, MutableList<CommandWrapper>>> {
        for (wrapper in this@mergeGuildCommands) {
            val command = wrapper.instance as GuildTopLevelCommand
            val id = command.commandGuildId
            val guild = try {
                kord.getGuild(id)
            } catch (e: EntityNotFoundException) {
                logger.error {
                    "Bot is not in guild with id $id, but it still attempted to register guild command ${command::class}"
                }
                continue
            }
            getOrPut(id) { guild to mutableListOf() }.second.add(wrapper)
        }
    }

context(Kommando)
private fun createWrappers(
    di: DirectDI,
    factories: List<CommandFactory<*>>,
): List<CommandWrapper> = factories.map { parent ->
    val paths = commands.argumentCache.pathStack
    logger.info { "Building command: ${parent.type}" }
    when (parent) {
        is RootCommandFactory -> {
            val instance = parent.create(di)
            paths.addFirst(instance.componentPath)
            ParentCommandWrapper(
                instance = instance,
                factory = parent,
                children = parent.children.map { child ->
                    when (child) {
                        is CommandGroupFactory -> {
                            val group = child.create(di).fix()
                            group.initParentCommand(instance)
                            paths.addFirst(paths.first() / group.componentPath)
                            CommandGroupWrapper(
                                instance = group,
                                subCommands = child
                                    .providers
                                    .map { with(di) { it.get() } }
                                    .onEach { it.fixSubCommand().initParentComponent(group) },
                                subCommandFactories = child.providers.map { SubCommandFactory(it) },
                                factory = child,
                            ).also {
                                paths.removeFirst()
                            }
                        }
                        is SubCommandFactory -> {
                            val subCommand = child.create(di)
                            subCommand.fixSubCommand().initParentComponent(instance)
                            SubCommandWrapper(
                                instance = subCommand,
                                factory = child,
                            )
                        }
                    }
                },
            ).also {
                paths.removeFirst()
            }
        }
        is SingleCommandFactory -> SingleCommandWrapper(parent.create(di), parent)
    }
}

private fun unknownCommandType(commands: List<CommandWrapper>): String {
    val unknownCommands = commands.map { it.instance }
        .filter { it !is GlobalCommandType && it !is GuildCommandType }
    return "Encountered command instances not associated with global nor guild: $unknownCommands"
}

private sealed interface CommandWrapper {
    val instance: TopLevelCommand<*, *>
    val factory: CommandFactory<*>

    operator fun component1(): TopLevelCommand<*, *>

    operator fun component2(): CommandFactory<*>
}

private data class SingleCommandWrapper(
    override val instance: TopLevelCommand<*, *>,
    override val factory: SingleCommandFactory,
) : CommandWrapper

private data class ParentCommandWrapper(
    override val instance: TopLevelCommand<*, *>,
    override val factory: RootCommandFactory,
    val children: List<CommandChildWrapper>,
) : CommandWrapper

private val CommandWrapper.type: KType
    get() = factory.type

private sealed interface CommandChildWrapper

private data class SubCommandWrapper(
    val instance: SubCommand<*, *>,
    val factory: SubCommandFactory,
) : CommandChildWrapper

private data class CommandGroupWrapper(
    val instance: CommandGroup<*>,
    val subCommands: List<SubCommand<*, *>>,
    val subCommandFactories: List<SubCommandFactory>,
    val factory: CommandGroupFactory,
) : CommandChildWrapper