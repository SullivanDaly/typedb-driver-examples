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
import org.example.model.ThreatActor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ThreatActorDAO {
    @Autowired
    private TypeDBSessionWrapper db;

    private String typeString;

    protected static final String TA_MATCH =
            "  $ta isa threat_actor, has stix_id $id ,has $attribute;" +
                    "$attribute isa! $j; ";

    public ThreatActorDAO(TypeDBSessionWrapper db) {
        this.db = db;
        ThreatActor tempThreatActor = new ThreatActor();
        typeString = tempThreatActor.getTypeString();
    }

    private ObjectNode getJSON(String getQueryStr) {
        return db.getAllJSON(getQueryStr);
    }
    
    public ObjectNode getAllJSON() {
        var getQueryStr = "match " + TA_MATCH + "group $id;";
        return getJSON(getQueryStr);
    }

    public String getAllString() {
        return getAllJSON().toString();
    }

    public Set<ThreatActor> getAllBeans() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String getQueryStr = "match " + TA_MATCH + " group $id;";
        ObjectNode json = getJSON(getQueryStr);
        Map<String, ThreatActor> test = objectMapper.readValue(json.toString(), new TypeReference<Map<String, ThreatActor>>(){});
        Set<ThreatActor> result = new HashSet<>(test.values());

        return result;
    }
    public ObjectNode getSearchJSON(String type, String name) {

        if (typeString.contains(" " + type + ";")){
            name = "\"" + name + "\"";
        }

        String search = "$ta has " + type + " = " + name + ";";
        var getQueryStr = "match " + TA_MATCH + search + "group $id;";
        System.out.println(getQueryStr);

        return getJSON(getQueryStr);
    }

    public String getSearchString(String type, String name) {
        return getSearchJSON(type, name).toString();
    }

    public Set<ThreatActor> getSearchBeans(String type, String name) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        if (typeString.contains(" " + type + ";")){
            name = "\"" + name + "\"";
        }

        String search = "$ta has " + type + " = " + name + ";";

        String getQueryStr = "match " + TA_MATCH + search + " group $id;";
        System.out.println(getQueryStr);

        ObjectNode json = getJSON(getQueryStr);
        Map<String, ThreatActor> test = objectMapper.readValue(json.toString(), new TypeReference<Map<String, ThreatActor>>(){});
        Set<ThreatActor> result = new HashSet<>(test.values());

        return result;
    }



}
