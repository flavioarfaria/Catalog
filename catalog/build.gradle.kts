plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = 32

    kotlinOptions {
        freeCompilerArgs += "-Xcontext-receivers"
    }
    namespace = "com.flaviofaria.catalog"
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
}
