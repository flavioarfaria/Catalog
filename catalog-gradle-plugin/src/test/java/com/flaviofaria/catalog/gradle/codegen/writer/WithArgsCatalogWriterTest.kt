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
import com.flaviofaria.catalog.gradle.codegen.ResourceType
import com.flaviofaria.catalog.gradle.codegen.StringArg
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.charset.Charset

class WithArgsCatalogWriterTest {

  @get:Rule
  var folder = TemporaryFolder()

  private lateinit var codegenDestination: File

  private val stringResources = listOf(
    ResourceEntry.WithArgs.String(
      file = File("."),
      name = "string_1",
      docs = "String 1 docs",
      args = emptyList(),
    ),
    ResourceEntry.WithArgs.String(
      file = File("."),
      name = "string_2",
      docs = null,
      args = listOf(
        StringArg(
          position = 1,
          type = 'd',
          isOptional = false,
        ),
        StringArg(
          position = 2,
          type = 'i',
          isOptional = false,
        ),
        StringArg(
          position = 3,
          type = 'u',
          isOptional = true,
        ),
        StringArg(
          position = 4,
          type = 'x',
          isOptional = false,
        ),
        StringArg(
          position = 5,
          type = 'o',
          isOptional = false,
        ),
      ),
    ),
    ResourceEntry.WithArgs.String(
      file = File("."),
      name = "string_3",
      docs = null,
      args = listOf(
        StringArg(
          position = 1,
          type = 'f',
          isOptional = true,
        ),
        StringArg(
          position = 2,
          type = 'e',
          isOptional = false,
        ),
        StringArg(
          position = 3,
          type = 'g',
          isOptional = false,
        ),
        StringArg(
          position = 4,
          type = 'a',
          isOptional = true,
        ),
        StringArg(
          position = 5,
          type = 's',
          isOptional = true,
        ),
      ),
    ),
    ResourceEntry.WithArgs.String(
      file = File("."),
      name = "string_4",
      docs = null,
      args = listOf(
        StringArg(
          position = 1,
          type = 'c',
          isOptional = true,
        ),
      ),
    ),
  )

  private val pluralResources = listOf(
    ResourceEntry.WithArgs.Plural(
      file = File("."),
      name = "plural_1",
      docs = "Plural 1 docs",
      args = emptyList(),
    ),
    ResourceEntry.WithArgs.Plural(
      file = File("."),
      name = "plural_2",
      docs = null,
      args = listOf(
        StringArg(
          position = 1,
          type = 'd',
          isOptional = false,
        ),
        StringArg(
          position = 2,
          type = 'i',
          isOptional = false,
        ),
        StringArg(
          position = 3,
          type = 'u',
          isOptional = true,
        ),
        StringArg(
          position = 4,
          type = 'x',
          isOptional = false,
        ),
        StringArg(
          position = 5,
          type = 'o',
          isOptional = false,
        ),
      ),
    ),
    ResourceEntry.WithArgs.Plural(
      file = File("."),
      name = "plural_3",
      docs = null,
      args = listOf(
        StringArg(
          position = 1,
          type = 'f',
          isOptional = true,
        ),
        StringArg(
          position = 2,
          type = 'e',
          isOptional = false,
        ),
        StringArg(
          position = 3,
          type = 'g',
          isOptional = false,
        ),
        StringArg(
          position = 4,
          type = 'a',
          isOptional = true,
        ),
        StringArg(
          position = 5,
          type = 's',
          isOptional = true,
        ),
      ),
    ),
    ResourceEntry.WithArgs.Plural(
      file = File("."),
      name = "plural_4",
      docs = null,
      args = listOf(
        StringArg(
          position = 1,
          type = 'c',
          isOptional = true,
        ),
      ),
    ),
  )

  @Before
  fun setUp() {
    codegenDestination = folder.newFolder()
  }

