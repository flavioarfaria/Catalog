plugins {
  id("de.fayard.refreshVersions") version "0.51.0"
}

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }
}
enableFeaturePreview("VERSION_CATALOGS")
rootProject.name = "build-logic"
include(":catalog-gradle-plugin")
project(":catalog-gradle-plugin").projectDir = File("../catalog-gradle-plugin")
