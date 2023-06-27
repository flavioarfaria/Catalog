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

import com.flaviofaria.catalog.gradle.codegen.writer.ColorCatalogWriter
import com.flaviofaria.catalog.gradle.codegen.writer.DimenCatalogWriter
import com.flaviofaria.catalog.gradle.codegen.writer.DrawableCatalogWriter
import com.flaviofaria.catalog.gradle.codegen.writer.StringArrayCatalogWriter
import com.flaviofaria.catalog.gradle.codegen.writer.WithArgsCatalogWriter
import java.io.File

class Codegen(
  private val valueResourceParser: ValueResourceParser,
  private val drawableResourceParser: DrawableResourceParser,
  packageName: String,
  private val generateResourcesExtensions: Boolean,
  private val generateComposeExtensions: Boolean,
  generateComposeAnimatedVectorExtensions: Boolean,
) {

  private val resourceReducer = ResourceReducer()

  private val resourceWriterRegistry = mapOf(
    ResourceEntry.XmlItem.WithArgs.String::class to WithArgsCatalogWriter(
      packageName = packageName,
      resourceType = ResourceType.String,
    ),
    ResourceEntry.XmlItem.WithArgs.Plural::class to WithArgsCatalogWriter(
      packageName = packageName,
      resourceType = ResourceType.Plural,
    ),
    ResourceEntry.XmlItem.StringArray::class to StringArrayCatalogWriter(
      packageName = packageName,
    ),
    ResourceEntry.XmlItem.Color::class to ColorCatalogWriter(
      packageName = packageName,
    ),
    ResourceEntry.XmlItem.Dimen::class to DimenCatalogWriter(
      packageName = packageName,
    ),
    ResourceEntry.Drawable::class to DrawableCatalogWriter(
      packageName = packageName,
      generateComposeAnimatedVectorExtensions = generateComposeAnimatedVectorExtensions,
    ),
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
      }.filterNot {
        it.isDirectory
      }
      .flatMap { file ->
        file.toResourceEntries()
      }
      .distinctBy { resourceEntry -> // group by name to eliminate alternative resources
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
        @Suppress("UNCHECKED_CAST")
        resourceWriterRegistry[type]?.write(
          resources as List<Nothing>,
          sourceSetName,
          outputDir,
          generateResourcesExtensions,
          generateComposeExtensions,
        ) ?: error("Could not find resource writer for type $type")
      }
  }

  private fun File.toResourceEntries(): Iterable<ResourceEntry> {
    return when {
      parentFile.name.startsWith("values") && lowercaseExtension == "xml" -> {
        valueResourceParser.parseFile(this)
      }
      parentFile.name.startsWith("drawable") -> {
        toDrawableResourceEntry()?.let { listOf(it) }
      }
      else -> null
    } ?: emptyList()
  }

  private val File.lowercaseExtension: String
    get() = extension.lowercase()

  private val File.isValidBitmap: Boolean
    get() = parentFile.name.startsWith("drawable")
      && lowercaseExtension in VALID_BITMAP_EXTENSIONS

  private val File.is9PatchDrawable: Boolean
    get() = nameWithoutExtension.endsWith(".9") && lowercaseExtension == "png"

  private fun File.toDrawableResourceEntry(): ResourceEntry? {
    return when {
      is9PatchDrawable -> ResourceEntry.Drawable(
        file = this,
        name = nameWithoutExtension.substringBeforeLast(".9"),
        type = ResourceEntry.Drawable.Type.NINE_PATCH,
      )
      isValidBitmap -> ResourceEntry.Drawable(
        file = this,
        name = nameWithoutExtension,
        type = ResourceEntry.Drawable.Type.BITMAP,
      )
      lowercaseExtension == "xml" -> drawableResourceParser.parseFile(this)
      else -> null
    }
  }

  private companion object {
    val VALID_BITMAP_EXTENSIONS = setOf(
      "gif",
      "jpeg",
      "jpg",
      "png",
      "webp",
    )
  }
}
