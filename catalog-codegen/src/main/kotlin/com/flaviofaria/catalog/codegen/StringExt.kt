package com.flaviofaria.catalog.codegen

fun String.toCamelCase(): String {
  val camelCase = StringBuilder()
  var i = 0
  var capitalize = false
  while (i < length) {
    val c = this[i]
    when {
      c == '_' || c == '-' -> capitalize = true
      capitalize -> {
        camelCase.append(c.uppercase())
        capitalize = false
      }
      else -> camelCase.append(c)
    }
    i++
  }
  return camelCase.toString()
}
