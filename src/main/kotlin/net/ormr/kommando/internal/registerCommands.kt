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
import net.ormr.kommando.commands.*
import net.ormr.kommando.commands.arguments.Argument
import net.ormr.kommando.commands.factory.*
import net.ormr.kommando.commands.permissions.*
import net.ormr.kommando.localization.LocalizationResolver
import org.kodein.di.DirectDI
import org.kodein.di.direct

private val logger = InlineLogger()

context(Kommando)
        internal suspend fun registerCommands(
    factories: List<CommandFactory>,
): Map<Snowflake, RegisteredCommand> {
    val commands = createWrappers(direct, factories)
    val globalCommands = commands.filter { it.instance is GlobalTopLevelCommand }
    val guildCommands = commands.filter { it.instance is GuildTopLevelCommand }.mergeGuildCommands(kord)
    check(globalCommands.size + guildCommands.size == factories.size) { unknownCommandType(commands) }
    val commandsCache = commands.associateBy({ CommandKey(it.instance) }, CommandWrapper::factory)
    val commandGroupsCache = commands.filterIsInstance<ParentCommandWrapper>()
        .associateBy(
            { CommandKey(it.instance) },
            { p ->
                p.children.filterIsInstance<CommandGroupWrapper>()
                    .associateBy(
                        { it.instance.defaultName },
                        {
                            RegisteredGroup(
                                it.factory,
                                (it.subCommands.asSequence() zip it.subCommandFactories.asSequence())
                                    .associate { (command, factory) -> command.defaultName to factory },
                            )
                        },
                    )
            }
        )
    val subCommandsCache = commands.filterIsInstance<ParentCommandWrapper>()
        .associateBy(
            { CommandKey(it.instance) },
            { p ->
                p.children.filterIsInstance<SubCommandWrapper>()
                    .associateBy({ it.instance.defaultName }, { it.factory })
            }
        )
    val globalPerms = defaultCommandPermissions?.globalPermissionsFactory
    val guildPerms = defaultCommandPermissions?.guildPermissionsFactory

    fun MutableMap<Snowflake, RegisteredCommand>.collectCommands(command: ApplicationCommand) {
        val key = CommandKey(command)
        val factory = commandsCache.getValue(key)
        val groups = commandGroupsCache[key] ?: emptyMap()
        val subCommands = subCommandsCache[key] ?: emptyMap()
        put(command.id, RegisteredCommand(factory, groups, subCommands))
    }

    return buildMap {
        kord.createGlobalApplicationCommands {
            for (wrapper in globalCommands) {
                when (val command = wrapper.instance as GlobalTopLevelCommand) {
                    is GlobalCommand -> input(command.defaultName, command.defaultDescription) {
                        applyPermissions(globalPerms, command)
                        buildCommand(wrapper, localization.defaultResolver)
                    }
                    is GlobalMessageCommand -> message(command.defaultName) {
                        applyPermissions(globalPerms, command)
                    }
                    is GlobalUserCommand -> user(command.defaultName) {
                        applyPermissions(globalPerms, command)
                    }
                }
            }
        }.collect(::collectCommands)

        for ((guildId, commandData) in guildCommands) {
            val (_, wrappers) = commandData
            kord.createGuildApplicationCommands(guildId) {
                for (wrapper in wrappers) {
                    when (val command = wrapper.instance as GuildTopLevelCommand) {
                        is GuildCommand -> input(command.defaultName, command.defaultDescription) {
                            applyPermissions(guildPerms, command)
                            buildCommand(wrapper, localization.defaultResolver)
                        }
                        is GuildMessageCommand -> message(command.defaultName) {
                            applyPermissions(guildPerms, command)
                        }
                        is GuildUserCommand -> user(command.defaultName) {
                            applyPermissions(guildPerms, command)
                        }
                    }
                }
            }.collect(::collectCommands)
        }
    }
}

private suspend fun <C> GlobalApplicationCommandCreateBuilder.applyPermissions(
    defaultFactory: GlobalCommandPermissionsFactory?,
    command: C,
) where C : TopLevelCommand<*, GlobalCommandPermissions>,
        C : GlobalCentricCommand {
    val permissions = command.defaultMemberPermissions ?: defaultFactory?.invoke(command)
    defaultMemberPermissions = permissions?.defaultMemberPermissions
    dmPermission = permissions?.isAllowedInDms
}

