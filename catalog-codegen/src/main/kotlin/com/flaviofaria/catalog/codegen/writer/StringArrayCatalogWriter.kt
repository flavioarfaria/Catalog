package com.flaviofaria.catalog.codegen.writer

import com.flaviofaria.catalog.codegen.ResourceEntry
import com.flaviofaria.catalog.codegen.toCamelCase
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies

class StringArrayCatalogWriter(
    private val packageName: String,
) : CatalogWriter<ResourceEntry.StringArray> {
    override fun write(
        codeGenerator: CodeGenerator,
        resources: Iterable<ResourceEntry.StringArray>,
    ) {
        codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = true), // TODO add sources for incremental builds
            packageName = packageName,
            fileName = "StringArrays",
        ).use { stream ->
            val fileContent = """
                |package $packageName
                |
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
                """.trimMargin().toByteArray()
            stream.write(fileContent)
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
        return """
            |${generateDocs()}context($methodReceiver)
            |inline fun StringArrays.${name.toCamelCase()}(): Array<String> {
            |  return resources.getStringArray(R.array.$name)
            |}
            """.trimMargin()
    }

    // TODO reuse
    private fun ResourceEntry.StringArray.generateDocs(): String {
        return docs?.let { "/**\n${it.trim().prependIndent(" * ")}\n */\n" } ?: ""
    }
}