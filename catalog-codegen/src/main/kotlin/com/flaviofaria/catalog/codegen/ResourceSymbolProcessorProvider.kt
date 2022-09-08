package com.flaviofaria.catalog.codegen

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class ResourceSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val resourcesPath = environment.options["resourcesPath"]
        val pkg = environment.options["package"]
        println("Resources path = $resourcesPath")
        return ResourceSymbolProcessor(
            environment.codeGenerator,
            resourcesPath!!,
            pkg!!,
            XmlResourceParser(),
        )
    }

}