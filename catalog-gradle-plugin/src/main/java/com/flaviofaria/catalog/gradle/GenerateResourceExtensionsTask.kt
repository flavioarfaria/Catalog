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
import com.android.build.gradle.internal.api.DefaultAndroidSourceFile
import com.flaviofaria.catalog.gradle.codegen.Codegen
import com.flaviofaria.catalog.gradle.codegen.DrawableResourceParser
import com.flaviofaria.catalog.gradle.codegen.SourceSetQualifier
import com.flaviofaria.catalog.gradle.codegen.ValueResourceParser
import com.flaviofaria.catalog.gradle.codegen.capitalize
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateResourceExtensionsTask : DefaultTask() {

  @Nested
  lateinit var input: TaskInput

  @get:OutputDirectory
  abstract val outputFolder: DirectoryProperty

  fun initialize(input: TaskInput) {
    this.input = input
    val outputDir = File(
      project.projectDir,
      "build/generated/kotlin/generate${input.sourceSetQualifier.name.capitalize()}ResourceExtensions",
    )
    input.sourceSetDirs.map { sourceSetDir ->
      sourceSetDir.takeIf { it.exists() }?.apply {
        inputs.dir(this)
        outputs.dir(outputDir)
        outputFolder.set(outputDir)
      }
    }
  }

  @TaskAction
  fun generateResourceExtensions() {
    val commonExtension = project.extensions.getByType(CommonExtension::class.java)

    val packageName = commonExtension.namespace
      ?: commonExtension.sourceSets.findPackageNameInManifest()
      ?: error("Missing package name in manifest file for source set ${input.sourceSetQualifier.name}")

    val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

    Codegen(
      valueResourceParser = ValueResourceParser(docBuilder),
      drawableResourceParser = DrawableResourceParser(docBuilder),
      packageName = packageName,
      generateResourcesExtensions = input.generateResourcesExtensions,
      generateComposeExtensions = input.generateComposeExtensions,
      generateComposeAnimatedVectorExtensions = input.generateComposeAnimatedVectorExtensions,
    ).start(input.sourceSetQualifier.name, input.sourceSetDirs, outputFolder.asFile.get())
  }

  private fun NamedDomainObjectContainer<out AndroidSourceSet>.findPackageNameInManifest(): String? {
    return findByName(input.sourceSetQualifier.name)?.readManifestPackageName()
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

  data class TaskInput(
    @Input val generateResourcesExtensions: Boolean,
    @Input val generateComposeExtensions: Boolean,
    @Input val generateComposeAnimatedVectorExtensions: Boolean,
    @Internal val sourceSetDirs: Set<File>,
    @Internal val sourceSetQualifier: SourceSetQualifier,
  )
}
