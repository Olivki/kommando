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

package net.ormr.kommando.command.argument

import dev.kord.core.entity.Entity
import dev.kord.core.entity.interaction.InteractionCommand
import net.ormr.kommando.kord.AttachmentEntity
import net.ormr.kommando.kord.ResolvedChannelEntity
import net.ormr.kommando.kord.RoleEntity
import kotlin.Boolean as KBoolean
import kotlin.String as KString

public sealed interface ChoiceArgumentType<Value> : ArgumentType<Value>
        where Value : Any

public sealed interface ArgumentType<Value>
        where Value : Any {
    public fun getValue(source: InteractionCommand, name: KString): Value = getValueOrNull(source, name)
        ?: throw NoSuchArgumentWithTypeException(name, this)

    public fun getValueOrNull(source: InteractionCommand, name: KString): Value?

    public data object Attachment : ArgumentType<AttachmentEntity> {
        override fun getValueOrNull(source: InteractionCommand, name: KString): AttachmentEntity? =
            source.attachments[name]
    }

    public data object Boolean : ArgumentType<KBoolean> {
        override fun getValueOrNull(source: InteractionCommand, name: KString): KBoolean? = source.booleans[name]
    }

    public data object Channel : ArgumentType<ResolvedChannelEntity> {
        override fun getValueOrNull(source: InteractionCommand, name: KString): ResolvedChannelEntity? =
            source.channels[name]
    }

    public data object Number : ChoiceArgumentType<Double> {
        override fun getValueOrNull(source: InteractionCommand, name: KString): Double? = source.numbers[name]
    }

    public data object Integer : ChoiceArgumentType<Long> {
        override fun getValueOrNull(source: InteractionCommand, name: KString): Long? = source.integers[name]
    }

    public data object Mentionable : ArgumentType<Entity> {
        override fun getValueOrNull(source: InteractionCommand, name: KString): Entity? = source.mentionables[name]
    }

    public data object Role : ArgumentType<RoleEntity> {
        override fun getValueOrNull(source: InteractionCommand, name: KString): RoleEntity? = source.roles[name]
    }

    public data object String : ChoiceArgumentType<KString> {
        override fun getValueOrNull(source: InteractionCommand, name: KString): KString? = source.strings[name]
    }
}