plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
}

dependencies {
  testImplementation(libs.junit)
  testImplementation(libs.google.truth)
}
