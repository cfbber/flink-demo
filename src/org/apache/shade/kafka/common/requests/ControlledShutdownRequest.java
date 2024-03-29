/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.shade.kafka.common.requests;

import org.apache.shade.kafka.common.TopicPartition;
import org.apache.shade.kafka.common.protocol.ApiKeys;
import org.apache.shade.kafka.common.protocol.Errors;
import org.apache.shade.kafka.common.protocol.ProtoUtils;
import org.apache.shade.kafka.common.protocol.types.Schema;
import org.apache.shade.kafka.common.protocol.types.Struct;

import java.nio.ByteBuffer;
import java.util.Collections;

public class ControlledShutdownRequest extends AbstractRequest {

    private static final Schema CURRENT_SCHEMA = ProtoUtils.currentRequestSchema(ApiKeys.CONTROLLED_SHUTDOWN_KEY.id);

    private static final String BROKER_ID_KEY_NAME = "broker_id";

    private int brokerId;

    public ControlledShutdownRequest(int brokerId) {
        super(new org.apache.shade.kafka.common.protocol.types.Struct(CURRENT_SCHEMA));
        struct.set(BROKER_ID_KEY_NAME, brokerId);
        this.brokerId = brokerId;
    }

    public ControlledShutdownRequest(org.apache.shade.kafka.common.protocol.types.Struct struct) {
        super(struct);
        brokerId = struct.getInt(BROKER_ID_KEY_NAME);
    }

    @Override
    public AbstractRequestResponse getErrorResponse(int versionId, Throwable e) {
        switch (versionId) {
            case 0:
                throw new IllegalArgumentException(String.format("Version 0 is not supported. It is only supported by " +
                        "the Scala request class for controlled shutdown"));
            case 1:
                return new ControlledShutdownResponse(Errors.forException(e).code(), Collections.<TopicPartition>emptySet());
            default:
                throw new IllegalArgumentException(String.format("Version %d is not valid. Valid versions for %s are 0 to %d",
                        versionId, this.getClass().getSimpleName(), ProtoUtils.latestVersion(ApiKeys.CONTROLLED_SHUTDOWN_KEY.id)));
        }
    }

    public int brokerId() {
        return brokerId;
    }

    public static ControlledShutdownRequest parse(ByteBuffer buffer, int versionId) {
        return new ControlledShutdownRequest(ProtoUtils.parseRequest(ApiKeys.CONTROLLED_SHUTDOWN_KEY.id, versionId, buffer));
    }

    public static ControlledShutdownRequest parse(ByteBuffer buffer) {
        return new ControlledShutdownRequest((Struct) CURRENT_SCHEMA.read(buffer));
    }
}
