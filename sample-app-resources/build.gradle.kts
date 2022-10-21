plugins {
  id(libs.plugins.android.application.get().pluginId)
  id(libs.plugins.kotlin.android.get().pluginId)
  id(libs.plugins.catalog.get().pluginId)
}

catalog {
  composeExtensions = false
}

android {
  namespace = "com.flaviofaria.catalog.sample.resources"
  compileSdk = libs.versions.android.sdk.compile.get().toInt()

  defaultConfig {
    applicationId = "com.flaviofaria.catalog"
    minSdk = libs.versions.android.sdk.min.get().toInt()
    targetSdk = libs.versions.android.sdk.target.get().toInt()
    versionCode = 1
    versionName = "1.0"
  }
  buildFeatures {
    viewBinding = true
  }
  flavorDimensions += "version"
  productFlavors {
    create("demo") {
      dimension = "version"
      applicationIdSuffix = ".demo"
      versionNameSuffix = "-demo"
    }
    create("full") {
      dimension = "version"
      applicationIdSuffix = ".full"
      versionNameSuffix = "-full"
    }
  }
}

dependencies {
  implementation(project(":sample-library"))
  implementation(libs.androidx.appcompat)
  implementation(libs.android.material)
}
