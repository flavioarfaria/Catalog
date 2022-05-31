dependencies {
    implementation("com.android.tools.build:gradle:7.2.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.6.21-1.0.5")
}

plugins {
    id("org.jetbrains.kotlin.jvm")
    kotlin("android.extensions")
    `java-library`
    `java-gradle-plugin`
    `maven-publish`
}

gradlePlugin {
    plugins {
        create("catalog") {
            id = "com.flaviofaria.catalog"
            implementationClass = "com.flaviofaria.catalog.CatalogPlugin"
        }
    }
}
