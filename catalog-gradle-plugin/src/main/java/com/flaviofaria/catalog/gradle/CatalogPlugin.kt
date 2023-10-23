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

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet
import com.android.build.gradle.internal.api.DefaultAndroidSourceFile
import com.android.build.gradle.internal.parsePackage
import com.flaviofaria.catalog.gradle.codegen.SourceSetQualifier
import com.flaviofaria.catalog.gradle.codegen.SourceSetType
import com.flaviofaria.catalog.gradle.codegen.capitalize
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

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
        ?: project.dependsOn(group = "androidx.compose.ui", name= "ui")
      if (generateComposeExtensions) {
        project.addRuntimeDependency("compose")
      }
      val generateComposeAnimatedVectorExtensions = project.dependsOn(
        group = "androidx.compose.animation",
        name = "animation-graphics",
      )

      var sourceSetQualifier = SourceSetQualifier("main", SourceSetType.MAIN)
      val mainTaskProvider = project.getTaskProviderForSourceSet(
        generateResourcesExtensions = catalogExtension.generateResourcesExtensions,
        generateComposeExtensions = generateComposeExtensions,
        generateComposeAnimatedVectorExtensions = generateComposeAnimatedVectorExtensions,
        packageName = commonExtension.getPackageName(sourceSetQualifier),
        sourceSetDirs = commonExtension.getQualifiedSourceSetsByName("main"),
        sourceSetQualifier = sourceSetQualifier,
      )

      androidComponents.onVariants { variant ->
        sourceSetQualifier = SourceSetQualifier(variant.name, SourceSetType.VARIANT)
        val variantTaskProvider = project.getTaskProviderForSourceSet(
          generateResourcesExtensions = catalogExtension.generateResourcesExtensions,
          generateComposeExtensions = generateComposeExtensions,
          generateComposeAnimatedVectorExtensions = generateComposeAnimatedVectorExtensions,
          packageName = commonExtension.getPackageName(sourceSetQualifier),
          sourceSetDirs = commonExtension.getQualifiedSourceSetsByName(variant.name),
          sourceSetQualifier = sourceSetQualifier,
        )
        val buildTypeTaskProvider = variant.buildType?.let { buildType ->
          sourceSetQualifier = SourceSetQualifier(buildType, SourceSetType.BUILD_TYPE)
          project.getTaskProviderForSourceSet(
            generateResourcesExtensions = catalogExtension.generateResourcesExtensions,
            generateComposeExtensions = generateComposeExtensions,
            generateComposeAnimatedVectorExtensions = generateComposeAnimatedVectorExtensions,
            packageName = commonExtension.getPackageName(sourceSetQualifier),
            sourceSetDirs = commonExtension.getQualifiedSourceSetsByName(buildType),
            sourceSetQualifier = sourceSetQualifier,
          )
        }
        val flavorTaskProvider = variant.flavorName.takeUnless {
          it?.isEmpty() == true // build variants come with a "" flavor
        }?.let { flavorName ->
          sourceSetQualifier = SourceSetQualifier(flavorName, SourceSetType.FLAVOR)
          project.getTaskProviderForSourceSet(
            generateResourcesExtensions = catalogExtension.generateResourcesExtensions,
            generateComposeExtensions = generateComposeExtensions,
            generateComposeAnimatedVectorExtensions = generateComposeAnimatedVectorExtensions,
            packageName = commonExtension.getPackageName(sourceSetQualifier),
            sourceSetDirs = commonExtension.getQualifiedSourceSetsByName(flavorName),
            sourceSetQualifier = sourceSetQualifier,
          )
        }
        variant.sources.java?.apply {
          addGeneratedSourceDirectory(
            mainTaskProvider,
            GenerateResourceExtensionsTask::outputFolder,
          )
          addGeneratedSourceDirectory(
            variantTaskProvider,
            GenerateResourceExtensionsTask::outputFolder,
          )
          buildTypeTaskProvider?.let {
            addGeneratedSourceDirectory(
              buildTypeTaskProvider,
              GenerateResourceExtensionsTask::outputFolder,
            )
          }
          flavorTaskProvider?.let {
            addGeneratedSourceDirectory(
              flavorTaskProvider,
              GenerateResourceExtensionsTask::outputFolder,
            )
          }
        }
      }
    }
  }

  private fun CommonExtension<*, *, *, *>.getPackageName(
    sourceSetQualifier: SourceSetQualifier,
  ): String {
    return namespace
      ?: sourceSets.findByName(sourceSetQualifier.name)?.readManifestPackageName()
      ?: error("Missing package name in manifest file for source set ${sourceSetQualifier.name}")
  }

  private fun AndroidSourceSet.readManifestPackageName(): String? {
    val manifestFile = (manifest as DefaultAndroidSourceFile).srcFile
    return if (manifestFile.exists()) {
      val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
      val doc = docBuilder.parse(manifestFile)
      val manifestRoot = doc.getElementsByTagName("manifest").item(0)
      manifestRoot.attributes.getNamedItem("package")?.nodeValue
    } else null
  }

  private fun Project.getTaskProviderForSourceSet(
    generateResourcesExtensions: Boolean,
    generateComposeExtensions: Boolean,
    generateComposeAnimatedVectorExtensions: Boolean,
    packageName: String,
    sourceSetDirs: Set<File>,
    sourceSetQualifier: SourceSetQualifier,
  ): TaskProvider<GenerateResourceExtensionsTask> {
    val taskName = "generate${sourceSetQualifier.name.capitalize()}ResourceExtensions"
    return runCatching {
      tasks.named(taskName, GenerateResourceExtensionsTask::class.java)
    }.getOrNull() ?: tasks.register(
      taskName,
      GenerateResourceExtensionsTask::class.java,
    ) { task ->
      task.initialize(
        GenerateResourceExtensionsTask.TaskInput(
          generateResourcesExtensions = generateResourcesExtensions,
          generateComposeExtensions = generateComposeExtensions,
          generateComposeAnimatedVectorExtensions = generateComposeAnimatedVectorExtensions,
          packageName = packageName,
          sourceSetDirs = sourceSetDirs,
          sourceSetQualifier = sourceSetQualifier,
        )
      )
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

  /**
   * Gets all the /res folders for a given source set name.
   *
   * This should only get one item, unless another source set
   * has been added by another plugin or Gradle script.
   */
  private fun CommonExtension<*, *, *, *>.getQualifiedSourceSetsByName(
    sourceSetName: String,
  ): Set<File> {
    return sourceSets.getByName(sourceSetName).res.let { res ->
      (res as DefaultAndroidSourceDirectorySet).srcDirs
    }
  }

  private fun Project.dependsOn(group: String, name: String): Boolean {
    val dependencyConfigs = setOf(
      configurations.getByName("api"),
      configurations.getByName("compileOnly"),
      configurations.getByName("implementation"),
    )
    return configurations
      .asSequence()
      .filter { config -> dependencyConfigs.any { it in config.extendsFrom } }
      .flatMap { it.dependencies }
      .any { it.group == group && it.name == name }
  }

  companion object {
    private const val RUNTIME_VERSION = "0.3.0-SNAPSHOT"
  }
}
