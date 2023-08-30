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

@file:Suppress("NOTHING_TO_INLINE")

package net.ormr.kommando.component

@JvmInline
public value class ComponentRows internal constructor(
    @PublishedApi
    internal val rows: List<List<VisibleComponent<*>>>,
) {
    public operator fun get(firstIndex: Int, secondIndex: Int): VisibleComponent<*> =
        rows[firstIndex][secondIndex]
}

public fun ComponentRows.isEmpty(): Boolean = rows.isEmpty() || rows.all { it.isEmpty() }

public inline fun ComponentRows.isNotEmpty(): Boolean = !isEmpty()

public inline fun ComponentRows.flatForEach(action: (row: Int, component: VisibleComponent<*>) -> Unit) {
    rows.forEachIndexed { i, row ->
        row.forEach { action(i, it) }
    }
}

public inline fun ComponentRows.forEach(action: (row: List<VisibleComponent<*>>) -> Unit) {
    for (row in rows) action(row)
}

public inline fun ComponentRows.map(
    transformer: (row: Int, component: VisibleComponent<*>) -> VisibleComponent<*>,
): ComponentRows = rows.mapIndexed { i, row ->
    row.map { transformer(i, it) }
}.asComponentRows()

@PublishedApi
internal fun List<List<VisibleComponent<*>>>.asComponentRows(): ComponentRows =
    ComponentRows(this)