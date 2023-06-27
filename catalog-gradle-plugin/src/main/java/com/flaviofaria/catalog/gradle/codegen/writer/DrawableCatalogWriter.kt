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
package com.flaviofaria.catalog.gradle.codegen.writer

import com.flaviofaria.catalog.gradle.codegen.ResourceEntry
import com.flaviofaria.catalog.gradle.codegen.ResourceEntry.Drawable.Type
import com.flaviofaria.catalog.gradle.codegen.ResourceType
import com.flaviofaria.catalog.gradle.codegen.toCamelCase
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeName

@OptIn(ExperimentalKotlinPoetApi::class)
class DrawableCatalogWriter(
  packageName: String,
  private val generateComposeAnimatedVectorExtensions: Boolean,
) : CatalogWriter<ResourceEntry.Drawable>(
  packageName, ResourceType.Drawable,
) {

  private val optInClass = ClassName("kotlin", "OptIn")
  private val experimentalAnimationGraphicsApiClass =
    ClassName(
      "androidx.compose.animation.graphics",
      "ExperimentalAnimationGraphicsApi",
    )

  private val painterResourceMember = MemberName(
    "androidx.compose.ui.res",
    "painterResource",
  )

  private val animatedVectorResourceMember = MemberName(
    "androidx.compose.animation.graphics.res",
    "animatedVectorResource",
  )

  private val contextCompatClass = ClassName(
    "androidx.core.content",
    "ContextCompat",
  )

  private val drawableClass = ClassName(
    "android.graphics.drawable",
    "Drawable",
  )

  private val composePainterClass = ClassName(
    "androidx.compose.ui.graphics.painter",
    "Painter",
  )

  private val composeAnimatorVectorClass = ClassName(
    "androidx.compose.animation.graphics.vector",
    "AnimatedImageVector",
  )

  override fun buildExtensionMethod(
    builder: FileSpec.Builder,
    resource: ResourceEntry.Drawable,
    contextReceiver: TypeName?,
    asComposeExtensions: Boolean,
  ): FileSpec.Builder {
    val statementArgs: List<Any>
    val statementFormat: String
    val returnType: TypeName
    when {
      !asComposeExtensions -> {
        val context = when (contextReceiver) {
          contextClass -> "this@Context"
          fragmentClass -> "requireContext()"
          else -> error("Unexpected receiver: $contextReceiver")
        }
        statementFormat = "return %T.getDrawable($context, %T.drawable.%L)!!"
        statementArgs = mutableListOf(contextCompatClass, rClass, resource.name)
        returnType = drawableClass
      }
      resource.type == Type.ANIMATED_VECTOR -> {
        statementFormat = "return %T.%M(%T.drawable.%L)"
        statementArgs = mutableListOf(
          composeAnimatorVectorClass,
          animatedVectorResourceMember,
          rClass,
          resource.name,
        )
        returnType = composeAnimatorVectorClass
      }
      else -> {
        statementFormat = "return %M(%T.drawable.%L)"
        statementArgs = mutableListOf(painterResourceMember, rClass, resource.name)
        returnType = composePainterClass
      }
    }
    return builder.addFunction(
      FunSpec.builder(resource.name.toCamelCase())
        .apply {
          if (asComposeExtensions) {
            if (resource.type == Type.ANIMATED_VECTOR) {
              addAnnotation(
                AnnotationSpec.builder(optInClass)
                  .addMember("%T::class", experimentalAnimationGraphicsApiClass)
                  .build(),
              )
            }
            addAnnotation(composableClass)
          }
        }
        .addModifiers(KModifier.INLINE)
        .apply { contextReceiver?.let { contextReceivers(it) } }
        .receiver(
          if (asComposeExtensions) {
            composeReceiverClass
          } else {
            resourcesReceiverClass
          }
        )
        .returns(returnType)
        .addStatement(statementFormat, *statementArgs.toTypedArray())
        .build()
    )
  }

  override fun shouldGenerateExtension(
    resource: ResourceEntry.Drawable,
    asComposeExtensions: Boolean,
  ): Boolean {
    // https://developer.android.com/jetpack/compose/resources
    return !asComposeExtensions
      || resource.type == Type.BITMAP
      || resource.type == Type.VECTOR
      || (generateComposeAnimatedVectorExtensions && resource.type == Type.ANIMATED_VECTOR)
  }
}
