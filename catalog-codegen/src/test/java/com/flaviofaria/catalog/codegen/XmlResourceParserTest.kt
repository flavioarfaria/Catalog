package com.flaviofaria.catalog.codegen

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
      ResourceEntry.String(
        file = file,
        name = "string_no_args_wo_docs",
        docs = null,
        args = emptyList(),
      ),
      ResourceEntry.String(
        file = file,
        name = "string_no_args_with_docs",
        docs = "Some test doc",
        args = emptyList(),
      ),
      ResourceEntry.String(
        file = file,
        name = "string_with_non_positioned_args",
        docs = null,
        args = listOf(
          StringArg(1, 'd', false),
          StringArg(2, 'd', false),
        ),
      ),
      ResourceEntry.String(
        file = file,
        name = "string_with_positioned_args",
        docs = null,
        args = listOf(
          StringArg(3, 'd', false),
          StringArg(1, 'f', false),
          StringArg(4, 's', false),
          StringArg(2, 'c', false),
        ),
      ),
      ResourceEntry.String(
        file = file,
        name = "unformatted_string",
        docs = null,
        args = emptyList(),
      ),
      ResourceEntry.String(
        file = file,
        name = "double_percent_symbol",
        docs = null,
        args = emptyList(),
      ),
      ResourceEntry.String(
        file = file,
        name = "escaped_percent_symbol",
        docs = null,
        args = emptyList(),
      ),
      ResourceEntry.Plural(
        file = file,
        name = "some_plural",
        docs = "there's no arg count validation, the only risk is going out of bounds",
        args = listOf(
          StringArg(1, 'd', false),
          StringArg(2, 'd', true),
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
