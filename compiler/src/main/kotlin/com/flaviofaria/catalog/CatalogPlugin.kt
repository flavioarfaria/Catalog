package com.flaviofaria.catalog

import org.gradle.api.Plugin
import org.gradle.api.Project

class CatalogPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.task("hello") {
            it.doLast {
                println("Hello from the CatalogPlugin!")
            }
        }
    }
}