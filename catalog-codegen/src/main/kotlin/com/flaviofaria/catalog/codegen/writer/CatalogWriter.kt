package com.flaviofaria.catalog.codegen.writer

import com.flaviofaria.catalog.codegen.ResourceEntry

interface CatalogWriter<T : ResourceEntry> {
    fun write(resources: Iterable<T>)
}