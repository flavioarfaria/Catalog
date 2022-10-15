package com.flaviofaria.catalog.gradle

data class SourceSetQualifier(
    val name: String,
    val type: SourceSetType,
)

enum class SourceSetType {
    VARIANT,
    BUILD_TYPE,
    FLAVOR,
    MAIN,
}
