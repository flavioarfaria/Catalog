plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "com.flaviofaria.catalog"
        minSdk = 24
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
    }

    kotlinOptions {
        freeCompilerArgs += "-Xcontext-receivers"
    }
    namespace = "com.flaviofaria.catalog.sample"
}


dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
}