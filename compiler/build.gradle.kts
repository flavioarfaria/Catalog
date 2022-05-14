plugins {
    id("org.jetbrains.kotlin.jvm")
    `java-library`
    `java-gradle-plugin`
    `maven-publish`
}

dependencies {
    implementation(gradleApi())
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