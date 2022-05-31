package com.flaviofaria.catalog.codegen

import java.io.File

class ResourceReducer {

    // TODO figure out if it's possible to tighten typing here (all elements are of the same ResourceEntry subtype)
    fun reduce(resources: List<ResourceEntry>): ResourceEntry {
        val typedArgs = mutableMapOf<Int, Pair<StringArg, File>>()
        return resources
            .asSequence()
            .onEach { it.validateArgumentTypes(typedArgs) }
            .maxByOrNull { resource ->
                when (resource) {
                    is ResourceEntry.String -> resource.args.size
                    is ResourceEntry.Plural -> resource.args.size
                    else -> return resource // non-reducable resource type
                }
            }!!
    }

    private fun ResourceEntry.validateArgumentTypes(
        typedArgs: MutableMap<Int, Pair<StringArg, File>>,
    ) {
        val args = when (this) {
            is ResourceEntry.String -> args
            is ResourceEntry.Plural -> args
            else -> return // no need to validate other resource types
        }
        args.forEach { arg ->
            val knownArg = typedArgs[arg.position]
            if (knownArg == null) {
                typedArgs[arg.position] = arg to file
            } else {
                require(knownArg.first.type == arg.type) {
                    """Inconsistent string argument type for [$name] at position [${arg.position}] in files
                      |${knownArg.second}
                      |$file
                    """.trimMargin()
                }
            }
        }
    }
}