plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp") version "1.6.21-1.0.5"
    //id("com.flaviofaria.catalog") version "0.1"
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
    /*buildFeatures {
        compose = true
    }*/

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    applicationVariants.all {
        kotlin {
            sourceSets {
                getByName(name) {
                    kotlin.srcDir("build/generated/ksp/$name/kotlin")
                }
            }
        }
    }

    /*composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }*/

    /*kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xcontext-receivers"
    }*/
    namespace = "com.flaviofaria.catalog.sample"
}

ksp {
    arg("resourcesPath", android.sourceSets["main"].res.srcDirs.first().absolutePath)
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    /*implementation("androidx.compose.ui:ui:1.2.0-beta2")
    implementation("androidx.compose.ui:ui-tooling:1.2.0-beta2")*/
    implementation("com.google.android.material:material:1.6.0")
    ksp(project(":catalog-codegen"))
}