pluginManagement {
  repositories {
    mavenLocal()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
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
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    google()
    mavenCentral()
    mavenLocal()
  }
}
enableFeaturePreview("VERSION_CATALOGS")
rootProject.name = "Catalog"
include(":sample-app-resources")
include(":sample-app-compose")
include(":catalog-gradle-plugin")
include(":sample-library")
include(":catalog-runtime-resources")
include(":catalog-runtime-compose")
