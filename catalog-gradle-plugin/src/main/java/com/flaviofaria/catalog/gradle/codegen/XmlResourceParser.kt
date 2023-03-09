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

import org.w3c.dom.Comment
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import java.lang.Integer.max
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory

class XmlResourceParser {

  private val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

  // Original pattern from String.format() is %(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])
  // https://en.wikipedia.org/wiki/Printf_format_string#Type_field
  private val fsPattern =
    Pattern.compile("%(\\d+\\$)?([-#+ 0,(<]*)?(\\d+)?(\\.\\d+)?([tT])?([diufFeEgGxXoscpaAn%])")

  fun parseFile(file: File): Set<ResourceEntry> {
    val doc = docBuilder.parse(file)
    val resourcesElements = doc.getElementsByTagName("resources").item(0)
    val resources = mutableSetOf<ResourceEntry>()

    var preceedingComment: String? = null
    for (i in 0 until resourcesElements.childNodes.length) {
      val node = resourcesElements.childNodes.item(i)
      when (node.nodeType) {
        Node.ELEMENT_NODE -> {
          val element = node as Element
          val name = node.attributes.getNamedItem("name").nodeValue
          when (element.tagName) {
            "item" -> null
            "string" -> {
              val formatted =
                node.attributes.getNamedItem("formatted")?.nodeValue != "false"
              ResourceEntry.WithArgs.String(
                file,
                name,
                preceedingComment,
                if (formatted) node.textContent.extractArgs(name) else emptyList(),
              )
            }
            "plurals" -> {
              ResourceEntry.WithArgs.Plural(
                file,
                name,
                preceedingComment,
                node.parsePlurals(name),
              )
            }
            "string-array" -> {
              ResourceEntry.StringArray(
                file,
                name,
                preceedingComment,
              )
            }
            "color" -> {
              ResourceEntry.Color(
                file,
                name,
                preceedingComment,
              )
            }
            "dimen" -> {
              ResourceEntry.Dimen(
                file,
                name,
                preceedingComment,
              )
            }
            "id" -> null
            else -> null
          }?.let {
            resources += it
          }
          preceedingComment = null
        }
        Node.COMMENT_NODE -> {
          preceedingComment = (node as Comment).data.trimIndent().trim()
        }
      }
    }
    return resources
  }

  private fun Element.parsePlurals(pluralName: String): List<StringArg> {
    val allArgs = mutableListOf<Map<Int, StringArg>>()
    var highestArgPosition = 0
    for (i in 0 until childNodes.length) {
      val child = childNodes.item(i)
      if (child.nodeType == Node.ELEMENT_NODE) {
        val childElement = child as Element
        if (childElement.tagName == "item") {
          val quantityArgs = childElement
            .textContent
            .extractArgs(pluralName)
            .associateBy { it.position }
          if (quantityArgs.isNotEmpty()) { // plurals with no argument
            highestArgPosition = max(
              highestArgPosition,
              quantityArgs.values.maxOf { it.position },
            )
          }
          allArgs += quantityArgs
        }
      }
    }
    val sharedArgs = mutableListOf<StringArg>()
    for (argPosition in 1..highestArgPosition) {
      var sharedArg: StringArg? = null
      allArgs.forEach { quantityArgs ->
        val arg = quantityArgs[argPosition]
        when {
          arg == null -> {
            return@forEach
          }
          sharedArg == null -> sharedArg = arg
          arg.type != sharedArg!!.type -> error(
            """Inconsistent argument types in plural resource "$pluralName". Make sure args with the same index across all quantity entries have the same type."""
          )
        }
      }
      sharedArg?.let { sharedArgs += it }
    }
    return sharedArgs
  }

  private fun String.extractArgs(resourceName: String): List<StringArg> {
    val args = mutableMapOf<Int, StringArg>()
    val matcher = fsPattern.matcher(this)
    var implicitPosition = 0
    var hasPositionalArgs = false
    while (matcher.find()) {
      if (matcher.groupCount() == 1) {
        // literal %
        val content = matcher.group(0)
        require(content == "%") { // TODO improve error message for debugging
          "Unexpected string resource argument format: $content"
        }
        continue
      }
      val start = matcher.start()
      val end = matcher.end()
      if (start > 0 && this[start - 1] == '\\' || start < end && this[start + 1] == '%') {
        // ignores \% and %%
        continue
      }
      val type = matcher.group(6).first().lowercase().first()
      val positionGroup = matcher.group(1)
      val arg = if (positionGroup != null) {
        require(positionGroup.endsWith("$")) { // TODO improve error message for debugging
          "Unexpected position placeholder: $positionGroup"
        }
        hasPositionalArgs = true
        val position = positionGroup.substring(0, positionGroup.lastIndex).toInt()
        position to StringArg(position, type)
      } else {
        val position = ++implicitPosition
        position to StringArg(position, type)
      }
      val existingArg = args[arg.first]
      require(existingArg == null || existingArg.type == arg.second.type) {
        "Argument #${arg.first} appears multiple times in $resourceName with different types (%${existingArg!!.type}, %${arg.second.type})"
      }
      args += arg
    }
    // TODO improve error message for debugging
    require(!hasPositionalArgs || implicitPosition == 0) {
      "Argument positions should be either all explicit or all implicit"
    }
    return args.values.toList()
  }
}
