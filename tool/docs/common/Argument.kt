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

data class Argument(
    val name: String,
    val anchor: String? = null,
    val defaultValue: String? = null,
    val description: String? = null,
    val type: String? = null,
) {
    fun toAsciiDocPage(language: String): String {
        var result = ""
        result += "a| `${this.name}` "
        result += "a| `${this.type}` "
        result += "a| ${this.description ?: ""}\n"
        return result
    }

    fun toAsciiDocTableRow(language: String): String {
        var result = ""
        result += "a| `${this.name}` "
        result += "a| ${this.description} "
        result += "a| "
        this.type?.let { result += "`${it.replace("|", "\\|")}` " }
        if (language == "python") {
            result += "a| "
            this.defaultValue?.let { result += "`$it`" }
        }
        return result
    }

//    fun toJavaCommentArg(): String {
//        var result = ""
//        result += "     * @param ${snakeToCamel(this.name)} ${backquotesToCode(this.description)}\n"
//        return result
//    }
//
//    fun toJavaCommentField(): String {
//        var result = ""
//        result += "${snakeToCamel(this.name)}\n\n"
//        result += "    /**\n     * ${backquotesToCode(this.description)}\n"
//        return "$result     */\n\n"
//    }
//
//    fun toRustCommentArg(): String {
//        var result = ""
//        result += "    /// * `${this.name}` -- ${this.description}\n"
//        return result
//    }
//
//    fun toRustCommentField(): String {
//        var result = ""
//        result += "${this.name}\n\n"
//        result += "    /// ${this.description}\n"
//        return "$result\n"
//    }
//
//    fun toNodejsCommentArg(): String {
//        var result = ""
//        result += "     * @param ${snakeToCamel(this.name)} - ${backquotesToCode(this.description)}\n"
//        return result
//    }
//
//    fun toNodejsCommentField(): String {
//        var result = ""
//        result += "${snakeToCamel(this.name)}\n\n"
//        result += "    /**\n     * ${backquotesToCode(this.description)}\n"
//        return "$result     */\n\n"
//    }
}

fun backquotesToCode(text: String?): String? {
    return text?.let { Regex("`([^`]*)`").replace(text, "<code>$1</code>") }
}

fun snakeToCamel(text: String): String {
    val words = text.split("_")
    return words.first() + words.drop(1).joinToString("") { it.capitalize() }
}
