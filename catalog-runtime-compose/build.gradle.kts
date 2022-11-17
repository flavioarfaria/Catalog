plugins {
  id(libs.plugins.android.library.get().pluginId)
  id(libs.plugins.kotlin.android.get().pluginId)
  id(libs.plugins.gradle.maven.publish.get().pluginId)
}

android {
  namespace = "com.flaviofaria.catalog.runtime.compose"
  compileSdk = libs.versions.android.sdk.compile.get().toInt()

  defaultConfig {
    minSdk = libs.versions.android.sdk.min.get().toInt()
    targetSdk = libs.versions.android.sdk.target.get().toInt()
  }
}
