plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  id(libs.plugins.gradle.maven.publish.get().pluginId)
  `java-library`
  `java-gradle-plugin`
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

dependencies {
  implementation(libs.android.gradle)
  implementation(libs.kotlin.gradle.plugin)
  implementation(libs.square.kotlinpoet)

  testImplementation(libs.junit)
  testImplementation(libs.google.truth)
}
