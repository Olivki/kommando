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

package net.ormr.kommando.localization

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import net.ormr.kommando.ElementPath
import net.ormr.kommando.KommandoDsl

private class YamlMessageBundle(private val resource: LocalizedResource) : MessageBundle {
    private val helper = JacksonMessageBundleHelper(mapper, resource)

    override fun getMessageOrNull(path: ElementPath, key: String): Message? =
        helper.findMessage(resource.defaultLocale, path, key)

    override fun isEmpty(): Boolean = resource.isEmpty()

    private companion object {
        private val mapper = YAMLMapper()
    }
}

@KommandoDsl
public fun MessageBundleBuilder.yaml(resource: LocalizedResource) {
    require(resource.defaultLocale == defaultLocale) { "Resource default locale (${resource.defaultLocale}) != builder default locale ($defaultLocale)" }
    +YamlMessageBundle(resource)
}

@KommandoDsl
public fun MessageBundleBuilder.yamlFromClassPath(
    path: String,
    extension: String = "yaml",
    clz: Class<*> = javaClass,
) {
    yaml(LocalizedResource.fromClass(clz, path, extension, defaultLocale))
}