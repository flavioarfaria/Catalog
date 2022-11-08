package com.flaviofaria.catalog.gradle.codegen

enum class ResourceType(
  val receiverType: kotlin.String,
  val resourceGroup: kotlin.String,
) {
  String(receiverType = "Strings", resourceGroup = "string"),
  Plural(receiverType = "Plurals", resourceGroup = "plurals"),
  StringArray(receiverType = "StringArrays", resourceGroup = "array"),
}