private suspend fun <C> ApplicationCommandCreateBuilder.applyPermissions(
    defaultFactory: GuildCommandPermissionsFactory?,
    command: C,
) where C : TopLevelCommand<*, GuildCommandPermissions>,
        C : GuildCentricCommand {
    val permissions = command.defaultMemberPermissions ?: defaultFactory?.invoke(command)
    defaultMemberPermissions = permissions?.defaultMemberPermissions
}

private fun RootInputChatBuilder.buildCommand(
    wrapper: CommandWrapper,
    resolver: LocalizationResolver,
) {
    val (command) = wrapper
    buildArguments(command.arguments.values, resolver)

    if (this is LocalizedNameBuilder) {
        nameLocalizations = command.name.toMutableMap()
    }

    if (command is SuperCommand<*, *> && this is LocalizedDescriptionBuilder) {
        descriptionLocalizations = command.description.toMutableMap()
    }

    fun SubCommandBuilder.buildSubCommand(subCommand: SubCommand<*, *>) {
        buildArguments(subCommand.arguments.values, resolver)
    }

    if (wrapper is ParentCommandWrapper) {
        for (child in wrapper.children) {
            when (child) {
                is CommandGroupWrapper -> {
                    val group = child.instance
                    group(group.defaultName, group.defaultDescription) {
                        nameLocalizations = group.name.toMutableMap()
                        descriptionLocalizations = group.description.toMutableMap()
                        for (subCommand in child.subCommands) {
                            subCommand(subCommand.defaultName, subCommand.defaultDescription) {
                                buildSubCommand(subCommand)
                            }
                        }
                    }
                }
                is SubCommandWrapper -> {
                    val subCommand = child.instance
                    subCommand(subCommand.defaultName, subCommand.defaultDescription) {
                        buildSubCommand(subCommand)
                    }
                }
            }
        }
    }
}

private fun BaseInputChatBuilder.buildArguments(arguments: Iterable<Argument<*, *>>, resolver: LocalizationResolver) {
    for (argument in arguments) {
        with(argument) {
            buildArgument(resolver, isRequired = true)
        }
    }
}

private suspend fun List<CommandWrapper>.mergeGuildCommands(
    kord: Kord,
): Map<Snowflake, Pair<Guild, List<CommandWrapper>>> =
    buildMap<_, Pair<Guild, MutableList<CommandWrapper>>> {
        for (wrapper in this@mergeGuildCommands) {
            val command = wrapper.instance as GuildTopLevelCommand
            val id = command.guildId
            val guild = try {
                kord.getGuild(id)
            } catch (e: EntityNotFoundException) {
                logger.error(e) {
                    "Bot is not in guild with id $id, but it still attempted to register guild command ${command::class.qualifiedName ?: command::class}"
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
            val instance = parent(di)
            ParentCommandWrapper(
                instance = instance,
                factory = parent,
                children = parent.children.map { child ->
                    when (child) {
                        is CommandGroupFactory -> {
                            val group = child(di)
                            group.setParent(instance)
                            CommandGroupWrapper(
                                instance = group,
                                subCommands = child.factories.map { it(di).also { sub -> sub.setParent(instance) } },
                                subCommandFactories = child.factories.map { SubCommandFactory(it) },
                                factory = child,
                            )
                        }
                        is SubCommandFactory -> {
                            val subCommand = child(di)
                            subCommand.setParent(instance)
                            SubCommandWrapper(
                                instance = subCommand,
                                factory = child
                            )
                        }
                    }
                },
            )
        }
        is SingleCommandFactory -> SingleCommandWrapper(parent(di), parent)
    }
}

private fun unknownCommandType(commands: List<CommandWrapper>): String {
    val unknownCommands = commands.map { it.instance }
        .filter { it !is GlobalCentricCommand && it !is GuildCentricCommand }
    return "Encountered command instances not associated with global nor guild: $unknownCommands"
}

private sealed interface CommandWrapper {
    val instance: TopLevelCommand<*, *>
    val factory: CommandFactory

    operator fun component1(): TopLevelCommand<*, *>

    operator fun component2(): CommandFactory
}

private data class SingleCommandWrapper(
    override val instance: TopLevelCommand<*, *>,
    override val factory: SingleCommandFactory,
) : CommandWrapper

private data class ParentCommandWrapper(
    override val instance: TopLevelCommand<*, *>,
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