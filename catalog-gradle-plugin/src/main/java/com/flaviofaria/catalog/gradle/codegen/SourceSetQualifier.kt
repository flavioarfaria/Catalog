package com.flaviofaria.catalog.gradle.codegen

data class SourceSetQualifier(
  val name: String,
  val type: SourceSetType,
)

// Order based on https://developer.android.com/studio/write/add-resources#resource_merging
enum class SourceSetType {
  VARIANT,
  BUILD_TYPE,
  FLAVOR,
  MAIN,
}
