plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
}

android {
    namespace = "com.flaviofaria.catalog.runtime"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

group = "com.flaviofaria.catalog"
version = "0.1"

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.flaviofaria.catalog"
            artifactId = "catalog-runtime"
            version = "0.1"

            artifact("$buildDir/outputs/aar/${artifactId}-release.aar")
        }
    }
}
