package com.flaviofaria.catalog.codegen

import com.flaviofaria.catalog.codegen.writer.PluralCatalogWriter
import com.flaviofaria.catalog.codegen.writer.StringArrayCatalogWriter
import com.flaviofaria.catalog.codegen.writer.StringCatalogWriter
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import java.io.File

class ResourceSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val resourcesPath: String,
    pkg: String,
    composeExtensions: Boolean,
    private val xmlResourceParser: XmlResourceParser,
) : SymbolProcessor {

    private val resourceReducer = ResourceReducer()
    private val stringCatalogWriter = StringCatalogWriter(pkg, composeExtensions)
    private val pluralCatalogWriter = PluralCatalogWriter(pkg, composeExtensions)
    private val stringArrayCatalogWriter = StringArrayCatalogWriter(pkg, composeExtensions)
    private var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (!invoked) {
            File(resourcesPath)
                .walk()
                .asSequence()
                .filter { it.parentFile.name.startsWith("values") && it.extension == "xml" }
                .flatMap(xmlResourceParser::parseFile)
                .groupBy { it::class } // groups by type
                .map { it.value }
                .flatMap { groupedByType ->
                    // groups by resource name
                    groupedByType.groupBy { it.name }.map { it.value }
                }
                .map(resourceReducer::reduce)
                .groupBy { it::class } // groups them back by type to write Kotlin files
                .forEach { (type, resources) ->
                    when (type) {
                        ResourceEntry.String::class -> {
                            @Suppress("UNCHECKED_CAST")
                            stringCatalogWriter.write(
                                codeGenerator,
                                resources as List<ResourceEntry.String>, // TODO unchecked cast
                            )
                        }
                        ResourceEntry.Plural::class -> {
                            @Suppress("UNCHECKED_CAST")
                            pluralCatalogWriter.write(
                                codeGenerator,
                                resources as List<ResourceEntry.Plural>, // TODO
                            )
                        }
                        ResourceEntry.StringArray::class -> {
                            @Suppress("UNCHECKED_CAST")
                            stringArrayCatalogWriter.write(
                                codeGenerator,
                                resources as List<ResourceEntry.StringArray>, // TODO
                            )
                        }
                    }
                }
            invoked = true
        }
        return emptyList()
    }
}
