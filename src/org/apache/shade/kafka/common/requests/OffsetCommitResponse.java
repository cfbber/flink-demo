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
import org.apache.shade.kafka.common.utils.CollectionUtils;
import org.apache.shade.kafka.common.protocol.types.Schema;
import org.apache.shade.kafka.common.protocol.types.Struct;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OffsetCommitResponse extends AbstractRequestResponse {
    
    private static final Schema CURRENT_SCHEMA = ProtoUtils.currentResponseSchema(ApiKeys.OFFSET_COMMIT.id);
    private static final String RESPONSES_KEY_NAME = "responses";

    // topic level fields
    private static final String TOPIC_KEY_NAME = "topic";
    private static final String PARTITIONS_KEY_NAME = "partition_responses";

    // partition level fields
    private static final String PARTITION_KEY_NAME = "partition";
    private static final String ERROR_CODE_KEY_NAME = "error_code";

    /**
     * Possible error codes:
     *
     * OFFSET_METADATA_TOO_LARGE (12)
     * GROUP_LOAD_IN_PROGRESS (14)
     * GROUP_COORDINATOR_NOT_AVAILABLE (15)
     * NOT_COORDINATOR_FOR_GROUP (16)
     * ILLEGAL_GENERATION (22)
     * UNKNOWN_MEMBER_ID (25)
     * REBALANCE_IN_PROGRESS (27)
     * INVALID_COMMIT_OFFSET_SIZE (28)
     * TOPIC_AUTHORIZATION_FAILED (29)
     * GROUP_AUTHORIZATION_FAILED (30)
     */

    private final Map<TopicPartition, Short> responseData;

    public OffsetCommitResponse(Map<TopicPartition, Short> responseData) {
        super(new org.apache.shade.kafka.common.protocol.types.Struct(CURRENT_SCHEMA));

        Map<String, Map<Integer, Short>> topicsData = CollectionUtils.groupDataByTopic(responseData);

        List<org.apache.shade.kafka.common.protocol.types.Struct> topicArray = new ArrayList<org.apache.shade.kafka.common.protocol.types.Struct>();
        for (Map.Entry<String, Map<Integer, Short>> entries: topicsData.entrySet()) {
            org.apache.shade.kafka.common.protocol.types.Struct topicData = struct.instance(RESPONSES_KEY_NAME);
            topicData.set(TOPIC_KEY_NAME, entries.getKey());
            List<org.apache.shade.kafka.common.protocol.types.Struct> partitionArray = new ArrayList<org.apache.shade.kafka.common.protocol.types.Struct>();
            for (Map.Entry<Integer, Short> partitionEntry : entries.getValue().entrySet()) {
                org.apache.shade.kafka.common.protocol.types.Struct partitionData = topicData.instance(PARTITIONS_KEY_NAME);
                partitionData.set(PARTITION_KEY_NAME, partitionEntry.getKey());
                partitionData.set(ERROR_CODE_KEY_NAME, partitionEntry.getValue());
                partitionArray.add(partitionData);
            }
            topicData.set(PARTITIONS_KEY_NAME, partitionArray.toArray());
            topicArray.add(topicData);
        }
        struct.set(RESPONSES_KEY_NAME, topicArray.toArray());
        this.responseData = responseData;
    }

    public OffsetCommitResponse(org.apache.shade.kafka.common.protocol.types.Struct struct) {
        super(struct);
        responseData = new HashMap<TopicPartition, Short>();
        for (Object topicResponseObj : struct.getArray(RESPONSES_KEY_NAME)) {
            org.apache.shade.kafka.common.protocol.types.Struct topicResponse = (org.apache.shade.kafka.common.protocol.types.Struct) topicResponseObj;
            String topic = topicResponse.getString(TOPIC_KEY_NAME);
            for (Object partitionResponseObj : topicResponse.getArray(PARTITIONS_KEY_NAME)) {
                org.apache.shade.kafka.common.protocol.types.Struct partitionResponse = (org.apache.shade.kafka.common.protocol.types.Struct) partitionResponseObj;
                int partition = partitionResponse.getInt(PARTITION_KEY_NAME);
                short errorCode = partitionResponse.getShort(ERROR_CODE_KEY_NAME);
                responseData.put(new TopicPartition(topic, partition), errorCode);
            }
        }
    }

    public Map<TopicPartition, Short> responseData() {
        return responseData;
    }

    public static OffsetCommitResponse parse(ByteBuffer buffer) {
        return new OffsetCommitResponse((Struct) CURRENT_SCHEMA.read(buffer));
    }
}
