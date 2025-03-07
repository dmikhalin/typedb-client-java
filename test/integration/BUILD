#
# Copyright (C) 2022 Vaticle
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

load("@vaticle_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@vaticle_typedb_common//test:rules.bzl", "typedb_java_test")

typedb_java_test(
    name = "test-client-query",
    srcs = ["ClientQueryTest.java"],
    server_mac_artifact = "@vaticle_typedb_artifact_mac//file",
    server_linux_artifact = "@vaticle_typedb_artifact_linux//file",
    server_windows_artifact = "@vaticle_typedb_artifact_windows//file",
    test_class = "com.vaticle.typedb.client.test.integration.ClientQueryTest",
    deps = [
        # Internal dependencies
        "//:client-java",
        "//api:api",

        # External dependencies from @vaticle
        "@vaticle_typeql//java:typeql-lang",
        "@vaticle_typeql//java/common",
        "@vaticle_typeql//java/query",

        # External dependencies from Maven
        "@maven//:org_hamcrest_hamcrest_library",
        "@maven//:org_slf4j_slf4j_api",
    ],
)

checkstyle_test(
    name = "checkstyle",
    include = glob(["*"]),
    license_type = "apache-header",
)
