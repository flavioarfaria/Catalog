/*
 * Copyright (C) 2022 Flavio Faria
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flaviofaria.catalog.gradle.codegen

import com.flaviofaria.catalog.gradle.codegen.writer.StringArrayCatalogWriter
import com.flaviofaria.catalog.gradle.codegen.writer.WithArgsCatalogWriter
import java.io.File

class Codegen(
  private val xmlResourceParser: XmlResourceParser = XmlResourceParser(),
  packageName: String,
  private val generateResourcesExtensions: Boolean,
  private val generateComposeExtensions: Boolean,
) {

  private val resourceReducer = ResourceReducer()
  private val stringCatalogWriter = WithArgsCatalogWriter(
    packageName = packageName,
    resourceType = ResourceType.String,
  )
  private val pluralCatalogWriter = WithArgsCatalogWriter(
    packageName = packageName,
    resourceType = ResourceType.Plural,
  )
  private val stringArrayCatalogWriter = StringArrayCatalogWriter(
    packageName = packageName,
  )

  fun start(
    sourceSetName: String,
    sourceSetDirs: Set<File>,
    outputDir: File,
  ) {
    sourceSetDirs
      .asSequence()
      .flatMap { sourceSetDir ->
        sourceSetDir.walk()
      }
      .filter { file ->
        file.parentFile.name.startsWith("values") && file.extension == "xml"
      }
      .flatMap { file ->
        xmlResourceParser.parseFile(file)
      }
      .distinctBy { resourceEntry -> // groups resources by name to eliminate duplicates by priority
        resourceEntry.name
      }.toList().let { resourceEntries ->
        generateExtensionFilesForSourceSet(
          sourceSetName,
          resourceEntries.toList(),
          outputDir,
        )
      }
  }

  private fun generateExtensionFilesForSourceSet(
    sourceSetName: String,
    resourceEntries: List<ResourceEntry>,
    outputDir: File,
  ) {
    outputDir.mkdirs()
    resourceEntries.groupBy { it::class } // groups by type
      .map { it.value }
      .flatMap { groupedByType ->
        // groups by resource name
        groupedByType.groupBy { it.name }.map { it.value }
      }
      .map(resourceReducer::reduce)
      .groupBy { it::class } // groups them back by type to write Kotlin files
      .forEach { (type, resources) ->
        when (type) {
          ResourceEntry.WithArgs.String::class -> {
            @Suppress("UNCHECKED_CAST")
            stringCatalogWriter.write(
              resources as List<ResourceEntry.WithArgs.String>, // TODO unchecked cast
              sourceSetName,
              outputDir,
              generateResourcesExtensions,
              generateComposeExtensions,
            )
          }
          ResourceEntry.WithArgs.Plural::class -> {
            @Suppress("UNCHECKED_CAST")
            pluralCatalogWriter.write(
              resources as List<ResourceEntry.WithArgs.Plural>, // TODO
              sourceSetName,
              outputDir,
              generateResourcesExtensions,
              generateComposeExtensions,
            )
          }
          ResourceEntry.StringArray::class -> {
            @Suppress("UNCHECKED_CAST")
            stringArrayCatalogWriter.write(
              resources as List<ResourceEntry.StringArray>, // TODO
              sourceSetName,
              outputDir,
              generateResourcesExtensions,
              generateComposeExtensions,
            )
          }
        }
      }
  }
}
