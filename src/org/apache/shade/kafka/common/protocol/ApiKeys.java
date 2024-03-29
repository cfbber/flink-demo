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

/**
 * Identifiers for all the Kafka APIs
 */
public enum ApiKeys {
    PRODUCE(0, "Produce"),
    FETCH(1, "Fetch"),
    LIST_OFFSETS(2, "Offsets"),
    METADATA(3, "Metadata"),
    LEADER_AND_ISR(4, "LeaderAndIsr"),
    STOP_REPLICA(5, "StopReplica"),
    UPDATE_METADATA_KEY(6, "UpdateMetadata"),
    CONTROLLED_SHUTDOWN_KEY(7, "ControlledShutdown"),
    OFFSET_COMMIT(8, "OffsetCommit"),
    OFFSET_FETCH(9, "OffsetFetch"),
    GROUP_COORDINATOR(10, "GroupCoordinator"),
    JOIN_GROUP(11, "JoinGroup"),
    HEARTBEAT(12, "Heartbeat"),
    LEAVE_GROUP(13, "LeaveGroup"),
    SYNC_GROUP(14, "SyncGroup"),
    DESCRIBE_GROUPS(15, "DescribeGroups"),
    LIST_GROUPS(16, "ListGroups");

    private static ApiKeys[] codeToType;
    public static final int MAX_API_KEY;

    static {
        int maxKey = -1;
        for (ApiKeys key : ApiKeys.values()) {
            maxKey = Math.max(maxKey, key.id);
        }
        codeToType = new ApiKeys[maxKey + 1];
        for (ApiKeys key : ApiKeys.values()) {
            codeToType[key.id] = key;
        }
        MAX_API_KEY = maxKey;
    }

    /** the permanent and immutable id of an API--this can't change ever */
    public final short id;

    /** an english description of the api--this is for debugging and can change */
    public final String name;

    private ApiKeys(int id, String name) {
        this.id = (short) id;
        this.name = name;
    }

    public static ApiKeys forId(int id) {
        return codeToType[id];
    }
}
