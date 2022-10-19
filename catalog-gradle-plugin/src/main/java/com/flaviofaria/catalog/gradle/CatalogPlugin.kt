package com.flaviofaria.catalog.gradle

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet
import com.flaviofaria.catalog.codegen.SourceSetQualifier
import com.flaviofaria.catalog.codegen.SourceSetType
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
            project.addRuntimeDependency()
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

                project.tasks.register(
                    taskName,
                    GenerateResourceExtensionsTask::class.java,
                ) { task ->
                    task.initialize(
                        GenerateResourceExtensionsTask.TaskInput(
                            variantName = variant.name,
                            buildType = variant.buildType!!, // TODO !!
                            productFlavors = variant.productFlavors.map { it.first },
                            composeExtensions = catalogExtension.composeExtensions
                                ?: project.dependsOnCompose(),
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

    private fun Project.addRuntimeDependency() {
        configurations.getByName("api").dependencies.add(
            dependencies.create("com.flaviofaria.catalog:catalog-runtime:0.1")
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
    ): List<Pair<File, SourceSetQualifier>>? {
        return sourceSets?.getByName(sourceSetName)?.res?.let { res ->
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
}