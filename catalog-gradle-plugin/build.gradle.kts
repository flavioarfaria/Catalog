plugins {
    id("org.jetbrains.kotlin.jvm")
    `java-library`
    `java-gradle-plugin`
    `maven-publish`
}

gradlePlugin {
    plugins {
        create("catalog") {
            id = "com.flaviofaria.catalog"
            implementationClass = "com.flaviofaria.catalog.gradle.CatalogPlugin"
        }
    }
}

group = "com.flaviofaria.catalog"
version = "0.1"

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.flaviofaria.catalog"
            artifactId = "catalog-gradle-plugin"
            version = "0.1"

            from(components["java"])
        }
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:7.2.0")
    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.7.10-1.0.6")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
}