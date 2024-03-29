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
import java.util.*;

public class StopReplicaResponse extends AbstractRequestResponse {
    private static final Schema CURRENT_SCHEMA = ProtoUtils.currentResponseSchema(ApiKeys.STOP_REPLICA.id);

    private static final String ERROR_CODE_KEY_NAME = "error_code";
    private static final String PARTITIONS_KEY_NAME = "partitions";

    private static final String PARTITIONS_TOPIC_KEY_NAME = "topic";
    private static final String PARTITIONS_PARTITION_KEY_NAME = "partition";
    private static final String PARTITIONS_ERROR_CODE_KEY_NAME = "error_code";

    private final Map<TopicPartition, Short> responses;
    private final short errorCode;

    /**
     * Possible error code:
     *
     * STALE_CONTROLLER_EPOCH (11)
     */

    public StopReplicaResponse(Map<TopicPartition, Short> responses) {
        this(Errors.NONE.code(), responses);
    }

    public StopReplicaResponse(short errorCode, Map<TopicPartition, Short> responses) {
        super(new org.apache.shade.kafka.common.protocol.types.Struct(CURRENT_SCHEMA));

        struct.set(ERROR_CODE_KEY_NAME, errorCode);

        List<org.apache.shade.kafka.common.protocol.types.Struct> responseDatas = new ArrayList<>(responses.size());
        for (Map.Entry<TopicPartition, Short> response : responses.entrySet()) {
            org.apache.shade.kafka.common.protocol.types.Struct partitionData = struct.instance(PARTITIONS_KEY_NAME);
            TopicPartition partition = response.getKey();
            partitionData.set(PARTITIONS_TOPIC_KEY_NAME, partition.topic());
            partitionData.set(PARTITIONS_PARTITION_KEY_NAME, partition.partition());
            partitionData.set(PARTITIONS_ERROR_CODE_KEY_NAME, response.getValue());
            responseDatas.add(partitionData);
        }

        struct.set(PARTITIONS_KEY_NAME, responseDatas.toArray());
        struct.set(ERROR_CODE_KEY_NAME, errorCode);

        this.responses = responses;
        this.errorCode = errorCode;
    }

    public StopReplicaResponse(org.apache.shade.kafka.common.protocol.types.Struct struct) {
        super(struct);

        responses = new HashMap<>();
        for (Object responseDataObj : struct.getArray(PARTITIONS_KEY_NAME)) {
            org.apache.shade.kafka.common.protocol.types.Struct responseData = (org.apache.shade.kafka.common.protocol.types.Struct) responseDataObj;
            String topic = responseData.getString(PARTITIONS_TOPIC_KEY_NAME);
            int partition = responseData.getInt(PARTITIONS_PARTITION_KEY_NAME);
            short errorCode = responseData.getShort(PARTITIONS_ERROR_CODE_KEY_NAME);
            responses.put(new TopicPartition(topic, partition), errorCode);
        }

        errorCode = struct.getShort(ERROR_CODE_KEY_NAME);
    }

    public Map<TopicPartition, Short> responses() {
        return responses;
    }

    public short errorCode() {
        return errorCode;
    }

    public static StopReplicaResponse parse(ByteBuffer buffer, int versionId) {
        return new StopReplicaResponse(ProtoUtils.parseRequest(ApiKeys.STOP_REPLICA.id, versionId, buffer));
    }

    public static StopReplicaResponse parse(ByteBuffer buffer) {
        return new StopReplicaResponse((Struct) CURRENT_SCHEMA.read(buffer));
    }
}
