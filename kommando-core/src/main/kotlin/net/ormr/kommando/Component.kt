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

package net.ormr.kommando

import net.ormr.kommando.localization.Message
import org.kodein.di.DI
import org.kodein.di.instance

public interface Component : KommandoDI {
    public val kommando: Kommando

    /**
     * The path to this component.
     *
     * This is not guaranteed to be a "full" path, as it may be a path relative to the parent component. For "full"
     * paths, see [ComposableComponent.fullComponentPath].
     */
    public val componentPath: ComponentPath
    // TODO: make a second interface that can implement a 'fullComponentPath' property
    //       that can create a full path by combining the 'componentPath' with the parent's 'componentPath'
}

public abstract class AbstractComponent : Component {
    final override val di: DI get() = super.di

    final override val kommando: Kommando by instance()
}

// TODO: better name, 'Composable' doesn't really convey what this does differently from 'Component'
public interface ComposableComponent {
    /**
     * The full path to this component, including the parent's path.
     */
    public val fullComponentPath: ComponentPath
}

public interface DescribableComponent : Component {
    public val componentDescription: Message
}