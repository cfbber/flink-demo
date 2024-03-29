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
import org.apache.shade.kafka.common.protocol.ProtoUtils;
import org.apache.shade.kafka.common.protocol.types.Schema;
import org.apache.shade.kafka.common.protocol.types.Struct;

import java.nio.ByteBuffer;
import java.util.*;

public class ControlledShutdownResponse extends AbstractRequestResponse {

    private static final Schema CURRENT_SCHEMA = ProtoUtils.currentResponseSchema(ApiKeys.CONTROLLED_SHUTDOWN_KEY.id);

    private static final String ERROR_CODE_KEY_NAME = "error_code";
    private static final String PARTITIONS_REMAINING_KEY_NAME = "partitions_remaining";

    private static final String TOPIC_KEY_NAME = "topic";
    private static final String PARTITION_KEY_NAME = "partition";

    /**
     * Possible error codes:
     *
     * UNKNOWN(-1) (this is because IllegalStateException may be thrown in `KafkaController.shutdownBroker`, it would be good to improve this)
     * BROKER_NOT_AVAILABLE(8)
     * STALE_CONTROLLER_EPOCH(11)
     */
    private final short errorCode;

    private final Set<TopicPartition> partitionsRemaining;

    public ControlledShutdownResponse(short errorCode, Set<TopicPartition> partitionsRemaining) {
        super(new org.apache.shade.kafka.common.protocol.types.Struct(CURRENT_SCHEMA));

        struct.set(ERROR_CODE_KEY_NAME, errorCode);

        List<org.apache.shade.kafka.common.protocol.types.Struct> partitionsRemainingList = new ArrayList<>(partitionsRemaining.size());
        for (TopicPartition topicPartition : partitionsRemaining) {
            org.apache.shade.kafka.common.protocol.types.Struct topicPartitionStruct = struct.instance(PARTITIONS_REMAINING_KEY_NAME);
            topicPartitionStruct.set(TOPIC_KEY_NAME, topicPartition.topic());
            topicPartitionStruct.set(PARTITION_KEY_NAME, topicPartition.partition());
            partitionsRemainingList.add(topicPartitionStruct);
        }
        struct.set(PARTITIONS_REMAINING_KEY_NAME, partitionsRemainingList.toArray());

        this.errorCode = errorCode;
        this.partitionsRemaining = partitionsRemaining;
    }

    public ControlledShutdownResponse(org.apache.shade.kafka.common.protocol.types.Struct struct) {
        super(struct);
        errorCode = struct.getShort(ERROR_CODE_KEY_NAME);
        Set<TopicPartition> partitions = new HashSet<>();
        for (Object topicPartitionObj : struct.getArray(PARTITIONS_REMAINING_KEY_NAME)) {
            org.apache.shade.kafka.common.protocol.types.Struct topicPartition = (org.apache.shade.kafka.common.protocol.types.Struct) topicPartitionObj;
            String topic = topicPartition.getString(TOPIC_KEY_NAME);
            int partition = topicPartition.getInt(PARTITION_KEY_NAME);
            partitions.add(new TopicPartition(topic, partition));
        }
        partitionsRemaining = partitions;
    }

    public short errorCode() {
        return errorCode;
    }

    public Set<TopicPartition> partitionsRemaining() {
        return partitionsRemaining;
    }

    public static ControlledShutdownResponse parse(ByteBuffer buffer) {
        return new ControlledShutdownResponse((Struct) CURRENT_SCHEMA.read(buffer));
    }

    public static ControlledShutdownResponse parse(ByteBuffer buffer, int version) {
        return new ControlledShutdownResponse(ProtoUtils.parseResponse(ApiKeys.CONTROLLED_SHUTDOWN_KEY.id, version, buffer));
    }

}
