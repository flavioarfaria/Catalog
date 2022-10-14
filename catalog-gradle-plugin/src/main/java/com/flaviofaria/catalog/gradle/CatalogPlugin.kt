package com.flaviofaria.catalog.gradle

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
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
            project.addRuntimeDependency()
            androidComponents.beforeVariants { variantBuilder ->
                commonExtension.addGeneratedFilesToSourceSet(variantBuilder.name)
            }
        }
        androidComponents.onVariants { variant ->
            val capitalizedVariantName = variant.name.replaceFirstChar {
                it.titlecase()
            }
            val taskName = "generate${capitalizedVariantName}ResourceExtensions"
            project.tasks.register(
                taskName,
                GenerateResourceExtensionsTask::class.java,
            ) { task ->
                task.variant = variant
                task.catalogExtension = catalogExtension
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

    private fun Project.addRuntimeDependency() {
        configurations.getByName("api").dependencies.add(
            dependencies.create("com.flaviofaria.catalog:catalog-runtime:0.1")
        )
    }

    private fun CommonExtension<*, *, *, *>.addGeneratedFilesToSourceSet(variantName: String) {
        sourceSets {
            getByName(variantName) { sourceSet ->
                sourceSet.kotlin.srcDir("build/generated/catalog/$variantName/kotlin")
            }
        }
    }
}