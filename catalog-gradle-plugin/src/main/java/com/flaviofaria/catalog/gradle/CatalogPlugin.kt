package com.flaviofaria.catalog.gradle

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet
import com.android.build.gradle.internal.api.DefaultAndroidSourceFile
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class CatalogPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("catalog", CatalogExtension::class.java)

        project.tasks.withType(KotlinCompile::class.java) {
            it.kotlinOptions {
                freeCompilerArgs = freeCompilerArgs + listOf("-Xcontext-receivers")
            }
        }
        val androidPluginHandler = { _: Plugin<*> ->
            project.afterEvaluate {
                val isApp = project.plugins.hasPlugin("com.android.application")
                val isLibrary = project.plugins.hasPlugin("com.android.library")

                when {
                    isApp -> {
                        val ext = project.extensions.getByType(BaseAppModuleExtension::class.java)
                        ext.applicationVariants.all { variant ->
                            ext.sourceSets {
                                getByName(variant.name) { sourceSet ->
                                    sourceSet.kotlin.srcDir("build/generated/ksp/${variant.name}/kotlin")
                                }
                            }
                        }
                    }
                    isLibrary -> {
                        val ext = project.extensions.getByType(LibraryExtension::class.java)
                        ext.buildTypes.all { buildType ->
                            ext.sourceSets {
                                getByName(buildType.name) { sourceSet ->
                                    sourceSet.kotlin.srcDir("build/generated/ksp/${buildType.name}/kotlin")
                                }
                            }
                        }
                    }
                }

                val composeExtensions = extension.composeExtensions ?: project.dependsOnCompose()

                project.configurations.getByName("api").dependencies.add(
                    project.dependencies.create("com.flaviofaria.catalog:catalog-runtime:0.1")
                )

                val ext = project.extensions.getByType(CommonExtension::class.java)

                (ext as CommonExtension).sourceSets.forEach { androidSourceSet ->
                    (androidSourceSet.res as DefaultAndroidSourceDirectorySet).srcDirs
                        .flatMap { resDir ->
                            resDir.listFiles()?.toList().orEmpty()
                        }.filter { resTypeDir ->
                            resTypeDir.name == "values" // todo configurations
                        }.flatMap { resTypeDir ->
                            resTypeDir.listFiles()?.toList().orEmpty()
                        }.filter { xmlFile ->
                            xmlFile.name == "strings.xml"
                        }.forEach { stringsFile ->
                            project.plugins.apply("com.google.devtools.ksp")
                            project.dependencies.add(
                                "ksp",
                                "com.flaviofaria.catalog:catalog-codegen:0.1"
                            )
                            val ksp = project.extensions.getByName("ksp") as KspExtension

                            // android.namespace first, if absent, get package id from manifest

                            val manifestFile =
                                (androidSourceSet.manifest as DefaultAndroidSourceFile).srcFile
                            val packageName = ext.namespace
                                ?: readPackageName(manifestFile)
                                ?: error("Missing package name in $manifestFile")
                            ksp.arg("resourcesPath", stringsFile.parentFile.absolutePath)
                            ksp.arg("package", packageName)
                            ksp.arg("composeExtensions", composeExtensions.toString())
                        }
                }
            }
        }
        project.plugins.withId("com.android.application", androidPluginHandler)
        project.plugins.withId("com.android.library", androidPluginHandler)
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

    private fun readPackageName(manifestFile: File): String? {
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = docBuilder.parse(manifestFile)
        val manifestRoot = doc.getElementsByTagName("manifest").item(0)
        return manifestRoot.attributes.getNamedItem("package").nodeValue
    }
}