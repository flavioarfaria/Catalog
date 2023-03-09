/*
 * Copyright (C) 2022 Flavio Faria
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

  data class Color(
    override val file: File,
    override val name: String,
    override val docs: String?,
  ) : ResourceEntry

  data class Dimen(
    override val file: File,
    override val name: String,
    override val docs: String?,
  ) : ResourceEntry
}

data class StringArg(
  val position: Int,
  val type: Char,
)
