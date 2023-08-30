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

package net.ormr.kommando.component

public class ComponentRow(public val components: List<VisibleComponent<*>>) : Component {
    init {
        checkTotalWidth()
    }

    override val width: ComponentWidth
        get() = ComponentWidth.MAX

    private fun checkTotalWidth() {
        var totalWidth = 0
        components.forEach {
            totalWidth += it.width.asInt()
            if (totalWidth > width) {
                val amount = totalWidth - width.asInt()
                throw IllegalArgumentException("Max width ($width) of row exceeded by $amount with component: $it")
            }
        }
    }
}