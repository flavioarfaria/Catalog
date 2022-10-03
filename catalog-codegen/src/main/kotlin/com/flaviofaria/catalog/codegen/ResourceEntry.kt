package com.flaviofaria.catalog.codegen

import java.io.File

sealed class ResourceEntry {
    abstract val file: File
    abstract val name: kotlin.String
    abstract val docs: kotlin.String?

    data class String(
        override val file: File,
        override val name: kotlin.String,
        override val docs: kotlin.String?,
        val args: List<StringArg>,
    ) : ResourceEntry()

    data class Plural(
        override val file: File,
        override val name: kotlin.String,
        override val docs: kotlin.String?,
        val args: List<StringArg>,
    ) : ResourceEntry()

    data class StringArray(
        override val file: File,
        override val name: kotlin.String,
        override val docs: kotlin.String?,
    ) : ResourceEntry()
}

data class StringArg(
    val position: Int,
    val type: Char,
    val isOptional: Boolean = false,
)
