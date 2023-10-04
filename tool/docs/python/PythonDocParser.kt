/*
 *  Copyright (C) 2022 Vaticle
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.vaticle.typedb.client.tool.doc.python

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

import com.vaticle.typedb.client.tool.doc.common.Argument
import com.vaticle.typedb.client.tool.doc.common.Class
import com.vaticle.typedb.client.tool.doc.common.EnumConstant
import com.vaticle.typedb.client.tool.doc.common.Method
import com.vaticle.typedb.client.tool.doc.common.removeAllTags
import com.vaticle.typedb.client.tool.doc.common.replaceCodeTags
import com.vaticle.typedb.client.tool.doc.common.replaceEmTags

fun main(args: Array<String>) {
    val inputDirectoryName = args[0]
    val outputDirectoryName = args[1]

    val docsDir = Paths.get(outputDirectoryName)
    Files.createDirectory(docsDir)
//    Files.createDirectory(docsDir.resolve("for_java"))
//    Files.createDirectory(docsDir.resolve("for_rust"))
//    Files.createDirectory(docsDir.resolve("for_nodejs"))

    File(inputDirectoryName).walkTopDown().filter {
        it.toString().endsWith(".html") &&
                (it.toString().contains(".api.") || it.toString().contains(".common."))
    }.forEach {
        val html = it.readText(Charsets.UTF_8)
        val parsed = Jsoup.parse(html)

        parsed.select("dl.class").forEach {
            val parsedClass = if (it.selectFirst("dt.sig-object + dd > p")!!.text().contains("Enum")) {
                parseEnum(it)
            } else {
                parseClass(it)
            }
            val parsedClassName = parsedClass.name
            val parsedClassAsciiDoc = parsedClass.toAsciiDoc("python")
//            val parsedClassForJava = parsedClass.toJavaComment()
//            val parsedClassForRust = parsedClass.toRustComment()
//            val parsedClassForNodejs = parsedClass.toNodejsComment()

            val outputFile = docsDir.resolve("$parsedClassName.adoc").toFile()
            outputFile.createNewFile()
            outputFile.writeText(parsedClassAsciiDoc)

//            val outputFileJava = docsDir.resolve("for_java").resolve("$parsedClassName.txt").toFile()
//            outputFileJava.createNewFile()
//            outputFileJava.writeText(parsedClassForJava)
//
//            val outputFileRust = docsDir.resolve("for_rust").resolve("$parsedClassName.txt").toFile()
//            outputFileRust.createNewFile()
//            outputFileRust.writeText(parsedClassForRust)
//
//            val outputFileNodejs = docsDir.resolve("for_nodejs").resolve("$parsedClassName.txt").toFile()
//            outputFileNodejs.createNewFile()
//            outputFileNodejs.writeText(parsedClassForNodejs)
        }
    }
}

fun parseClass(element: Element): Class {
    val classSignElement = element.selectFirst("dt.sig-object")
    val className = classSignElement!!.selectFirst("dt.sig-object span.sig-name")!!.text()

    val classDetails = classSignElement.nextElementSibling()
    val classDetailsParagraphs = classDetails!!.children().map { it }.filter { it.tagName() == "p" }
    val (descr, bases) = classDetailsParagraphs.partition { it.select("code.py-class").isNullOrEmpty() }
    val superClasses = bases[0]!!.select("span").map { it.html() }.filter { it != "ABC" && it != "Enum" }
    val classDescr = descr.map { reformatTextWithCode(it.html()) }

    val classExamples = element.select("dl.class > dt.sig-object + dd > section:contains(Example) > div > .highlight")
        .map { it.text() }

    val methods = classDetails.select("dl.method")
        .map { parseMethod(it) }

    val properties = classDetails.select("dl.property")
        .map { parseProperty(it) }

    return Class(
        name = className,
        description = classDescr,
        examples = classExamples,
        fields = properties,
        methods = methods,
        superClasses = superClasses,
    )
}

fun parseEnum(element: Element): Class {
    val classSignElement = element.selectFirst("dt.sig-object")
    val className = classSignElement!!.selectFirst("dt.sig-object span.sig-name")!!.text()

    val classDetails = classSignElement.nextElementSibling()
    val classDetailsParagraphs = classDetails!!.children().map { it }.filter { it.tagName() == "p" }
    val (descr, bases) = classDetailsParagraphs.partition { it.select("code.py-class").isNullOrEmpty() }
    val classBases = bases[0]!!.select("span").map { it.html() }
    val classDescr = descr.map { reformatTextWithCode(it.html()) }

    val classExamples = element.select("section:contains(Examples) .highlight").map { it.text() }

    val methods = classDetails.select("dl.method").map { parseMethod(it) }
    val members = classDetails.select("dl.attribute").map { parseEnumConstant(it) }

    return Class(
        name = className,
        description = classDescr,
        enumConstants = members,
        examples = classExamples,
        methods = methods,
        superClasses = classBases,
    )
}

fun parseMethod(element: Element): Method {
    val methodSignature = enhanceSignature(element.selectFirst("dt.sig-object")!!.text())
    val methodName = element.selectFirst("dt.sig-object span.sig-name")!!.text()
    val allArgs = getArgsFromSignature(element.selectFirst("dt.sig-object")!!)
    val methodReturnType = element.select(".sig-return-typehint").text()
    val methodDescr = element.select("dl.method > dd > p").map { reformatTextWithCode(it.html()) }
    val methodArgs = element.select(".field-list > dt:contains(Parameters) + dd p").map {
        val argName = it.selectFirst("strong")!!.text()
        assert(allArgs.contains(argName))
        val argDescr = reformatTextWithCode(removeArgName(it.html())).removePrefix(" – ")
        Argument(
            name = argName,
            defaultValue = allArgs[argName]?.second,
            description = argDescr,
            type = allArgs[argName]?.first,
        )
    }
    val methodReturnDescr = element.select(".field-list > dt:contains(Returns) + dd p").text()
    val methodExamples = element.select("section:contains(Examples) .highlight").map { it.text() }

    return Method(
        name = methodName,
        signature = methodSignature,
        args = methodArgs,
        description = methodDescr,
        examples = methodExamples,
        returnDescription = methodReturnDescr,
        returnType = methodReturnType,
    )

}

fun parseProperty(element: Element): Argument {
    val name = element.selectFirst("dt.sig-object span.sig-name")!!.text()
    val type = element.selectFirst("dt.sig-object span.sig-name + .property ")?.text()?.dropWhile { !it.isLetter() }
    val descr = element.select("dd > p").map { reformatTextWithCode(it.html()) }.joinToString("\n\n")
    return Argument(
        name = name,
        description = descr,
        type = type,
    )
}

fun parseEnumConstant(element: Element): EnumConstant {
    val name = element.selectFirst("dt.sig-object span.sig-name")!!.text()
    val value = element.selectFirst("dt.sig-object span.sig-name + .property")!!.text().removePrefix("= ")
    return EnumConstant(
        name = name,
        value = value,
    )
}

fun getArgsFromSignature(methodSignature: Element): Map<String, Pair<String?, String?>> {
    return methodSignature.select(".sig-param").map {
        it.selectFirst(".n")!!.text() to
                Pair(it.select(".p + .w + .n").text(), it.selectFirst("span.default_value")?.text())
    }.toMap()
}

fun reformatTextWithCode(html: String): String {
    return removeAllTags(replaceEmTags(replaceCodeTags(html)))
}

fun removeArgName(html: String): String {
    return Regex("<strong>[^<]*</strong>").replace(html, "")
}

fun enhanceSignature(signature: String): String {
    return signature.replace("→", "->").replace("¶", "")
}
