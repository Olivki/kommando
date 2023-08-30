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

private const val MAX_ROWS = 5

public class ComponentGroupFactory(
    private val groupId: String,
    private val factories: List<ComponentFactory>,
    private val interactionIdProvider: ComponentInteractionIdProvider,
) {
    private fun ComponentFactory.create(): Component = when (this) {
        is ComponentFactory.Row -> create()
        is ComponentFactory.Visible<*> -> create()
        is ComponentFactory.Executable<*> -> create(
            interactionId = interactionIdProvider.get(groupId = groupId, componentId = id),
        )
    }

    private fun createRows(): ComponentRows {
        // TODO: be more efficient and use 2D Array instead of 2D List?
        val rows = MutableList(MAX_ROWS) { ArrayList<VisibleComponent<*>>(ComponentWidth.MAX.asInt()) }
        var currentWidth = 0
        var currentRowIndex = 0
        factories.forEach { factory ->
            val component = factory.create()
            currentWidth += component.width.asInt()
            if (currentWidth > ComponentWidth.MAX) {
                currentWidth = component.width.asInt()
                currentRowIndex++
            }
            if (currentRowIndex >= MAX_ROWS) {
                throw IllegalArgumentException("Max rows ($MAX_ROWS) exceeded by component: $component")
            }
            val currentRow = rows[currentRowIndex]
            when (component) {
                is VisibleComponent<*> -> currentRow.add(component)
                is ComponentRow -> currentRow.addAll(component.components)
            }
        }
        return rows.filter { it.isNotEmpty() }.asComponentRows()
    }

    public fun create(): ComponentGroup = ComponentGroup(
        id = groupId,
        rows = createRows(),
    )
}