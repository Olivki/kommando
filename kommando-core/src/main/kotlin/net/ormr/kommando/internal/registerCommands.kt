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
import net.ormr.kommando.Kommando
import net.ormr.kommando.command.*
import net.ormr.kommando.command.argument.Argument
import net.ormr.kommando.command.argument.ArgumentBuildContext
import net.ormr.kommando.command.factory.*
import net.ormr.kommando.command.permission.GlobalCommandPermissionsFactory
import net.ormr.kommando.command.permission.GuildCommandPermissionsFactory
import net.ormr.kommando.defaultComponentDescription
import net.ormr.kommando.localization.toMutableMapOrNull
import org.kodein.di.DirectDI
import org.kodein.di.direct

// Here be dragons

// TODO: verify command names?
//       https://discord.com/developers/docs/interactions/application-commands#application-command-object-application-command-naming
//       localized names follow the same rules as normal names

// TODO: verify that arguments are ordered correctly (required before optional)

private val logger = InlineLogger()

context(Kommando)
internal suspend fun registerCommands(
    factories: List<CommandFactory>,
): Map<Snowflake, RegisteredCommand> {
    val commands = createWrappers(direct, factories)
    val globalCommands = commands
        .filter { it.instance is GlobalRootCommand }
    val guildCommands = commands
        .filter { it.instance is GuildRootCommand }
        .mergeGuildCommands(kord)
    check(globalCommands.size + guildCommands.size == factories.size) { unknownCommandType(commands) }
    val commandCache = commands
        .associateByTo(hashMapOf(), { it.instance.toCommandKey() }, CommandWrapper::factory)
    val commandGroupCache = commands
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
    val subCommandCache = commands.filterIsInstance<ParentCommandWrapper>()
        .associateBy(
            { it.instance.toCommandKey() },
            { parent ->
                parent
                    .children
                    .filterIsInstance<SubCommandWrapper>()
                    .associateByTo(hashMapOf(), { it.instance.defaultCommandName }, { it.factory })
            }
        )
    val globalPerms = defaultCommandPermissions?.globalPermissionsFactory
    val guildPerms = defaultCommandPermissions?.guildPermissionsFactory

    fun MutableMap<Snowflake, RegisteredCommand>.collectCommands(command: ApplicationCommand) {
        val key = command.toCommandKey()
        val factory = commandCache.getValue(key)
        val groups = commandGroupCache[key] ?: emptyMap()
        val subCommands = subCommandCache[key] ?: emptyMap()
        put(command.id, RegisteredCommand(factory, groups, subCommands))
    }

    return buildMap {
        kord.createGlobalApplicationCommands {
            for (wrapper in globalCommands) {
                logger.info { "Registering global command ${wrapper.instance::class.qualifiedName}#${wrapper.instance.defaultCommandName}" }
                when (val command = wrapper.instance as GlobalRootCommand) {
                    is GlobalCommand -> input(command.defaultCommandName, command.defaultComponentDescription) {
                        applyPermissions(globalPerms, command)
                        buildCommand(wrapper)
                    }
                    is GlobalMessageCommand -> message(command.defaultCommandName) {
                        applyPermissions(globalPerms, command)
                    }
                    is GlobalUserCommand -> user(command.defaultCommandName) {
                        applyPermissions(globalPerms, command)
                    }
                }
            }
        }.collect(::collectCommands)

        for ((guildId, commandData) in guildCommands) {
            val (_, wrappers) = commandData
            kord.createGuildApplicationCommands(guildId) {
                for (wrapper in wrappers) {
                    logger.info { "Registering guild command ${wrapper.instance::class.qualifiedName}#${wrapper.instance.defaultCommandName} @$guildId" }
                    when (val command = wrapper.instance as GuildRootCommand) {
                        is GuildCommand -> input(command.defaultCommandName, command.defaultComponentDescription) {
                            applyPermissions(guildPerms, command)
                            buildCommand(wrapper)
                        }
                        is GuildMessageCommand -> message(command.defaultCommandName) {
                            applyPermissions(guildPerms, command)
                        }
                        is GuildUserCommand -> user(command.defaultCommandName) {
                            applyPermissions(guildPerms, command)
                        }
                    }
                }
            }.collect(::collectCommands)
        }
    }
}

private suspend fun <Cmd> GlobalApplicationCommandCreateBuilder.applyPermissions(
    defaultFactory: GlobalCommandPermissionsFactory?,
    command: Cmd,
) where Cmd : GlobalRootCommand {
    val permissions = command.defaultMemberPermissions ?: defaultFactory?.invoke(command)
    defaultMemberPermissions = permissions?.defaultMemberPermissions
    dmPermission = permissions?.isAllowedInDms
}

private suspend fun <Cmd> ApplicationCommandCreateBuilder.applyPermissions(
    defaultFactory: GuildCommandPermissionsFactory?,
    command: Cmd,
) where Cmd : GuildRootCommand {
    val permissions = command.defaultMemberPermissions ?: defaultFactory?.invoke(command)
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

    if (command is SuperCommand<*, *> && builder is LocalizedDescriptionBuilder) {
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

context(ArgumentBuildContext, SubCommandBuilder)
private fun buildSubCommand(subCommand: SubCommand<*, *>) {
    buildArguments(subCommand.findDirectArguments().values)
}

context(ArgumentBuildContext, BaseInputChatBuilder)
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
            val command = wrapper.instance as GuildRootCommand
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

private fun createWrappers(
    di: DirectDI,
    factories: List<CommandFactory>,
): List<CommandWrapper> = factories.map { parent ->
    when (parent) {
        is ParentCommandFactory -> {
            val instance = parent.create(di)
            ParentCommandWrapper(
                instance = instance,
                factory = parent,
                children = parent.children.map { child ->
                    when (child) {
                        is CommandGroupFactory -> {
                            val group = child.create(di)
                            group.initSuperCommand(instance)
                            CommandGroupWrapper(
                                instance = group,
                                subCommands = child
                                    .factories
                                    .map { it(di) }
                                    .onEach { it.initSuperCommand(instance) },
                                subCommandFactories = child.factories.map { SubCommandFactory(it) },
                                factory = child,
                            )
                        }
                        is SubCommandFactory -> {
                            val subCommand = child.create(di)
                            subCommand.initSuperCommand(instance)
                            SubCommandWrapper(
                                instance = subCommand,
                                factory = child,
                            )
                        }
                    }
                },
            )
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
    val instance: RootCommand<*, *>
    val factory: CommandFactory

    operator fun component1(): RootCommand<*, *>

    operator fun component2(): CommandFactory
}

private data class SingleCommandWrapper(
    override val instance: RootCommand<*, *>,
    override val factory: SingleCommandFactory,
) : CommandWrapper

private data class ParentCommandWrapper(
    override val instance: RootCommand<*, *>,
    override val factory: ParentCommandFactory,
    val children: List<CommandChildWrapper>,
) : CommandWrapper

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