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
package com.flaviofaria.catalog.gradle.codegen.writer

import com.flaviofaria.catalog.gradle.codegen.ResourceEntry
import com.flaviofaria.catalog.gradle.codegen.ResourceType
import com.flaviofaria.catalog.gradle.codegen.toCamelCase
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName

@OptIn(ExperimentalKotlinPoetApi::class)
class StringArrayCatalogWriter(
  packageName: String,
) : CatalogWriter<ResourceEntry.StringArray>(
  packageName, ResourceType.StringArray,
) {

  private val stringArrayResourceMember = MemberName(
    "androidx.compose.ui.res",
    "stringArrayResource",
  )

  override fun buildExtensionMethod(
    builder: FileSpec.Builder,
    resource: ResourceEntry.StringArray,
    contextReceiver: TypeName?,
    asComposeExtensions: Boolean,
  ): FileSpec.Builder {
    val statementArgs: List<Any>
    val methodName: String
    if (asComposeExtensions) {
      statementArgs = mutableListOf(stringArrayResourceMember, rClass, resource.name)
      methodName = "%M"
    } else {
      statementArgs = mutableListOf(rClass, resource.name)
      methodName = "resources.getStringArray"
    }

    val statementFormat = "return $methodName(%T.array.%L)"
    return builder.addFunction(
      FunSpec.builder(resource.name.toCamelCase())
        .apply { resource.docs?.let(::addKdoc) }
        .apply {
          if (asComposeExtensions) {
            addAnnotation(composableClass)
            addAnnotation(readOnlyComposableClass)
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
        .returns(
          Array::class
            .asClassName()
            .parameterizedBy(String::class.asClassName())
        )
        .addStatement(statementFormat, *statementArgs.toTypedArray())
        .build()
    )
  }
}
