package com.flaviofaria.catalog.codegen

import com.flaviofaria.catalog.codegen.writer.PluralCatalogWriter
import com.flaviofaria.catalog.codegen.writer.StringArrayCatalogWriter
import com.flaviofaria.catalog.codegen.writer.StringCatalogWriter
import java.io.File

class Codegen(
    private val xmlResourceParser: XmlResourceParser = XmlResourceParser(),
    packageName: String,
    composeExtensions: Boolean,
    codegenDestination: File,
) {

    private val resourceReducer = ResourceReducer()
    private val stringCatalogWriter = StringCatalogWriter(
        packageName,
        composeExtensions,
        codegenDestination,
    )
    private val pluralCatalogWriter = PluralCatalogWriter(
        packageName,
        composeExtensions,
        codegenDestination,
    )
    private val stringArrayCatalogWriter = StringArrayCatalogWriter(
        packageName,
        composeExtensions,
        codegenDestination,
    )

    init {
        codegenDestination.mkdirs()
    }

    fun start(
        resourcesDirs: List<File>
    ) {
        val resourcesPath = resourcesDirs.first { it.absolutePath.contains("main") } // TODO
        resourcesPath
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
                            resources as List<ResourceEntry.String>, // TODO unchecked cast
                        )
                    }
                    ResourceEntry.Plural::class -> {
                        @Suppress("UNCHECKED_CAST")
                        pluralCatalogWriter.write(
                            resources as List<ResourceEntry.Plural>, // TODO
                        )
                    }
                    ResourceEntry.StringArray::class -> {
                        @Suppress("UNCHECKED_CAST")
                        stringArrayCatalogWriter.write(
                            resources as List<ResourceEntry.StringArray>, // TODO
                        )
                    }
                }
            }
    }
}