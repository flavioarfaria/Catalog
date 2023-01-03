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

import com.google.common.truth.Truth.assertThat
import org.intellij.lang.annotations.Language
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class XmlResourceParserTest {

  @get:Rule
  var folder = TemporaryFolder()

  private val parser = XmlResourceParser()

  @Test
  fun `parseFile()`() {
    @Language("XML")
    val xmlContent =
      """
            |<resources>
            |   <string name="string_no_args_wo_docs">String no args w/o docs</string>
            |   <!-- Some test doc -->
            |   <string name="string_no_args_with_docs">String no args with docs</string>
            |   <!--
            |   Some test documentation:
            |       - Bullet 1
            |       - Bullet 2
            |   @since 20221231
            |   -->
            |   <string name="string_no_args_with_ktdocs">String no args w/o docs</string>
            |   <string name="string_with_non_positioned_args">String with %d non-positioned %d args</string>
            |   <string name="string_with_positioned_args">Args %3${'$'}d are %1${'$'}f out %4${'$'}s of %2${'$'}c order</string>
            |   <string name="unformatted_string" formatted="false">Some %1${'$'}f unformatted %2${'$'}s args %3${'$'}d</string>
            |   <string name="double_percent_symbol">Double %% symbol</string>
            |   <string name="escaped_percent_symbol">Escaped \% symbol</string>
            |   <!-- there's no arg count validation, the only risk is going out of bounds -->
            |   <plurals name="some_plural">
            |       <item quantity="one">Single %1${'$'}d argument</item>
            |       <item quantity="other">Double %2${'$'}d arguments %1${'$'}d</item>
            |   </plurals>
            |   <string-array name="some_string_array">
            |       <item>Item 1</item>
            |       <item>Item 2</item>
            |       <item>Item 3</item>
            |   </string-array>
            |</resources>
            """.trimMargin()

    val file = folder.newFile()
    file.writeText(xmlContent)

    val resourceEntries = parser.parseFile(file)

    assertThat(resourceEntries).containsExactly(
      ResourceEntry.WithArgs.String(
        file = file,
        name = "string_no_args_wo_docs",
        docs = null,
        args = emptyList(),
      ),
      ResourceEntry.WithArgs.String(
        file = file,
        name = "string_no_args_with_docs",
        docs = "Some test doc",
        args = emptyList(),
      ),
      ResourceEntry.WithArgs.String(
        file = file,
        name = "string_no_args_with_ktdocs",
        docs = "Some test documentation:\n    - Bullet 1\n    - Bullet 2\n@since 20221231",
        args = emptyList(),
      ),
      ResourceEntry.WithArgs.String(
        file = file,
        name = "string_with_non_positioned_args",
        docs = null,
        args = listOf(
          StringArg(1, 'd'),
          StringArg(2, 'd'),
        ),
      ),
      ResourceEntry.WithArgs.String(
        file = file,
        name = "string_with_positioned_args",
        docs = null,
        args = listOf(
          StringArg(3, 'd'),
          StringArg(1, 'f'),
          StringArg(4, 's'),
          StringArg(2, 'c'),
        ),
      ),
      ResourceEntry.WithArgs.String(
        file = file,
        name = "unformatted_string",
        docs = null,
        args = emptyList(),
      ),
      ResourceEntry.WithArgs.String(
        file = file,
        name = "double_percent_symbol",
        docs = null,
        args = emptyList(),
      ),
      ResourceEntry.WithArgs.String(
        file = file,
        name = "escaped_percent_symbol",
        docs = null,
        args = emptyList(),
      ),
      ResourceEntry.WithArgs.Plural(
        file = file,
        name = "some_plural",
        docs = "there's no arg count validation, the only risk is going out of bounds",
        args = listOf(
          StringArg(1, 'd'),
          StringArg(2, 'd'),
        ),
      ),
      ResourceEntry.StringArray(
        file = file,
        name = "some_string_array",
        docs = null,
      ),
    )
  }
}
