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

  sealed interface XmlItem : ResourceEntry {
    val docs: String?

    sealed interface WithArgs : XmlItem {
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
    ) : XmlItem

    data class Color(
      override val file: File,
      override val name: String,
      override val docs: String?,
    ) : XmlItem

    data class Dimen(
      override val file: File,
      override val name: String,
      override val docs: String?,
    ) : XmlItem
  }

  data class Drawable(
    override val file: File,
    override val name: String,
    val type: Type,
  ) : ResourceEntry {
    enum class Type {
      ANIMATED_VECTOR,
      ANIMATION_LIST,
      CLIP,
      BITMAP,
      BITMAP_REFERENCE,
      INSET,
      LAYER_LIST,
      LEVEL_LIST,
      NINE_PATCH,
      NINE_PATCH_REFERENCE,
      SCALE,
      SHAPE,
      STATE_LIST,
      TRANSITION,
      VECTOR,
      OTHER,
    }
  }
}

data class StringArg(
  val position: Int,
  val type: Char,
)
