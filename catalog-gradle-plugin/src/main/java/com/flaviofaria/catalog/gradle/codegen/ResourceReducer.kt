/*
 * Copyright (C) 2022 Flavio Faria
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flaviofaria.catalog.gradle.codegen

import java.io.File

class ResourceReducer {

  // TODO figure out if it's possible to tighten typing here (all elements are of the same ResourceEntry subtype)
  fun reduce(resources: List<ResourceEntry>): ResourceEntry {
    val typedArgs = mutableMapOf<Int, Pair<StringArg, File>>()
    return resources
      .asSequence()
      .onEach { it.validateArgumentTypes(typedArgs) }
      .maxByOrNull { resource ->
        when (resource) {
          is ResourceEntry.WithArgs.String -> resource.args.size
          is ResourceEntry.WithArgs.Plural -> resource.args.size
          else -> return resource // non-reducable resource type
        }
      }!!.apply {
        // they all have the same name, it doesn't matter
        resources.first().verifyIfNoArgHasBeenSkipped(typedArgs)
      }
  }

  private fun ResourceEntry.validateArgumentTypes(
    typedArgs: MutableMap<Int, Pair<StringArg, File>>,
  ) {
    val args = when (this) {
      is ResourceEntry.WithArgs.String -> args
      is ResourceEntry.WithArgs.Plural -> args
      else -> return // no need to validate other resource types
    }
    args.forEach { arg ->
      val knownArg = typedArgs[arg.position]
      if (knownArg == null) {
        typedArgs[arg.position] = arg to file
      } else {
        require(knownArg.first.type == arg.type) {
          """Inconsistent string argument type for [$name] at position [${arg.position}] in files
          |${knownArg.second}
          |$file
          """.trimMargin()
        }
      }
    }
  }

  private fun ResourceEntry.verifyIfNoArgHasBeenSkipped(
    typedArgs: MutableMap<Int, Pair<StringArg, File>>,
  ) {
    val keys = typedArgs.keys
    if (keys.isNotEmpty()) {
      val missingArgs = (1..keys.maxOrNull()!!).mapNotNull { position ->
        position.takeIf { it !in typedArgs }
      }.toList()
      require(missingArgs.isEmpty()) {
        val formattedArgs = missingArgs.joinToString { "#$it" }
        val plural = if (missingArgs.size > 1) "s" else ""
        "Missing arg$plural $formattedArgs for resource $name"
      }
    }
  }
}
