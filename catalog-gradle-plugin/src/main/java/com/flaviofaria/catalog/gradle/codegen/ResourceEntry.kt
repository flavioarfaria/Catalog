package com.flaviofaria.catalog.gradle.codegen

import java.io.File

sealed interface ResourceEntry {
  val file: File
  val name: String
  val docs: String?

  interface WithArgs : ResourceEntry {
    val args: List<StringArg>

    data class String(
      override val file: File,
      override val name: kotlin.String,
      override val docs: kotlin.String?,
      override val args: List<StringArg>,
    ) : WithArgs

    data class Plural(
      override val file: File,
      override val name: kotlin.String,
      override val docs: kotlin.String?,
      override val args: List<StringArg>,
    ) : WithArgs
  }

  data class StringArray(
    override val file: File,
    override val name: String,
    override val docs: String?,
  ) : ResourceEntry
}

data class StringArg(
  val position: Int,
  val type: Char,
  val isOptional: Boolean = false,
)
