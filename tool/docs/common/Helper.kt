/*
 * Copyright (C) 2022 Vaticle
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.vaticle.typedb.client.tool.doc.common

fun replaceCodeTags(html: String): String {
    return Regex("<code[^>]*>").replace(html, "`").replace("</code>", "`")
}

fun replaceEmTags(html: String): String {
    return Regex("<em[^>]*>").replace(html, "_").replace("</em>", "_")
}

fun removeAllTags(html: String): String {
    return Regex("(?<!<)<[^<>]*>(?!>)").replace(html, "")
}

fun replaceSpaces(html: String): String {
    return html.replace("&nbsp;", " ").replace("\u00a0", " ")
}

fun replaceSymbols(html: String): String {
    return html.replace("&lt;", "<").replace("&gt;", ">")
        .replace("&amp;", "&")
}

fun replaceSymbolsForAnchor(name: String): String {
    return name.replace("[\\.,\\(\\)\\s#]".toRegex(), "_").removeSuffix("_")
}

fun mergeClasses(first: Class, second: Class): Class {
    assert(first.name == second.name)
    return Class(
        name = first.name,
        anchor = first.anchor ?: second.anchor,
        enumConstants = first.enumConstants + second.enumConstants,
        description = first.description.ifEmpty { second.description },
        examples = first.examples.ifEmpty { second.examples },
        fields = first.fields + second.fields,
        methods = first.methods + second.methods,
        packagePath = first.packagePath ?: second.packagePath,
        superClasses = first.superClasses.ifEmpty { second.superClasses },
    )
}