  @Test
  fun `GIVEN resource type String and composeExtensions disabled THEN generate standard extensions`() {
    val codegenFile = File("${codegenDestination.absolutePath}/com/example/Strings.kt")
    val writer = WithArgsCatalogWriter(
      packageName = "com.example",
      resourceType = ResourceType.String,
    )
    writer.write(
      stringResources,
      sourceSetName = "main",
      codegenDestination = codegenDestination,
      asComposeExtensions = false,
    )
    assertThat(
      codegenFile.readBytes().toString(Charset.defaultCharset()),
    ).isEqualTo(
      """
      |// Auto-generated by Catalog. DO NOT EDIT.
      |// https://github.com/flavioarfaria/Catalog
      |@file:JvmName("StringsMain")
      |@file:Suppress("NOTHING_TO_INLINE")
      |
      |package com.example
      |
      |import android.content.Context
      |import androidx.fragment.app.Fragment
      |import com.flaviofaria.catalog.runtime.Strings
      |import kotlin.Char
      |import kotlin.CharSequence
      |import kotlin.Double
      |import kotlin.Int
      |import kotlin.String
      |import kotlin.Suppress
      |import kotlin.UInt
      |import kotlin.jvm.JvmName
      |
      |/**
      | * String 1 docs
      | */
      |public inline val Strings.string1: Int
      |  get() = R.string.string_1
      |
      |/**
      | * String 1 docs
      | */
      |context(Context)
      |public inline fun Strings.string1(): CharSequence = getText(R.string.string_1)
      |
      |/**
      | * String 1 docs
      | */
      |context(Fragment)
      |public inline fun Strings.string1(): CharSequence = getText(R.string.string_1)
      |
      |public inline val Strings.string2: Int
      |  get() = R.string.string_2
      |
      |context(Context)
      |public inline fun Strings.string2(
      |  arg1: Int,
      |  arg2: Int,
      |  arg3: UInt,
      |  arg4: UInt,
      |  arg5: UInt,
      |): String = getString(R.string.string_2, arg1, arg2, arg3, arg4, arg5)
      |
      |context(Fragment)
      |public inline fun Strings.string2(
      |  arg1: Int,
      |  arg2: Int,
      |  arg3: UInt,
      |  arg4: UInt,
      |  arg5: UInt,
      |): String = getString(R.string.string_2, arg1, arg2, arg3, arg4, arg5)
      |
      |public inline val Strings.string3: Int
      |  get() = R.string.string_3
      |
      |context(Context)
      |public inline fun Strings.string3(
      |  arg1: Double,
      |  arg2: Double,
      |  arg3: Double,
      |  arg4: Double,
      |  arg5: String,
      |): String = getString(R.string.string_3, arg1, arg2, arg3, arg4, arg5)
      |
      |context(Fragment)
      |public inline fun Strings.string3(
      |  arg1: Double,
      |  arg2: Double,
      |  arg3: Double,
      |  arg4: Double,
      |  arg5: String,
      |): String = getString(R.string.string_3, arg1, arg2, arg3, arg4, arg5)
      |
      |public inline val Strings.string4: Int
      |  get() = R.string.string_4
      |
      |context(Context)
      |public inline fun Strings.string4(arg1: Char): String = getString(R.string.string_4, arg1)
      |
      |context(Fragment)
      |public inline fun Strings.string4(arg1: Char): String = getString(R.string.string_4, arg1)
      |""".trimMargin(),
    )
  }

