package com.flaviofaria.catalog.gradle

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.internal.api.DefaultAndroidSourceFile
import com.flaviofaria.catalog.gradle.codegen.Codegen
import com.flaviofaria.catalog.gradle.codegen.SourceSetQualifier
import com.flaviofaria.catalog.gradle.codegen.XmlResourceParser
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

@CacheableTask
abstract class GenerateResourceExtensionsTask : DefaultTask() {

  @Nested
  lateinit var input: TaskInput

  fun initialize(input: TaskInput) {
    this.input = input
    input.qualifiedSourceSets.map { qualifiedSourceSet ->
      qualifiedSourceSet.first.takeIf { it.exists() }?.apply {
        inputs.dir(this)
        outputs.dir(
          File(
            project.projectDir,
            "build/generated/catalog/${qualifiedSourceSet.second.name}/kotlin"
          )
        )
      }
    }
  }

  @TaskAction
  fun generateResourceExtensions() {
    val commonExtension = project.extensions.getByType(CommonExtension::class.java)

    val packageName = commonExtension.namespace
      ?: commonExtension.sourceSets.findPackageNameInManifest()
      ?: error("Missing package name in manifest file for build variant ${input.variantName}")

    Codegen(
      xmlResourceParser = XmlResourceParser(),
      packageName = packageName,
      generateComposeExtensions = input.generateComposeExtensions,
      projectDir = project.projectDir,
    ).start(input.qualifiedSourceSets)
  }

  private fun NamedDomainObjectContainer<out AndroidSourceSet>.findPackageNameInManifest(): String? {
    // https://developer.android.com/studio/build/manage-manifests#merge_priorities
    return findByName(input.variantName)?.readManifestPackageName()
      ?: input.buildType?.let { findByName(it) }?.readManifestPackageName()
      ?: input.productFlavors.asSequence().mapNotNull { flavor ->
        findByName(flavor)?.readManifestPackageName()
      }.firstOrNull()
      ?: findByName("main")?.readManifestPackageName()
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
    @Input val variantName: String,
    @Input val buildType: String?,
    @Input val productFlavors: List<String>,
    @Input val generateComposeExtensions: Boolean = false,
    @Internal val qualifiedSourceSets: Set<Pair<File, SourceSetQualifier>>
  )
}
