/*
 * Copyright 2022 Oliver Berg
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

package net.ormr.kommando.commands.arguments

import dev.kord.rest.builder.interaction.BaseInputChatBuilder
import net.ormr.kommando.localization.LocalizationResolver

public sealed class ArgumentWithChoice<Value : Any, ArgValue : Any, ArgType : ChoiceArgumentType<ArgValue>> :
    Argument<Value, ArgValue>() {
    abstract override val type: ArgType

    public abstract fun convertChoiceValue(value: Value): ArgValue

    public abstract fun BaseInputChatBuilder.buildArgumentWithChoices(
        resolver: LocalizationResolver,
        choices: List<ArgumentChoice<ArgValue>>,
        isRequired: Boolean,
    )

    public abstract override fun toString(): String
}
