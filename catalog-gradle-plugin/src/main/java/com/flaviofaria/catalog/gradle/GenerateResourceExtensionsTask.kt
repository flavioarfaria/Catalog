package com.flaviofaria.catalog.gradle

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet
import com.android.build.gradle.internal.api.DefaultAndroidSourceFile
import com.flaviofaria.catalog.codegen.Codegen
import com.flaviofaria.catalog.codegen.SourceSetQualifier
import com.flaviofaria.catalog.codegen.SourceSetType
import com.flaviofaria.catalog.codegen.XmlResourceParser
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

abstract class GenerateResourceExtensionsTask : DefaultTask() { // TODO consider SourceTask

    @Input
    lateinit var variant: Variant

    @Input
    lateinit var catalogExtension: CatalogExtension

    private val CommonExtension<*, *, *, *>.androidSourceSets
        get() = when (this) {
            is AppExtension -> {
                sourceSets
            }
            is LibraryExtension -> {
                sourceSets
            }
            // TODO add other types like instant app
            else -> null
        }

    private fun CommonExtension<*, *, *, *>.getQualifiedSourceSetsByName(
        sourceSetName: String,
        sourceSetType: SourceSetType,
    ): List<Pair<File, SourceSetQualifier>>? {
        return androidSourceSets?.getByName(sourceSetName)?.res?.let { res ->
            (res as DefaultAndroidSourceDirectorySet).srcDirs.map {
                it to SourceSetQualifier(sourceSetName, sourceSetType)
            }
        }
    }

    @TaskAction
    fun generateResourceExtensions() {
        val commonExtension = project.extensions.getByType(CommonExtension::class.java)

        val sourceSetMap = mutableSetOf<Pair<File, SourceSetQualifier>>()

        commonExtension
            .getQualifiedSourceSetsByName(variant.name, SourceSetType.VARIANT)
            ?.let(sourceSetMap::addAll)

        variant.buildType?.let { buildType ->
            commonExtension
                .getQualifiedSourceSetsByName(buildType, SourceSetType.BUILD_TYPE)
                ?.let(sourceSetMap::addAll)
        }
        variant.flavorName?.takeIf { it.isNotEmpty() }?.let { flavorName ->
            commonExtension
                .getQualifiedSourceSetsByName(flavorName, SourceSetType.FLAVOR)
                ?.let(sourceSetMap::addAll)
        }
        commonExtension
            .getQualifiedSourceSetsByName("main", SourceSetType.MAIN)
            ?.let(sourceSetMap::addAll)

        val packageName = commonExtension.namespace
            ?: commonExtension.sourceSets.findPackageNameInManifest()
            ?: error("Missing package name in manifest file for build variant ${variant.name}")

        val composeExtensions = catalogExtension.composeExtensions ?: project.dependsOnCompose()

        Codegen(
            xmlResourceParser = XmlResourceParser(),
            packageName = packageName,
            composeExtensions = composeExtensions,
            projectDir = project.projectDir,
        ).start(sourceSetMap)
    }

    // TODO verify buildVariant.mergeResources
    private fun NamedDomainObjectContainer<out AndroidSourceSet>.findPackageNameInManifest(): String? {
        // https://developer.android.com/studio/build/manage-manifests#merge_priorities
        return findByName(variant.name)?.readManifestPackageName()
            ?: findByName(variant.buildType!!)?.readManifestPackageName() // TODO buildtype!!
            ?: variant.productFlavors.asSequence().mapNotNull { flavor ->
                findByName(flavor.first)?.readManifestPackageName() // TODO first?
            }.firstOrNull()
            ?: findByName("main")?.readManifestPackageName()
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

    private fun AndroidSourceSet.readManifestPackageName(): String? {
        val manifestFile = (manifest as DefaultAndroidSourceFile).srcFile
        return if (manifestFile.exists()) {
            val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val doc = docBuilder.parse(manifestFile)
            val manifestRoot = doc.getElementsByTagName("manifest").item(0)
            manifestRoot.attributes.getNamedItem("package")?.nodeValue
        } else null
    }
}