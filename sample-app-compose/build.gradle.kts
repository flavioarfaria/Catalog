plugins {
  id(libs.plugins.android.application.get().pluginId)
  id(libs.plugins.kotlin.android.get().pluginId)
  id(libs.plugins.catalog.get().pluginId)
}

catalog {
  generateResourcesExtensions = false
}

android {
  namespace = "com.flaviofaria.catalog.sample.compose"
  compileSdk = libs.versions.android.sdk.compile.get().toInt()

  defaultConfig {
    applicationId = "com.flaviofaria.catalog"
    minSdk = libs.versions.android.sdk.min.get().toInt()
    targetSdk = libs.versions.android.sdk.target.get().toInt()
    versionCode = 1
    versionName = "1.0"
  }
  buildFeatures {
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtension.get()
  }
}

dependencies {
  implementation(project(":sample-library"))
  implementation(libs.android.material)
  implementation(libs.androidx.compose.animation.graphics)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.activity.compose)
}
