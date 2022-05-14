pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    mavenLocal()
  }
}
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
  repositories {
    google()
    mavenCentral()
  }
}
rootProject.name = "Catalog"
include(":sample", ":catalog")
include(":compiler")
