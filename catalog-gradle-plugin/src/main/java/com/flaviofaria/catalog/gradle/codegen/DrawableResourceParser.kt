/*
 * Copyright (C) 2023 Flavio Faria
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
import javax.xml.parsers.DocumentBuilder

class DrawableResourceParser(private val docBuilder: DocumentBuilder) {

  fun parseFile(file: File): ResourceEntry.Drawable {
    val doc = docBuilder.parse(file)
    return ResourceEntry.Drawable(
      file = file,
      name = file.nameWithoutExtension,
      type = when (doc.documentElement.tagName) {
        "animated-vector" -> ResourceEntry.Drawable.Type.ANIMATED_VECTOR
        "animation-list" -> ResourceEntry.Drawable.Type.ANIMATION_LIST
        "bitmap" -> ResourceEntry.Drawable.Type.BITMAP_REFERENCE
        "clip" -> ResourceEntry.Drawable.Type.CLIP
        "inset" -> ResourceEntry.Drawable.Type.INSET
        "layer-list" -> ResourceEntry.Drawable.Type.LAYER_LIST
        "level-list" -> ResourceEntry.Drawable.Type.LEVEL_LIST
        "nine-patch" -> ResourceEntry.Drawable.Type.NINE_PATCH_REFERENCE
        "scale" -> ResourceEntry.Drawable.Type.SCALE
        "selector" -> ResourceEntry.Drawable.Type.STATE_LIST
        "shape" -> ResourceEntry.Drawable.Type.SHAPE
        "transition" -> ResourceEntry.Drawable.Type.TRANSITION
        "vector" -> ResourceEntry.Drawable.Type.VECTOR
        else -> ResourceEntry.Drawable.Type.OTHER
      }
    )
  }
}
