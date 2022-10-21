plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  `java-library`
  `java-gradle-plugin`
  `maven-publish`
}

val catalogPluginId = libs.plugins.catalog.get().pluginId
val catalogVersion = libs.versions.catalog.get()

gradlePlugin {
  plugins {
    create("catalog") {
      id = catalogPluginId
      implementationClass = "$catalogPluginId.gradle.CatalogPlugin"
    }
  }
}

group = catalogPluginId
version = catalogVersion

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = catalogPluginId
      artifactId = "catalog-gradle-plugin"
      version = catalogVersion

      from(components["java"])
    }
  }
}

dependencies {
  implementation(project(":catalog-codegen"))
  implementation(libs.android.gradle)
  implementation(libs.kotlin.gradle.plugin)
}
