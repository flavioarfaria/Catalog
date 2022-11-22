buildscript {
  dependencies {
    classpath(libs.kotlin.gradle.plugin)
  }

  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

allprojects {
  repositories {
    mavenCentral()
    google()
  }
}
