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
package com.flaviofaria.catalog.gradle.codegen.writer

import com.flaviofaria.catalog.gradle.codegen.ResourceEntry
import com.google.common.truth.Truth.assertThat
import java.io.File
import java.nio.charset.Charset
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class StringArrayCatalogWriterTest {

  @get:Rule
  var folder = TemporaryFolder()

  private lateinit var codegenDestination: File

  private val resources = listOf(
    ResourceEntry.XmlItem.StringArray(
      file = File("."),
      name = "string_array_1",
      docs = "String array 1 docs",
    ),
    ResourceEntry.XmlItem.StringArray(
      file = File("."),
      name = "string_array_2",
      docs = null,
    ),
  )

  private lateinit var codegenFile: File
  private val writer = StringArrayCatalogWriter(packageName = "com.example")

  @Before
  fun setUp() {
    codegenDestination = folder.newFolder()
    codegenFile = File("${codegenDestination.absolutePath}/com/example/StringArrays.kt")
  }

  @Test
  fun `GIVEN generateResourcesExtensions and generateComposeExtensions disabled THEN generate property extensions only`() {
    writer.write(
      resources,
      sourceSetName = "main",
      codegenDestination = codegenDestination,
      generateResourcesExtensions = false,
      generateComposeExtensions = false,
    )
    assertThat(
      codegenFile.readBytes().toString(Charset.defaultCharset()),
    ).isEqualTo(
      """
      |// Auto-generated by Catalog. DO NOT EDIT.
      |// https://github.com/flavioarfaria/Catalog
      |@file:JvmName("StringArraysMain")
      |@file:Suppress("NOTHING_TO_INLINE")
      |
      |package com.example
      |
      |import androidx.`annotation`.ArrayRes
      |import com.flaviofaria.catalog.runtime.resources.StringArrays
      |import kotlin.Int
      |import kotlin.Suppress
      |import kotlin.jvm.JvmName
      |
      |/**
      | * String array 1 docs
      | */
      |@get:ArrayRes
      |public inline val StringArrays.stringArray1: Int
      |  get() = R.array.string_array_1
      |
      |@get:ArrayRes
      |public inline val StringArrays.stringArray2: Int
      |  get() = R.array.string_array_2
      |""".trimMargin(),
    )
  }

  @Test
  fun `GIVEN generateResourcesExtensions enabled and generateComposeExtensions disabled THEN generate property and resources extensions only`() {
    writer.write(
      resources,
      sourceSetName = "main",
      codegenDestination = codegenDestination,
      generateResourcesExtensions = true,
      generateComposeExtensions = false,
    )
    assertThat(
      codegenFile.readBytes().toString(Charset.defaultCharset()),
    ).isEqualTo(
      """
      |// Auto-generated by Catalog. DO NOT EDIT.
      |// https://github.com/flavioarfaria/Catalog
      |@file:JvmName("StringArraysMain")
      |@file:Suppress("NOTHING_TO_INLINE")
      |
      |package com.example
      |
      |import android.content.Context
      |import androidx.`annotation`.ArrayRes
      |import androidx.fragment.app.Fragment
      |import com.flaviofaria.catalog.runtime.resources.StringArrays
      |import kotlin.Array
      |import kotlin.Int
      |import kotlin.String
      |import kotlin.Suppress
      |import kotlin.jvm.JvmName
      |
      |/**
      | * String array 1 docs
      | */
      |@get:ArrayRes
      |public inline val StringArrays.stringArray1: Int
      |  get() = R.array.string_array_1
      |
      |/**
      | * String array 1 docs
      | */
      |context(Context)
      |public inline fun StringArrays.stringArray1(): Array<String> =
      |    resources.getStringArray(R.array.string_array_1)
      |
      |/**
      | * String array 1 docs
      | */
      |context(Fragment)
      |public inline fun StringArrays.stringArray1(): Array<String> =
      |    resources.getStringArray(R.array.string_array_1)
      |
      |@get:ArrayRes
      |public inline val StringArrays.stringArray2: Int
      |  get() = R.array.string_array_2
      |
      |context(Context)
      |public inline fun StringArrays.stringArray2(): Array<String> =
      |    resources.getStringArray(R.array.string_array_2)
      |
      |context(Fragment)
      |public inline fun StringArrays.stringArray2(): Array<String> =
      |    resources.getStringArray(R.array.string_array_2)
      |""".trimMargin(),
    )
  }

  @Test
  fun `GIVEN generateResourcesExtensions disabled and generateComposeExtensions enabled THEN generate property and compose extensions only`() {
    writer.write(
      resources,
      sourceSetName = "main",
      codegenDestination = codegenDestination,
      generateResourcesExtensions = false,
      generateComposeExtensions = true,
    )
    assertThat(
      codegenFile.readBytes().toString(Charset.defaultCharset()),
    ).isEqualTo(
      """
      |// Auto-generated by Catalog. DO NOT EDIT.
      |// https://github.com/flavioarfaria/Catalog
      |@file:JvmName("StringArraysMain")
      |@file:Suppress("NOTHING_TO_INLINE")
      |
      |package com.example
      |
      |import androidx.`annotation`.ArrayRes
      |import androidx.compose.runtime.Composable
      |import androidx.compose.runtime.ReadOnlyComposable
      |import androidx.compose.ui.res.stringArrayResource
      |import com.flaviofaria.catalog.runtime.compose.StringArrays
      |import kotlin.Array
      |import kotlin.Int
      |import kotlin.String
      |import kotlin.Suppress
      |import kotlin.jvm.JvmName
      |
      |/**
      | * String array 1 docs
      | */
      |@get:ArrayRes
      |public inline val StringArrays.stringArray1: Int
      |  get() = R.array.string_array_1
      |
      |/**
      | * String array 1 docs
      | */
      |@Composable
      |@ReadOnlyComposable
      |public inline fun StringArrays.stringArray1(): Array<String> =
      |    stringArrayResource(R.array.string_array_1)
      |
      |@get:ArrayRes
      |public inline val StringArrays.stringArray2: Int
      |  get() = R.array.string_array_2
      |
      |@Composable
      |@ReadOnlyComposable
      |public inline fun StringArrays.stringArray2(): Array<String> =
      |    stringArrayResource(R.array.string_array_2)
      |""".trimMargin(),
    )
  }

  @Test
  fun `GIVEN generateResources and generateComposeExtensions enabled THEN generate property, resources and compose extensions`() {
    writer.write(
      resources,
      sourceSetName = "main",
      codegenDestination = codegenDestination,
      generateResourcesExtensions = true,
      generateComposeExtensions = true,
    )
    assertThat(
      codegenFile.readBytes().toString(Charset.defaultCharset()),
    ).isEqualTo(
      """
      |// Auto-generated by Catalog. DO NOT EDIT.
      |// https://github.com/flavioarfaria/Catalog
      |@file:JvmName("StringArraysMain")
      |@file:Suppress("NOTHING_TO_INLINE")
      |
      |package com.example
      |
      |import android.content.Context
      |import androidx.`annotation`.ArrayRes
      |import androidx.compose.runtime.Composable
      |import androidx.compose.runtime.ReadOnlyComposable
      |import androidx.compose.ui.res.stringArrayResource
      |import androidx.fragment.app.Fragment
      |import com.flaviofaria.catalog.runtime.compose.StringArrays
      |import kotlin.Array
      |import kotlin.Int
      |import kotlin.String
      |import kotlin.Suppress
      |import kotlin.jvm.JvmName
      |
      |/**
      | * String array 1 docs
      | */
      |@get:ArrayRes
      |public inline val StringArrays.stringArray1: Int
      |  get() = R.array.string_array_1
      |
      |/**
      | * String array 1 docs
      | */
      |context(Context)
      |public inline fun com.flaviofaria.catalog.runtime.resources.StringArrays.stringArray1():
      |    Array<String> = resources.getStringArray(R.array.string_array_1)
      |
      |/**
      | * String array 1 docs
      | */
      |context(Fragment)
      |public inline fun com.flaviofaria.catalog.runtime.resources.StringArrays.stringArray1():
      |    Array<String> = resources.getStringArray(R.array.string_array_1)
      |
      |/**
      | * String array 1 docs
      | */
      |@Composable
      |@ReadOnlyComposable
      |public inline fun StringArrays.stringArray1(): Array<String> =
      |    stringArrayResource(R.array.string_array_1)
      |
      |@get:ArrayRes
      |public inline val StringArrays.stringArray2: Int
      |  get() = R.array.string_array_2
      |
      |context(Context)
      |public inline fun com.flaviofaria.catalog.runtime.resources.StringArrays.stringArray2():
      |    Array<String> = resources.getStringArray(R.array.string_array_2)
      |
      |context(Fragment)
      |public inline fun com.flaviofaria.catalog.runtime.resources.StringArrays.stringArray2():
      |    Array<String> = resources.getStringArray(R.array.string_array_2)
      |
      |@Composable
      |@ReadOnlyComposable
      |public inline fun StringArrays.stringArray2(): Array<String> =
      |    stringArrayResource(R.array.string_array_2)
      |""".trimMargin(),
    )
  }
}
