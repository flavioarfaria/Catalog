plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.catalog) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.gradle.maven.publish) apply false
}

buildscript {
  dependencies {
    classpath(libs.kotlin.gradle.plugin)
  }
  repositories {
    google()
  }
}

repositories {
  google()
}
