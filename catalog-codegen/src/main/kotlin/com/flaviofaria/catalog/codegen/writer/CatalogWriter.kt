package com.flaviofaria.catalog.codegen.writer

import com.flaviofaria.catalog.codegen.ResourceEntry
import com.google.devtools.ksp.processing.CodeGenerator

interface CatalogWriter<T : ResourceEntry> {
    fun write(codeGenerator: CodeGenerator, resources: Iterable<T>)
}