pluginManagement {
  repositories {
    mavenLocal()
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}
plugins {
  id("de.fayard.refreshVersions") version "0.50.2"
}
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
  repositories {
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
    google()
    mavenCentral()
    mavenLocal()
  }
}
enableFeaturePreview("VERSION_CATALOGS")
rootProject.name = "Catalog"
include(":sample-resources")
include(":sample-compose")
include(":catalog-codegen")
include(":catalog-gradle-plugin")
include(":library")
include(":catalog-runtime")
