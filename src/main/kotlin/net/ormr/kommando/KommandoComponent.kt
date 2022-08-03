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

package net.ormr.kommando

import net.ormr.kommando.localization.LOCALIZATION_PATH_SEPARATOR
import net.ormr.kommando.localization.Localization
import net.ormr.kommando.localization.LocalizedString
import org.kodein.di.DI
import org.kodein.di.instance

public abstract class KommandoComponent : KommandoDI {
    final override val di: DI get() = super.di

    public val kommando: Kommando by instance()

    public val localization: Localization get() = kommando.localization

    public abstract val componentPath: KommandoComponentPath
}

public class KommandoComponentPath(paths: Iterable<String>) {
    init {
        require(paths.none { LOCALIZATION_PATH_SEPARATOR in it }) {
            "Component paths may not contain '$LOCALIZATION_PATH_SEPARATOR'"
        }
    }

    public val paths: List<String> = paths.toList()

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is KommandoComponentPath -> false
        paths != other.paths -> false
        else -> true
    }

    override fun hashCode(): Int = paths.hashCode()

    public fun format(): String = paths.joinToString(separator = LOCALIZATION_PATH_SEPARATOR.toString())

    public fun format(extra: String): String = "${format()}/$extra"

    override fun toString(): String = format()
}

public fun KommandoComponentPath.extend(vararg paths: String): KommandoComponentPath =
    KommandoComponentPath(this.paths + paths)

context(KommandoComponent)
        public fun componentPath(vararg paths: String): KommandoComponentPath =
    KommandoComponentPath(paths.asIterable())

context(KommandoComponent)
        public fun Localization.resolve(extra: String): LocalizedString = resolve(this@KommandoComponent, extra)

context(KommandoComponent)
        public fun Localization.resolveOrNull(extra: String): LocalizedString? =
    resolveOrNull(this@KommandoComponent, extra)