  @Test
  fun `GIVEN resource type String and composeExtensions enabled THEN generate standard extensions`() {
    val codegenFile = File("${codegenDestination.absolutePath}/com/example/Strings.kt")
    val writer = WithArgsCatalogWriter(
      packageName = "com.example",
      resourceType = ResourceType.String,
    )
    writer.write(
      stringResources,
      sourceSetName = "main",
      codegenDestination = codegenDestination,
      asComposeExtensions = true,
    )
    assertThat(
      codegenFile.readBytes().toString(Charset.defaultCharset()),
    ).isEqualTo(
      """
      |// Auto-generated by Catalog. DO NOT EDIT.
      |// https://github.com/flavioarfaria/Catalog
      |@file:JvmName("StringsMain")
      |@file:Suppress("NOTHING_TO_INLINE")
      |
      |package com.example
      |
      |import android.content.Context
      |import androidx.compose.runtime.Composable
      |import androidx.compose.runtime.ReadOnlyComposable
      |import androidx.compose.ui.res.stringResource
      |import androidx.fragment.app.Fragment
      |import com.flaviofaria.catalog.runtime.Strings
      |import kotlin.Char
      |import kotlin.CharSequence
      |import kotlin.Double
      |import kotlin.Int
      |import kotlin.String
      |import kotlin.Suppress
      |import kotlin.UInt
      |import kotlin.jvm.JvmName
      |
      |/**
      | * String 1 docs
      | */
      |public inline val Strings.string1: Int
      |  get() = R.string.string_1
      |
      |/**
      | * String 1 docs
      | */
      |context(Context)
      |@Composable
      |@ReadOnlyComposable
      |public inline fun Strings.string1(): CharSequence = stringResource(R.string.string_1)
      |
      |/**
      | * String 1 docs
      | */
      |context(Fragment)
      |@Composable
      |@ReadOnlyComposable
      |public inline fun Strings.string1(): CharSequence = stringResource(R.string.string_1)
      |
      |public inline val Strings.string2: Int
      |  get() = R.string.string_2
      |
      |context(Context)
      |@Composable
      |@ReadOnlyComposable
      |public inline fun Strings.string2(
      |  arg1: Int,
      |  arg2: Int,
      |  arg3: UInt,
      |  arg4: UInt,
      |  arg5: UInt,
      |): String = stringResource(R.string.string_2, arg1, arg2, arg3, arg4, arg5)
      |
      |context(Fragment)
      |@Composable
      |@ReadOnlyComposable
      |public inline fun Strings.string2(
      |  arg1: Int,
      |  arg2: Int,
      |  arg3: UInt,
      |  arg4: UInt,
      |  arg5: UInt,
      |): String = stringResource(R.string.string_2, arg1, arg2, arg3, arg4, arg5)
      |
      |public inline val Strings.string3: Int
      |  get() = R.string.string_3
      |
      |context(Context)
      |@Composable
      |@ReadOnlyComposable
      |public inline fun Strings.string3(
      |  arg1: Double,
      |  arg2: Double,
      |  arg3: Double,
      |  arg4: Double,
      |  arg5: String,
      |): String = stringResource(R.string.string_3, arg1, arg2, arg3, arg4, arg5)
      |
      |context(Fragment)
      |@Composable
      |@ReadOnlyComposable
      |public inline fun Strings.string3(
      |  arg1: Double,
      |  arg2: Double,
      |  arg3: Double,
      |  arg4: Double,
      |  arg5: String,
      |): String = stringResource(R.string.string_3, arg1, arg2, arg3, arg4, arg5)
      |
      |public inline val Strings.string4: Int
      |  get() = R.string.string_4
      |
      |context(Context)
      |@Composable
      |@ReadOnlyComposable
      |public inline fun Strings.string4(arg1: Char): String = stringResource(R.string.string_4, arg1)
      |
      |context(Fragment)
      |@Composable
      |@ReadOnlyComposable
      |public inline fun Strings.string4(arg1: Char): String = stringResource(R.string.string_4, arg1)
      |""".trimMargin(),
    )
  }

