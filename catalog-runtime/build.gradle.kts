plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    `maven-publish`
}

android {
    namespace = "com.flaviofaria.catalog.runtime"
    compileSdk = libs.versions.android.sdk.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.sdk.min.get().toInt()
        targetSdk = libs.versions.android.sdk.target.get().toInt()
    }
}

val catalogPluginId = libs.plugins.catalog.get().pluginId
val catalogVersion = libs.versions.catalog.get()

group = catalogPluginId
version = catalogVersion

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = catalogPluginId
            artifactId = "catalog-runtime"
            version = catalogVersion

            artifact("$buildDir/outputs/aar/${artifactId}-release.aar")
        }
    }
}
