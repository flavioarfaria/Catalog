package com.flaviofaria.catalog.codegen.writer

import com.flaviofaria.catalog.codegen.ResourceEntry
import com.flaviofaria.catalog.codegen.toCamelCase
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies

class StringCatalogWriter(
    private val packageName: String,
) : CatalogWriter<ResourceEntry.String> {
    override fun write(codeGenerator: CodeGenerator, resources: Iterable<ResourceEntry.String>) {
        codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = true), // TODO add sources for incremental builds
            packageName = packageName,
            fileName = "Strings",
        ).use { stream ->
            val fileContent = """
                |package $packageName
                |
                |import android.content.Context
                |import android.view.View
                |import androidx.fragment.app.Fragment
                |
                |import $packageName.R
                |
                |object Strings {
                |
                |${resources.joinToString("\n\n") { it.generateProperty().prependIndent(" ") }}
                |
                |${resources.joinToString("\n\n") { it.generateContextMethod().prependIndent("  ") }}
                |
                |${resources.joinToString("\n\n") { it.generateFragmentMethod().prependIndent("  ") }}
                |}
                """.trimMargin().toByteArray()
            stream.write(fileContent)
        }
    }

    // TODO avoid calling toCamelCase() frequently
    private fun ResourceEntry.String.generateProperty(): String {
        return """
            |${generateDocs()}val ${name.toCamelCase()}: Int
            |  get() = R.string.$name
            """.trimMargin()
    }

    private fun ResourceEntry.String.generateContextMethod(): String {
        return generateExtensionMethod("Context")
    }

    private fun ResourceEntry.String.generateFragmentMethod(): String {
        return generateExtensionMethod("Fragment")
    }

    private fun ResourceEntry.String.generateExtensionMethod(methodReceiver: String): String {
        val sortedArgs = args.sortedBy { it.position }
        val typedArgs = sortedArgs.mapIndexed { i, arg ->
            val primitiveType = when (arg.type) {
                'd', 'i' -> "Int"
                'u', 'x', 'o' -> "UInt"
                'f', 'e', 'g', 'a' -> "Double"
                's' -> "String"
                'c' -> "Char"
                'n' -> null
                else -> error("Unexpected string resource argument type: ${arg.type}")
            }
            primitiveType?.let { "arg${i + 1}: $primitiveType" }
        }.filterNotNull()

        val varargs = if (sortedArgs.isNotEmpty()) {
            ", " + sortedArgs.mapIndexed { i, _ -> "arg${i + 1}" }.joinToString()
        } else ""

        return """
            |${generateDocs()}context($methodReceiver)
            |fun ${name.toCamelCase()}(${typedArgs.joinToString()}): String {
            |  return getString(R.string.$name$varargs)
            |}
            """.trimMargin()
    }

    private fun ResourceEntry.String.generateDocs(): String {
        return docs?.let { "/**\n${it.trim().prependIndent(" * ")}\n */\n" } ?: ""
    }
}