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
package org.apache.shade.kafka.common.requests;

import static org.apache.shade.kafka.common.protocol.Protocol.RESPONSE_HEADER;

import java.nio.ByteBuffer;

import org.apache.shade.kafka.common.protocol.Protocol;
import org.apache.shade.kafka.common.protocol.types.Field;
import org.apache.shade.kafka.common.protocol.types.Struct;


/**
 * A response header in the kafka protocol.
 */
public class ResponseHeader extends AbstractRequestResponse {

    private static final Field CORRELATION_KEY_FIELD = RESPONSE_HEADER.get("correlation_id");

    private final int correlationId;

    public ResponseHeader(org.apache.shade.kafka.common.protocol.types.Struct header) {
        super(header);
        correlationId = struct.getInt(CORRELATION_KEY_FIELD);
    }

    public ResponseHeader(int correlationId) {
        super(new org.apache.shade.kafka.common.protocol.types.Struct(Protocol.RESPONSE_HEADER));
        struct.set(CORRELATION_KEY_FIELD, correlationId);
        this.correlationId = correlationId;
    }

    public int correlationId() {
        return correlationId;
    }

    public static ResponseHeader parse(ByteBuffer buffer) {
        return new ResponseHeader((Struct) Protocol.RESPONSE_HEADER.read(buffer));
    }

}
