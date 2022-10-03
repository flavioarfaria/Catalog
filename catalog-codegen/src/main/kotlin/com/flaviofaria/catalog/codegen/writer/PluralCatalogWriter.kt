package com.flaviofaria.catalog.codegen.writer

import com.flaviofaria.catalog.codegen.ResourceEntry
import com.flaviofaria.catalog.codegen.toCamelCase
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies

class PluralCatalogWriter(
    private val packageName: String,
    private val composeExtensions: Boolean,
) : CatalogWriter<ResourceEntry.Plural> {
    override fun write(codeGenerator: CodeGenerator, resources: Iterable<ResourceEntry.Plural>) {
        codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = true), // TODO add sources for incremental builds
            packageName = packageName,
            fileName = "Plurals",
        ).use { stream ->
            val composeImports = if (composeExtensions) {
                """
                |import androidx.compose.ui.ExperimentalComposeUiApi
                |import androidx.compose.ui.res.pluralStringResource
                |import androidx.compose.runtime.Composable
                |import androidx.compose.runtime.ReadOnlyComposable"""
            } else ""
            val fileContent = """
                |package $packageName
                |$composeImports
                |import android.content.Context
                |import android.view.View
                |import androidx.fragment.app.Fragment
                |import com.flaviofaria.catalog.runtime.Plurals
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
    private fun ResourceEntry.Plural.generateProperty(): String {
        return """
            |${generateDocs()}inline val Plurals.${name.toCamelCase()}: Int
            |  get() = R.plurals.$name
            """.trimMargin()
    }

    private fun ResourceEntry.Plural.generateContextMethod(): String {
        return generateExtensionMethod("Context")
    }

    private fun ResourceEntry.Plural.generateFragmentMethod(): String {
        return generateExtensionMethod("Fragment")
    }

    // TODO reuse it with StringCatalogWriter
    private fun ResourceEntry.Plural.generateExtensionMethod(methodReceiver: String): String {
        val composeAnnotations = if (composeExtensions) {
            """
            |@OptIn(ExperimentalComposeUiApi::class)
            |@Composable
            |@ReadOnlyComposable"""
        } else ""
        val inline = if (composeExtensions) "" else "inline "
        val sortedArgs = args.sortedBy { it.position }
        val typedArgs = sortedArgs.mapIndexed { i, arg ->
            val primitiveType = when (arg.type) {
                'd', 'i' -> "Int"
                'u', 'x', 'o' -> "UInt"
                'f', 'e', 'g', 'a' -> "Double"
                's' -> "String"
                'c' -> "Char"
                'n' -> null
                else -> error("Unexpected plural resource argument type: ${arg.type}")
            }
            val nullability = if (!composeExtensions && arg.isOptional) "?" else ""
            primitiveType?.let { "arg${i + 1}: $primitiveType$nullability" }
        }.filterNotNull()

        val varargs = if (sortedArgs.isNotEmpty()) {
            ", " + List(sortedArgs.size) { i -> "arg${i + 1}" }.joinToString()
        } else ""
        val methodName = if (composeExtensions) {
            "pluralStringResource"
        } else {
            "resources.getQuantityString"
        }
        return """
            |${generateDocs()}context($methodReceiver)$composeAnnotations
            |${inline}fun Plurals.${name.toCamelCase()}(quantity: Int, ${typedArgs.joinToString()}): String {
            |  return $methodName(R.plurals.$name, quantity$varargs)
            |}
            """.trimMargin()
    }

    private fun ResourceEntry.Plural.generateDocs(): String {
        return docs?.let { "/**\n${it.trim().prependIndent(" * ")}\n */\n" } ?: ""
    }
}