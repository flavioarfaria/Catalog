package com.flaviofaria.catalog.gradle

import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet
import com.android.build.gradle.internal.api.DefaultAndroidSourceFile
import com.flaviofaria.catalog.codegen.Codegen
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

    @TaskAction
    fun generateResourceExtensions() {
        val commonExtension = project.extensions.getByType(CommonExtension::class.java)

        val sourceSetMap = mutableSetOf<Pair<File, String>>()

        commonExtension.androidSourceSets?.getByName(variant.name)?.res?.let { variantRes ->
            sourceSetMap += (variantRes as DefaultAndroidSourceDirectorySet).srcDirs.map {
                it to variant.name
            }
        }
        variant.buildType?.let { buildType ->
            commonExtension.androidSourceSets?.getByName(buildType)?.res?.let { buildTypeRes ->
                sourceSetMap += (buildTypeRes as DefaultAndroidSourceDirectorySet).srcDirs.map {
                    it to buildType
                }
            }
        }
        variant.flavorName?.takeIf { it.isNotEmpty() }?.let { flavorName ->
            commonExtension.androidSourceSets?.getByName(flavorName)?.res?.let { flavorRes ->
                sourceSetMap += (flavorRes as DefaultAndroidSourceDirectorySet).srcDirs.map {
                    it to flavorName
                }
            }
        }
        commonExtension.androidSourceSets?.getByName("main")?.res?.let { mainRes ->
            sourceSetMap += (mainRes as DefaultAndroidSourceDirectorySet).srcDirs.map {
                it to "main"
            }
        }

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
        return findSourceSetByName(variant.name)?.readManifestPackageName()
            ?: findSourceSetByName(variant.buildType!!)?.readManifestPackageName() // TODO buildtype!!
            ?: variant.productFlavors.asSequence().mapNotNull { flavor ->
                findSourceSetByName(flavor.first)?.readManifestPackageName() // TODO first?
            }.firstOrNull()
            ?: findSourceSetByName("main")?.readManifestPackageName()
    }

    private fun NamedDomainObjectContainer<out AndroidSourceSet>.findSourceSetByName(
        name: String,
    ): AndroidSourceSet? {
        return firstOrNull { androidSourceSet ->
            (androidSourceSet.res as DefaultAndroidSourceDirectorySet).name == name
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

    private fun AndroidSourceSet.readManifestPackageName(): String? {
        val manifestFile = (manifest as DefaultAndroidSourceFile).srcFile
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = docBuilder.parse(manifestFile)
        val manifestRoot = doc.getElementsByTagName("manifest").item(0)
        return manifestRoot.attributes.getNamedItem("package").nodeValue
    }
}