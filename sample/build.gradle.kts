plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.flaviofaria.catalog.sample"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.flaviofaria.catalog"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    /*buildFeatures {
        compose = true
    }*/

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    /*composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }*/
}

dependencies {
    implementation(project(":library"))
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    /*implementation("androidx.compose.ui:ui:1.2.0-beta2")
    implementation("androidx.compose.ui:ui-tooling:1.2.0-beta2")*/
    implementation("com.google.android.material:material:1.6.0")
}