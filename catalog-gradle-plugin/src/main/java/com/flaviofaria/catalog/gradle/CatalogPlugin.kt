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
package com.flaviofaria.catalog.gradle

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet
import com.flaviofaria.catalog.gradle.codegen.SourceSetQualifier
import com.flaviofaria.catalog.gradle.codegen.SourceSetType
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import java.io.File

class CatalogPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val catalogExtension = project.extensions.create(
      "catalog",
      CatalogExtension::class.java,
    )
    project.tasks.withType(KotlinCompile::class.java) {
      it.kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-Xcontext-receivers")
      }
    }
    val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
    androidComponents.finalizeDsl { commonExtension ->
      if (catalogExtension.generateResourcesExtensions) {
        project.addRuntimeDependency("resources")
      }
      val generateComposeExtensions = catalogExtension.generateComposeExtensions
        ?: project.dependsOnCompose()
      if (generateComposeExtensions) {
        project.addRuntimeDependency("compose")
      }
      androidComponents.beforeVariants { variantBuilder ->
        commonExtension.addGeneratedFilesToSourceSet(variantBuilder.name)
        variantBuilder.flavorName?.takeIf { it.isNotEmpty() }?.let {
          commonExtension.addGeneratedFilesToSourceSet(it)
        }
        variantBuilder.buildType?.let { commonExtension.addGeneratedFilesToSourceSet(it) }
        commonExtension.addGeneratedFilesToSourceSet("main")
      }
      androidComponents.onVariants { variant ->
        val capitalizedVariantName = variant.name.replaceFirstChar {
          it.titlecase()
        }
        val taskName = "generate${capitalizedVariantName}ResourceExtensions"

        val sourceSetMap = mutableSetOf<Pair<File, SourceSetQualifier>>()

        commonExtension
          .getQualifiedSourceSetsByName(variant.name, SourceSetType.VARIANT)
            .let(sourceSetMap::addAll)

        variant.buildType?.let { buildType ->
          commonExtension
            .getQualifiedSourceSetsByName(buildType, SourceSetType.BUILD_TYPE)
              .let(sourceSetMap::addAll)
        }
        variant.flavorName?.takeIf { it.isNotEmpty() }?.let { flavorName ->
          commonExtension
            .getQualifiedSourceSetsByName(flavorName, SourceSetType.FLAVOR)
              .let(sourceSetMap::addAll)
        }
        commonExtension
          .getQualifiedSourceSetsByName("main", SourceSetType.MAIN)
            .let(sourceSetMap::addAll)

        project.tasks.register(
          taskName,
          GenerateResourceExtensionsTask::class.java,
        ) { task ->
          task.initialize(
            GenerateResourceExtensionsTask.TaskInput(
              variantName = variant.name,
              buildType = variant.buildType,
              productFlavors = variant.productFlavors.map { it.first },
              generateResourcesExtensions = catalogExtension.generateResourcesExtensions,
              generateComposeExtensions = generateComposeExtensions,
              qualifiedSourceSets = sourceSetMap,
            )
          )
        }
        project.afterEvaluate {
          project.tasks.named(
            "compile${capitalizedVariantName}Kotlin",
          ).configure { task ->
            task.dependsOn(taskName)
          }
        }
      }
    }
  }

  private fun Project.addRuntimeDependency(runtimeType: String) {
    configurations.getByName("api").dependencies.add(
      if (project.properties["com.flaviofaria.catalog.internal"].toString() == "true") {
        dependencies.project(mapOf("path" to ":catalog-runtime-$runtimeType"))
      } else {
        dependencies.create("com.flaviofaria.catalog:catalog-runtime-$runtimeType:$RUNTIME_VERSION")
      }
    )
  }

  private fun CommonExtension<*, *, *, *>.addGeneratedFilesToSourceSet(sourceSetName: String) {
    sourceSets {
      findByName(sourceSetName)?.kotlin?.srcDir(
        "build/generated/catalog/$sourceSetName/kotlin",
      )
    }
  }

  private fun CommonExtension<*, *, *, *>.getQualifiedSourceSetsByName(
    sourceSetName: String,
    sourceSetType: SourceSetType,
  ): List<Pair<File, SourceSetQualifier>> {
    return sourceSets.getByName(sourceSetName).res.let { res ->
      (res as DefaultAndroidSourceDirectorySet).srcDirs.map {
        it to SourceSetQualifier(sourceSetName, sourceSetType)
      }
    }
  }

  private fun Project.dependsOnCompose(): Boolean {
    val dependencyConfigs = setOf(
      configurations.getByName("api"),
      configurations.getByName("compileOnly"),
      configurations.getByName("implementation"),
    )
    return configurations
      .asSequence()
      .filter { config -> dependencyConfigs.any { it in config.extendsFrom } }
      .flatMap { it.dependencies }
      .any { it.group == "androidx.compose.ui" && it.name == "ui" }
  }

  companion object {
    private const val RUNTIME_VERSION = "0.2.0"
  }
}
