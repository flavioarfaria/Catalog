package com.flaviofaria.catalog.codegen

import com.flaviofaria.catalog.codegen.writer.PluralCatalogWriter
import com.flaviofaria.catalog.codegen.writer.StringArrayCatalogWriter
import com.flaviofaria.catalog.codegen.writer.StringCatalogWriter
import java.io.File

class Codegen(
    private val xmlResourceParser: XmlResourceParser = XmlResourceParser(),
    packageName: String,
    composeExtensions: Boolean,
    private val projectDir: File,
) {

    private val resourceReducer = ResourceReducer()
    private val stringCatalogWriter = StringCatalogWriter(
        packageName,
        composeExtensions,
    )
    private val pluralCatalogWriter = PluralCatalogWriter(
        packageName,
        composeExtensions,
    )
    private val stringArrayCatalogWriter = StringArrayCatalogWriter(
        packageName,
        composeExtensions,
    )

    fun start(
        resourcesDirs: Set<Pair<File, SourceSetQualifier>>,
    ) {
        resourcesDirs
            .asSequence()
            .flatMap { (file, sourceSetName) ->
                file.walk().associateWith { sourceSetName }.toList()
            }
            .filter { (file, _) ->
                file.parentFile.name.startsWith("values") && file.extension == "xml"
            }
            .flatMap { (file, sourceSetName) ->
                xmlResourceParser
                    .parseFile(file)
                    .map { it to sourceSetName }
            }
            .groupBy { (resourceEntry, _) -> // groups resources by name to eliminate duplicates by priority
                resourceEntry.name
            }
            .map { (_, qualifiedResourceEntries) -> // selects only the resource of highest priority among duplicates
                qualifiedResourceEntries.minByOrNull { (_, qualifier) -> qualifier.type.ordinal }!!
            }
            .groupBy { (_, sourceSetQualifier) ->
                sourceSetQualifier
            }
            .map { (sourceSetQualifier, qualifiedEntries) ->
                sourceSetQualifier.name to qualifiedEntries.map {
                        (resourceEntry, _) -> resourceEntry
                }
            }
            .forEach { (sourceSetName, resourceEntries) ->
                generateExtensionFilesForSourceSet(
                    sourceSetName,
                    resourceEntries,
                )
            }
    }

    private fun generateExtensionFilesForSourceSet(
        sourceSetName: String,
        resourceEntries: List<ResourceEntry>,
    ) {
        val codegenDir = File(projectDir, "build/generated/catalog/${sourceSetName}/kotlin")
        codegenDir.mkdirs()
        resourceEntries.groupBy { it::class } // groups by type
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
                            sourceSetName,
                            codegenDir,
                        )
                    }
                    ResourceEntry.Plural::class -> {
                        @Suppress("UNCHECKED_CAST")
                        pluralCatalogWriter.write(
                            resources as List<ResourceEntry.Plural>, // TODO
                            sourceSetName,
                            codegenDir,
                        )
                    }
                    ResourceEntry.StringArray::class -> {
                        @Suppress("UNCHECKED_CAST")
                        stringArrayCatalogWriter.write(
                            resources as List<ResourceEntry.StringArray>, // TODO
                            sourceSetName,
                            codegenDir,
                        )
                    }
                }
            }
    }
}