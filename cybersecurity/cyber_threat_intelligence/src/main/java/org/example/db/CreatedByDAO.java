/*
 * Copyright (C) 2023 Vaticle
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

package org.example.db;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.model.CreatedBy;

public class CreatedByDAO {

    private final TypeDBSessionWrapper db;
    private final CreatedBy created_by;

    private final String nameRel = "created_by";
    private final String typeString;

    protected static final String CREATED_BY_MATCH =
            "$ta (creator: $AAA, created: $BBB) isa created_by;";

    public CreatedByDAO(TypeDBSessionWrapper db) {
        this.db = db;
        created_by = new CreatedBy();
        typeString = created_by.getTypeString();
    }

    public ObjectNode getAllJSON() {
        var getQueryStr = "match " + CREATED_BY_MATCH + "group $ta; ";
        return db.getListJSON(getQueryStr, nameRel ,created_by.getRolePlayers());
    }

    public ObjectNode getSearchJSON(String type, String name) {

        if (typeString.contains(" " + type + ";")){
            name = "\"" + name + "\"";
        }

        String search = "$ta has " + type + " = " + name + ";";
        var getQueryStr = "match " + CREATED_BY_MATCH + search + "group $ta;";

        return db.getListJSON(getQueryStr, nameRel, created_by.getRolePlayers());
    }

}