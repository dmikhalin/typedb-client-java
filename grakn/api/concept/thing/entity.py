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
from abc import ABC, abstractmethod
from typing import TYPE_CHECKING

from grakn.api.concept.thing.thing import Thing, RemoteThing

if TYPE_CHECKING:
    from grakn.api.concept.type.entity_type import EntityType
    from grakn.api.transaction import GraknTransaction


class Entity(Thing, ABC):

    def is_entity(self):
        return True

    @abstractmethod
    def get_type(self) -> "EntityType":
        pass

    @abstractmethod
    def as_remote(self, transaction: "GraknTransaction") -> "RemoteEntity":
        pass


class RemoteEntity(RemoteThing, Entity, ABC):
    pass
