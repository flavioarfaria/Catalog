plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
}

dependencies {
  implementation(libs.square.kotlinpoet)

  testImplementation(libs.junit)
  testImplementation(libs.google.truth)
}
