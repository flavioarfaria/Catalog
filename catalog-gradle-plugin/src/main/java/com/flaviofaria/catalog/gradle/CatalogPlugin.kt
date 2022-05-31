package com.flaviofaria.catalog.gradle

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet
import org.gradle.api.Plugin
import org.gradle.api.Project

class CatalogPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply("com.google.devtools.ksp")
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.finalizeDsl {
            it.sourceSets.forEach { androidSourceSet ->
                (androidSourceSet.res as DefaultAndroidSourceDirectorySet).srcDirs.flatMap { resDir ->
                    resDir.listFiles()?.toList().orEmpty()
                }.filter { resTypeDir ->
                    resTypeDir.name == "values"
                }.flatMap { resTypeDir ->
                    resTypeDir.listFiles()?.toList().orEmpty()
                }.filter { xmlFile ->
                    xmlFile.name == "strings.xml"
                }.forEach { stringsFile ->
                    println(stringsFile)
                }
            }
        }
    }
}