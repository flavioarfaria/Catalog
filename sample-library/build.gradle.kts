plugins {
  id(libs.plugins.android.library.get().pluginId)
  id(libs.plugins.kotlin.android.get().pluginId)
  id(libs.plugins.catalog.get().pluginId)
}

catalog {
  generateComposeExtensions = true
}

android {
  namespace = "com.flaviofaria.catalog.library"
  compileSdk = libs.versions.android.sdk.compile.get().toInt()

  defaultConfig {
    minSdk = libs.versions.android.sdk.min.get().toInt()
    targetSdk = libs.versions.android.sdk.target.get().toInt()
  }
  buildFeatures {
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtension.get()
  }
}

dependencies {
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.activity.compose)
}
