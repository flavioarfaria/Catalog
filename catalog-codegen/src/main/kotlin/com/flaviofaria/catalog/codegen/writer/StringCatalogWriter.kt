package com.flaviofaria.catalog.codegen.writer

import com.flaviofaria.catalog.codegen.ResourceEntry
import com.flaviofaria.catalog.codegen.toCamelCase
import java.io.File

class StringCatalogWriter(
    private val packageName: String,
    private val composeExtensions: Boolean,
) : CatalogWriter<ResourceEntry.String> {

    override fun write(
        resources: Iterable<ResourceEntry.String>,
        sourceSetName: String,
        codegenDestination: File,
    ) {
        val capitalizedSourceSetName = sourceSetName.replaceFirstChar {
            it.titlecase()
        }
        with(File(codegenDestination, "Strings.kt")) {
            createNewFile()
            val composeImports = if (composeExtensions) {
                """
                |import androidx.compose.runtime.Composable
                |import androidx.compose.runtime.ReadOnlyComposable
                |import androidx.compose.ui.res.stringResource"""
            } else ""
            val fileContent = """
                |@file:JvmName("Strings$capitalizedSourceSetName")
                |@file:Suppress("NOTHING_TO_INLINE")
                |package $packageName
                |$composeImports
                |import android.content.Context
                |import android.view.View
                |import androidx.fragment.app.Fragment
                |import com.flaviofaria.catalog.runtime.Strings
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
    private fun ResourceEntry.String.generateProperty(): String {
        return """
            |${generateDocs()}inline val Strings.${name.toCamelCase()}: Int
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
        val composeAnnotations = if (composeExtensions) {
            """
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
                else -> error("Unexpected string resource argument type: ${arg.type}")
            }
            val nullability = if (!composeExtensions && arg.isOptional) "?" else ""
            primitiveType?.let { "arg${i + 1}: $primitiveType$nullability" }
        }.filterNotNull()

        val varargs = if (sortedArgs.isNotEmpty()) {
            ", " + List(sortedArgs.size) { i -> "arg${i + 1}" }.joinToString()
        } else ""

        val styledByDefault = varargs.isEmpty()
        val returnType = if (styledByDefault) "CharSequence" else "String"
        val methodName = when {
            composeExtensions -> "stringResource"
            styledByDefault -> "getText"
            else -> "getString"
        }
        return """
            |${generateDocs()}context($methodReceiver)$composeAnnotations
            |${inline}fun Strings.${name.toCamelCase()}(${typedArgs.joinToString()}): $returnType {
            |  return $methodName(R.string.$name$varargs)
            |}
            """.trimMargin()
    }

    private fun ResourceEntry.String.generateDocs(): String {
        return docs?.let { "/**\n${it.trim().prependIndent(" * ")}\n */\n" } ?: ""
    }
}