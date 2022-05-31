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
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots") }
    google()
    mavenCentral()
  }
}
rootProject.name = "Catalog"
include(":sample")
include(":catalog-codegen")
include(":catalog-gradle-plugin")