  @Test
  fun `GIVEN resource type Plurals and composeExtensions disabled THEN generate standard extensions`() {
    val codegenFile = File("${codegenDestination.absolutePath}/com/example/Plurals.kt")
    val writer = WithArgsCatalogWriter(
      packageName = "com.example",
      resourceType = ResourceType.Plural,
    )
    writer.write(
      pluralResources,
      sourceSetName = "main",
      codegenDestination = codegenDestination,
      asComposeExtensions = false,
    )
    assertThat(
      codegenFile.readBytes().toString(Charset.defaultCharset()),
    ).isEqualTo(
      """
      |// Auto-generated by Catalog. DO NOT EDIT.
      |// https://github.com/flavioarfaria/Catalog
      |@file:JvmName("PluralsMain")
      |@file:Suppress("NOTHING_TO_INLINE")
      |
      |package com.example
      |
      |import android.content.Context
      |import androidx.fragment.app.Fragment
      |import com.flaviofaria.catalog.runtime.Plurals
      |import kotlin.Char
      |import kotlin.CharSequence
      |import kotlin.Double
      |import kotlin.Int
      |import kotlin.String
      |import kotlin.Suppress
      |import kotlin.UInt
      |import kotlin.jvm.JvmName
      |
      |/**
      | * Plural 1 docs
      | */
      |public inline val Plurals.plural1: Int
      |  get() = R.plurals.plural_1
      |
      |/**
      | * Plural 1 docs
      | */
      |context(Context)
      |public inline fun Plurals.plural1(quantity: Int): CharSequence =
      |    resources.getQuantityString(R.plurals.plural_1, quantity)
      |
      |/**
      | * Plural 1 docs
      | */
      |context(Fragment)
      |public inline fun Plurals.plural1(quantity: Int): CharSequence =
      |    resources.getQuantityString(R.plurals.plural_1, quantity)
      |
      |public inline val Plurals.plural2: Int
      |  get() = R.plurals.plural_2
      |
      |context(Context)
      |public inline fun Plurals.plural2(
      |  quantity: Int,
      |  arg1: Int,
      |  arg2: Int,
      |  arg3: UInt,
      |  arg4: UInt,
      |  arg5: UInt,
      |): String = resources.getQuantityString(R.plurals.plural_2, quantity, arg1, arg2, arg3, arg4, arg5)
      |
      |context(Fragment)
      |public inline fun Plurals.plural2(
      |  quantity: Int,
      |  arg1: Int,
      |  arg2: Int,
      |  arg3: UInt,
      |  arg4: UInt,
      |  arg5: UInt,
      |): String = resources.getQuantityString(R.plurals.plural_2, quantity, arg1, arg2, arg3, arg4, arg5)
      |
      |public inline val Plurals.plural3: Int
      |  get() = R.plurals.plural_3
      |
      |context(Context)
      |public inline fun Plurals.plural3(
      |  quantity: Int,
      |  arg1: Double,
      |  arg2: Double,
      |  arg3: Double,
      |  arg4: Double,
      |  arg5: String,
      |): String = resources.getQuantityString(R.plurals.plural_3, quantity, arg1, arg2, arg3, arg4, arg5)
      |
      |context(Fragment)
      |public inline fun Plurals.plural3(
      |  quantity: Int,
      |  arg1: Double,
      |  arg2: Double,
      |  arg3: Double,
      |  arg4: Double,
      |  arg5: String,
      |): String = resources.getQuantityString(R.plurals.plural_3, quantity, arg1, arg2, arg3, arg4, arg5)
      |
      |public inline val Plurals.plural4: Int
      |  get() = R.plurals.plural_4
      |
      |context(Context)
      |public inline fun Plurals.plural4(quantity: Int, arg1: Char): String =
      |    resources.getQuantityString(R.plurals.plural_4, quantity, arg1)
      |
      |context(Fragment)
      |public inline fun Plurals.plural4(quantity: Int, arg1: Char): String =
      |    resources.getQuantityString(R.plurals.plural_4, quantity, arg1)
      |""".trimMargin(),
    )
  }

