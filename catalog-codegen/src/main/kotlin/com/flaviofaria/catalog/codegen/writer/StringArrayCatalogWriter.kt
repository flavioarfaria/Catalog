package com.flaviofaria.catalog.codegen.writer

import com.flaviofaria.catalog.codegen.ResourceEntry
import com.flaviofaria.catalog.codegen.toCamelCase
import java.io.File

class StringArrayCatalogWriter(
    private val packageName: String,
    private val composeExtensions: Boolean,
) : CatalogWriter<ResourceEntry.StringArray> {

    override fun write(
        resources: Iterable<ResourceEntry.StringArray>,
        sourceSetName: String,
        codegenDestination: File,
    ) {
        with(File(codegenDestination, "StringArrays.kt")) {
            createNewFile()
            val composeImports = if (composeExtensions) {
                """
                |import androidx.compose.runtime.Composable
                |import androidx.compose.runtime.ReadOnlyComposable
                |import androidx.compose.ui.res.stringArrayResource"""
            } else ""
            val fileContent = """
                |package $packageName.$sourceSetName
                |$composeImports
                |import android.content.Context
                |import android.view.View
                |import androidx.fragment.app.Fragment
                |import com.flaviofaria.catalog.runtime.StringArrays
                |
                |import $packageName.R
                |
                |${resources.joinToString("\n\n") { it.generateProperty() }}
                |
                |${resources.joinToString("\n\n") { it.generateContextMethod() }}
                |
                |${resources.joinToString("\n\n") { it.generateFragmentMethod() }}
                |
                """.trimMargin()
            writeText(fileContent)
        }
    }

    // TODO avoid calling toCamelCase() frequently
    private fun ResourceEntry.StringArray.generateProperty(): String {
        return """
            |${generateDocs()}inline val StringArrays.${name.toCamelCase()}: Int
            |  get() = R.array.$name
            """.trimMargin()
    }

    private fun ResourceEntry.StringArray.generateContextMethod(): String {
        return generateExtensionMethod("Context")
    }

    private fun ResourceEntry.StringArray.generateFragmentMethod(): String {
        return generateExtensionMethod("Fragment")
    }

    private fun ResourceEntry.StringArray.generateExtensionMethod(methodReceiver: String): String {
        val composeAnnotations = if (composeExtensions) {
            """
            |@Composable
            |@ReadOnlyComposable"""
        } else ""
        val inline = if (composeExtensions) "" else "inline "
        val methodName = if (composeExtensions) {
            "stringArrayResource"
        } else {
            "resources.getStringArray"
        }
        return """
            |${generateDocs()}context($methodReceiver)$composeAnnotations
            |${inline}fun StringArrays.${name.toCamelCase()}(): Array<String> {
            |  return $methodName(R.array.$name)
            |}
            """.trimMargin()
    }

    // TODO reuse
    private fun ResourceEntry.StringArray.generateDocs(): String {
        return docs?.let { "/**\n${it.trim().prependIndent(" * ")}\n */\n" } ?: ""
    }
}