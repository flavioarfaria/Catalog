pluginManagement {
  repositories {
    mavenLocal()
    gradlePluginPortal()
    google()
    mavenCentral()
  }
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
rootProject.name = "Catalog"
include(":sample")
include(":catalog-codegen")
include(":catalog-gradle-plugin")
include(":library")
include(":catalog-runtime")
