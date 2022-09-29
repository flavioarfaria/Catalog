package com.flaviofaria.catalog.gradle

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet
import com.android.build.gradle.internal.api.DefaultAndroidSourceFile
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class CatalogPlugin : Plugin<Project> {
    // android.namespace first, if absent, get package id from manifest
    override fun apply(project: Project) {
        val androidPluginHandler = { _: Plugin<*> ->
            project.afterEvaluate {
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
                            val manifestFile =
                                (androidSourceSet.manifest as DefaultAndroidSourceFile).srcFile
                            val packageName = ext.namespace
                                ?: readPackageName(manifestFile)
                                ?: error("Missing package name in $manifestFile")
                            ksp.arg("resourcesPath", stringsFile.parentFile.absolutePath)
                            ksp.arg("package", packageName)
                            println(stringsFile)
                        }
                }
            }
        }
        project.plugins.withId("com.android.application", androidPluginHandler)
        project.plugins.withId("com.android.library", androidPluginHandler)
    }

    private fun readPackageName(manifestFile: File): String? {
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = docBuilder.parse(manifestFile)
        val manifestRoot = doc.getElementsByTagName("manifest").item(0)
        return manifestRoot.attributes.getNamedItem("package").nodeValue
    }
}