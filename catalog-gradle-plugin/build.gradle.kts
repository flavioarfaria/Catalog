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
            implementationClass = "com.flaviofaria.catalog.CatalogPlugin"
        }
    }
}

group = "com.flaviofaria.catalog"
version = "0.1"

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.flaviofaria.catalog"
            artifactId = "catalog"
            version = "0.1"

            from(components["java"])
        }
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:7.2.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.6.21-1.0.5")
}