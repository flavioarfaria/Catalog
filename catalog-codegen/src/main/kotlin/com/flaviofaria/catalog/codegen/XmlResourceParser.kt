package com.flaviofaria.catalog.codegen

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
                            ResourceEntry.String(
                                file,
                                name,
                                preceedingComment,
                                if (formatted) node.textContent.extractArgs() else emptyList(),
                            )
                        }
                        "plurals" -> {
                            ResourceEntry.Plural(
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
                        "color" -> null
                        "dimen" -> null
                        "id" -> null
                        else -> null
                    }?.let {
                        resources += it
                    }
                    preceedingComment = null
                }
                Node.COMMENT_NODE -> {
                    preceedingComment = (node as Comment).data.trim()
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
                        .extractArgs()
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
            var isOptional = false
            allArgs.forEach { quantityArgs ->
                val arg = quantityArgs[argPosition]
                when {
                    arg == null -> {
                        isOptional = true
                        return@forEach
                    }
                    sharedArg == null -> sharedArg = arg
                    arg.type != sharedArg!!.type -> error(
                        """Inconsistent argument types in plural resource "$pluralName". Make sure args with the same index across all quantity entries have the same type."""
                    )
                }
            }
            sharedArg?.copy(isOptional = isOptional)?.also { sharedArgs += it }
        }
        return sharedArgs
    }

    private fun String.extractArgs(): List<StringArg> {
        val args = mutableListOf<StringArg>()
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
            args += if (positionGroup != null) {
                require(positionGroup.endsWith("$")) { // TODO improve error message for debugging
                    "Unexpected position placeholder: $positionGroup"
                }
                hasPositionalArgs = true
                val position = positionGroup.substring(0, positionGroup.lastIndex).toInt()
                StringArg(position, type)
            } else {
                StringArg(++implicitPosition, type)
            }
        }
        // TODO improve error message for debugging
        require(!hasPositionalArgs || implicitPosition == 0) {
            "Argument positions should be either all explicit or all implicit"
        }
        return args
    }
}