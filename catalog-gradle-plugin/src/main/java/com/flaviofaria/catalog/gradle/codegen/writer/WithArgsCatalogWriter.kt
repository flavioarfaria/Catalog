package com.flaviofaria.catalog.gradle.codegen.writer

import com.flaviofaria.catalog.gradle.codegen.ResourceEntry
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
class WithArgsCatalogWriter(
  packageName: String,
  resourceType: ResourceType,
) : CatalogWriter<ResourceEntry.WithArgs>(
  packageName, resourceType,
) {

  private val optInClass = ClassName("kotlin", "OptIn")
  private val experimentalComposeUiApiClass =
    ClassName(
      "androidx.compose.ui",
      "ExperimentalComposeUiApi",
    )
  private val stringResourceMember = MemberName("androidx.compose.ui.res", "stringResource")
  private val pluralResourceMember = MemberName("androidx.compose.ui.res", "pluralStringResource")

  private val asPlurals = resourceType == ResourceType.Plural

  override fun buildExtensionMethod(
    builder: FileSpec.Builder,
    resource: ResourceEntry.WithArgs,
    contextReceiver: TypeName,
    asComposeExtensions: Boolean,
  ): FileSpec.Builder {
    val sortedArgs = resource.args.sortedBy { it.position }
    val returnType = if (sortedArgs.isEmpty()) CharSequence::class else String::class
    val statementArgs = mutableListOf<Any>()
    val methodName: String
    when {
      asComposeExtensions -> {
        statementArgs += if (asPlurals) pluralResourceMember else stringResourceMember
        methodName = "%M"
      }
      asPlurals -> {
        methodName = "resources.getQuantityString"
      }
      sortedArgs.isEmpty() -> {
        methodName = "getText"
      }
      else -> {
        methodName = "getString"
      }
    }
    val quantityParamName = "quantity"
    statementArgs.apply {
      add(rClass)
      add(resource.name)
      if (asPlurals) {
        add(quantityParamName)
      }
      addAll(resource.args.map { "arg${it.position}" })
    }

    val functionCallParams = mutableListOf("%T.${resourceType.resourceGroup}.%L").apply {
      if (asPlurals) {
        add("%L")
      }
      addAll(resource.args.map { "%L" })
    }

    val statementFormat = "return $methodName(${functionCallParams.joinToString()})"
    return builder.addFunction(
      FunSpec.builder(resource.name.toCamelCase())
        .apply { resource.docs?.let(::addKdoc) }
        .apply {
          if (asComposeExtensions) {
            if (asPlurals) {
              addAnnotation(
                AnnotationSpec.builder(optInClass)
                  .addMember("%T::class", experimentalComposeUiApiClass)
                  .build(),
              )
            }
            addAnnotation(composableClass)
            addAnnotation(readOnlyComposableClass)
          }
        }
        .addModifiers(KModifier.INLINE)
        .contextReceivers(contextReceiver)
        .receiver(receiverClass)
        .apply {
          if (asPlurals) {
            addParameter(name = quantityParamName, type = Int::class)
          }
          sortedArgs.forEach { parameter ->
            addParameter(
              name = "arg${parameter.position}",
              type = when (parameter.type) {
                'd', 'i' -> Int::class
                'u', 'x', 'o' -> UInt::class
                'f', 'e', 'g', 'a' -> Double::class
                's' -> String::class
                'c' -> Char::class
                else -> error(
                  """Unexpected argument type "${parameter.type}" for string
                    | resource "${resource.name}" in file "${resource.file}"""".trimMargin(),
                )
              }
            )
          }
        }
        .returns(returnType)
        .addStatement(statementFormat, *statementArgs.toTypedArray())
        .build()
    )
  }
}