  @Test
  fun `GIVEN resource type Plurals and composeExtensions enabled THEN generate standard extensions`() {
    val codegenFile = File("${codegenDestination.absolutePath}/com/example/Plurals.kt")
    val writer = WithArgsCatalogWriter(
      packageName = "com.example",
      resourceType = ResourceType.Plural,
    )
    writer.write(
      pluralResources,
      sourceSetName = "main",
      codegenDestination = codegenDestination,
      asComposeExtensions = true,
    )
    assertThat(
      codegenFile.readBytes().toString(Charset.defaultCharset()),
    ).isEqualTo(
      """
      |// Auto-generated by Catalog. DO NOT EDIT.
      |// https://github.com/flavioarfaria/Catalog
      |@file:JvmName("PluralsMain")
      |@file:Suppress("NOTHING_TO_INLINE")
      |
      |package com.example
      |
      |import android.content.Context
      |import androidx.compose.runtime.Composable
      |import androidx.compose.runtime.ReadOnlyComposable
      |import androidx.compose.ui.ExperimentalComposeUiApi
      |import androidx.compose.ui.res.pluralStringResource
      |import androidx.fragment.app.Fragment
      |import com.flaviofaria.catalog.runtime.Plurals
      |import kotlin.Char
      |import kotlin.CharSequence
      |import kotlin.Double
      |import kotlin.Int
      |import kotlin.OptIn
      |import kotlin.String
      |import kotlin.Suppress
      |import kotlin.UInt
      |import kotlin.jvm.JvmName
      |
      |/**
      | * Plural 1 docs
      | */
      |public inline val Plurals.plural1: Int
      |  get() = R.plurals.plural_1
      |
      |/**
      | * Plural 1 docs
      | */
      |context(Context)
      |@OptIn(ExperimentalComposeUiApi::class)
      |@Composable
      |@ReadOnlyComposable
      |public inline fun Plurals.plural1(quantity: Int): CharSequence =
      |    pluralStringResource(R.plurals.plural_1, quantity)
      |
      |/**
      | * Plural 1 docs
      | */
      |context(Fragment)
      |@OptIn(ExperimentalComposeUiApi::class)
      |@Composable
      |@ReadOnlyComposable
      |public inline fun Plurals.plural1(quantity: Int): CharSequence =
      |    pluralStringResource(R.plurals.plural_1, quantity)
      |
      |public inline val Plurals.plural2: Int
      |  get() = R.plurals.plural_2
      |
      |context(Context)
      |@OptIn(ExperimentalComposeUiApi::class)
      |@Composable
      |@ReadOnlyComposable
      |public inline fun Plurals.plural2(
      |  quantity: Int,
      |  arg1: Int,
      |  arg2: Int,
      |  arg3: UInt,
      |  arg4: UInt,
      |  arg5: UInt,
      |): String = pluralStringResource(R.plurals.plural_2, quantity, arg1, arg2, arg3, arg4, arg5)
      |
      |context(Fragment)
      |@OptIn(ExperimentalComposeUiApi::class)
      |@Composable
      |@ReadOnlyComposable
      |public inline fun Plurals.plural2(
      |  quantity: Int,
      |  arg1: Int,
      |  arg2: Int,
      |  arg3: UInt,
      |  arg4: UInt,
      |  arg5: UInt,
      |): String = pluralStringResource(R.plurals.plural_2, quantity, arg1, arg2, arg3, arg4, arg5)
      |
      |public inline val Plurals.plural3: Int
      |  get() = R.plurals.plural_3
      |
      |context(Context)
      |@OptIn(ExperimentalComposeUiApi::class)
      |@Composable
      |@ReadOnlyComposable
      |public inline fun Plurals.plural3(
      |  quantity: Int,
      |  arg1: Double,
      |  arg2: Double,
      |  arg3: Double,
      |  arg4: Double,
      |  arg5: String,
      |): String = pluralStringResource(R.plurals.plural_3, quantity, arg1, arg2, arg3, arg4, arg5)
      |
      |context(Fragment)
      |@OptIn(ExperimentalComposeUiApi::class)
      |@Composable
      |@ReadOnlyComposable
      |public inline fun Plurals.plural3(
      |  quantity: Int,
      |  arg1: Double,
      |  arg2: Double,
      |  arg3: Double,
      |  arg4: Double,
      |  arg5: String,
      |): String = pluralStringResource(R.plurals.plural_3, quantity, arg1, arg2, arg3, arg4, arg5)
      |
      |public inline val Plurals.plural4: Int
      |  get() = R.plurals.plural_4
      |
      |context(Context)
      |@OptIn(ExperimentalComposeUiApi::class)
      |@Composable
      |@ReadOnlyComposable
      |public inline fun Plurals.plural4(quantity: Int, arg1: Char): String =
      |    pluralStringResource(R.plurals.plural_4, quantity, arg1)
      |
      |context(Fragment)
      |@OptIn(ExperimentalComposeUiApi::class)
      |@Composable
      |@ReadOnlyComposable
      |public inline fun Plurals.plural4(quantity: Int, arg1: Char): String =
      |    pluralStringResource(R.plurals.plural_4, quantity, arg1)
      |""".trimMargin(),
    )
  }
}
