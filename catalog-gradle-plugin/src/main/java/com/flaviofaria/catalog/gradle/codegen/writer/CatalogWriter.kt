package com.flaviofaria.catalog.gradle.codegen.writer

import com.flaviofaria.catalog.gradle.codegen.ResourceEntry
import java.io.File

interface CatalogWriter<T : ResourceEntry> {
  fun write(resources: Iterable<T>, sourceSetName: String, codegenDestination: File)
}
