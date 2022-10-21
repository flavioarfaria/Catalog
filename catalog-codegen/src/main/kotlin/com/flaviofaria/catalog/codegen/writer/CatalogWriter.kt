package com.flaviofaria.catalog.codegen.writer

import com.flaviofaria.catalog.codegen.ResourceEntry
import java.io.File

interface CatalogWriter<T : ResourceEntry> {
  fun write(resources: Iterable<T>, sourceSetName: String, codegenDestination: File)
}
