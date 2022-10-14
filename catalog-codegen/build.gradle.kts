plugins {
    id("org.jetbrains.kotlin.jvm")
    kotlin("android.extensions")
    `java-library`
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.flaviofaria.catalog"
version = "0.1"

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.flaviofaria.catalog"
            artifactId = "catalog-codegen"
            version = "0.1"


            from(components["java"])
        }
    }
}


dependencies {
    implementation("com.android.tools.build:gradle:7.2.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.1.3")
}