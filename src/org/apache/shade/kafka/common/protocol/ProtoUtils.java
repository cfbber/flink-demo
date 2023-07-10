/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shade.kafka.common.protocol;

import java.nio.ByteBuffer;

import org.apache.shade.kafka.common.protocol.types.Schema;
import org.apache.shade.kafka.common.protocol.types.Struct;

public class ProtoUtils {

    private static org.apache.shade.kafka.common.protocol.types.Schema schemaFor(org.apache.shade.kafka.common.protocol.types.Schema[][] schemas, int apiKey, int version) {
        if (apiKey < 0 || apiKey > schemas.length)
            throw new IllegalArgumentException("Invalid api key: " + apiKey);
        org.apache.shade.kafka.common.protocol.types.Schema[] versions = schemas[apiKey];
        if (version < 0 || version > versions.length)
            throw new IllegalArgumentException("Invalid version for API key " + apiKey + ": " + version);
        if (versions[version] == null)
            throw new IllegalArgumentException("Unsupported version for API key " + apiKey + ": " + version);
        return versions[version];
    }

    public static short latestVersion(int apiKey) {
        if (apiKey < 0 || apiKey >= org.apache.shade.kafka.common.protocol.Protocol.CURR_VERSION.length)
            throw new IllegalArgumentException("Invalid api key: " + apiKey);
        return org.apache.shade.kafka.common.protocol.Protocol.CURR_VERSION[apiKey];
    }

    public static org.apache.shade.kafka.common.protocol.types.Schema requestSchema(int apiKey, int version) {
        return schemaFor(org.apache.shade.kafka.common.protocol.Protocol.REQUESTS, apiKey, version);
    }

    public static org.apache.shade.kafka.common.protocol.types.Schema currentRequestSchema(int apiKey) {
        return requestSchema(apiKey, latestVersion(apiKey));
    }

    public static org.apache.shade.kafka.common.protocol.types.Schema responseSchema(int apiKey, int version) {
        return schemaFor(org.apache.shade.kafka.common.protocol.Protocol.RESPONSES, apiKey, version);
    }

    public static Schema currentResponseSchema(int apiKey) {
        return schemaFor(Protocol.RESPONSES, apiKey, latestVersion(apiKey));
    }

    public static org.apache.shade.kafka.common.protocol.types.Struct parseRequest(int apiKey, int version, ByteBuffer buffer) {
        return (org.apache.shade.kafka.common.protocol.types.Struct) requestSchema(apiKey, version).read(buffer);
    }

    public static org.apache.shade.kafka.common.protocol.types.Struct parseResponse(int apiKey, ByteBuffer buffer) {
        return (org.apache.shade.kafka.common.protocol.types.Struct) currentResponseSchema(apiKey).read(buffer);
    }

    public static org.apache.shade.kafka.common.protocol.types.Struct parseResponse(int apiKey, int version, ByteBuffer buffer) {
        return (Struct) responseSchema(apiKey, version).read(buffer);
    }

}
