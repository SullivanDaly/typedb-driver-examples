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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.model.IdUnknown;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IdUnknownDAO {
    protected static final String IDUNKNOWN_MATCH =
            "  $idUnknown isa id_unknown, has stix_id $id, has $attribute;" +
                    "$attribute isa! $j; ";
    TypeDBSessionWrapper db;
    String typeString;


    public IdUnknownDAO(TypeDBSessionWrapper db) {
        this.db = db;
        typeString = IdUnknown.typeString;
    }

    private ObjectNode find(String getQueryStr) {
        return db.getAllJSON(getQueryStr);
    }

    public ObjectNode findAll() {
        var getQueryStr = "match " + IDUNKNOWN_MATCH + "group $id; ";
        return find(getQueryStr);
    }

    public Set<IdUnknown> findAllBeans() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String getQueryStr = "match " + IDUNKNOWN_MATCH + "group $id;";
        ObjectNode json = find(getQueryStr);
        Map<String, IdUnknown> test = objectMapper.readValue(json.toString(), new TypeReference<>() {
        });

        return new HashSet<>(test.values());
    }

    public ObjectNode search(String attrType, String attrName) {

        if (typeString.contains(" " + attrType + ";")) {
            attrName = "\"" + attrName + "\"";
        }

        String search = "$idUnknown has " + attrType + " = " + attrName + ";";
        var getQueryStr = "match " + IDUNKNOWN_MATCH + search + "group $id;";

        return find(getQueryStr);
    }


    public Set<IdUnknown> searchBeans(String attrType, String attrName) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        if (typeString.contains(" " + attrType + ";")) {
            attrName = "\"" + attrName + "\"";
        }

        String search = "$idUnknown has " + attrType + " = " + attrName + ";";

        String getQueryStr = "match " + IDUNKNOWN_MATCH + search + " group $id;";
        ObjectNode json = find(getQueryStr);
        Map<String, IdUnknown> test = objectMapper.readValue(json.toString(), new TypeReference<>() {
        });

        return new HashSet<>(test.values());
    }


}


