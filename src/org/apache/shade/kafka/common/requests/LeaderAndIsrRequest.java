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

import org.apache.shade.kafka.common.TopicPartition;
import org.apache.shade.kafka.common.protocol.ApiKeys;
import org.apache.shade.kafka.common.protocol.Errors;
import org.apache.shade.kafka.common.protocol.ProtoUtils;
import org.apache.shade.kafka.common.protocol.types.Schema;
import org.apache.shade.kafka.common.protocol.types.Struct;

import java.nio.ByteBuffer;
import java.util.*;

public class LeaderAndIsrRequest extends AbstractRequest {

    public static class PartitionState {
        public final int controllerEpoch;
        public final int leader;
        public final int leaderEpoch;
        public final List<Integer> isr;
        public final int zkVersion;
        public final Set<Integer> replicas;

        public PartitionState(int controllerEpoch, int leader, int leaderEpoch, List<Integer> isr, int zkVersion, Set<Integer> replicas) {
            this.controllerEpoch = controllerEpoch;
            this.leader = leader;
            this.leaderEpoch = leaderEpoch;
            this.isr = isr;
            this.zkVersion = zkVersion;
            this.replicas = replicas;
        }

    }

    public static final class EndPoint {
        public final int id;
        public final String host;
        public final int port;

        public EndPoint(int id, String host, int port) {
            this.id = id;
            this.host = host;
            this.port = port;
        }
    }

    private static final Schema CURRENT_SCHEMA = ProtoUtils.currentRequestSchema(ApiKeys.LEADER_AND_ISR.id);

    private static final String CONTROLLER_ID_KEY_NAME = "controller_id";
    private static final String CONTROLLER_EPOCH_KEY_NAME = "controller_epoch";
    private static final String PARTITION_STATES_KEY_NAME = "partition_states";
    private static final String LIVE_LEADERS_KEY_NAME = "live_leaders";

    // partition_states key names
    private static final String TOPIC_KEY_NAME = "topic";
    private static final String PARTITION_KEY_NAME = "partition";
    private static final String LEADER_KEY_NAME = "leader";
    private static final String LEADER_EPOCH_KEY_NAME = "leader_epoch";
    private static final String ISR_KEY_NAME = "isr";
    private static final String ZK_VERSION_KEY_NAME = "zk_version";
    private static final String REPLICAS_KEY_NAME = "replicas";

    // live_leaders key names
    private static final String END_POINT_ID_KEY_NAME = "id";
    private static final String HOST_KEY_NAME = "host";
    private static final String PORT_KEY_NAME = "port";

    private final int controllerId;
    private final int controllerEpoch;
    private final Map<TopicPartition, PartitionState> partitionStates;
    private final Set<EndPoint> liveLeaders;

    public LeaderAndIsrRequest(int controllerId, int controllerEpoch, Map<TopicPartition, PartitionState> partitionStates,
                               Set<EndPoint> liveLeaders) {
        super(new org.apache.shade.kafka.common.protocol.types.Struct(CURRENT_SCHEMA));
        struct.set(CONTROLLER_ID_KEY_NAME, controllerId);
        struct.set(CONTROLLER_EPOCH_KEY_NAME, controllerEpoch);

        List<org.apache.shade.kafka.common.protocol.types.Struct> partitionStatesData = new ArrayList<>(partitionStates.size());
        for (Map.Entry<TopicPartition, PartitionState> entry : partitionStates.entrySet()) {
            org.apache.shade.kafka.common.protocol.types.Struct partitionStateData = struct.instance(PARTITION_STATES_KEY_NAME);
            TopicPartition topicPartition = entry.getKey();
            partitionStateData.set(TOPIC_KEY_NAME, topicPartition.topic());
            partitionStateData.set(PARTITION_KEY_NAME, topicPartition.partition());
            PartitionState partitionState = entry.getValue();
            partitionStateData.set(CONTROLLER_EPOCH_KEY_NAME, partitionState.controllerEpoch);
            partitionStateData.set(LEADER_KEY_NAME, partitionState.leader);
            partitionStateData.set(LEADER_EPOCH_KEY_NAME, partitionState.leaderEpoch);
            partitionStateData.set(ISR_KEY_NAME, partitionState.isr.toArray());
            partitionStateData.set(ZK_VERSION_KEY_NAME, partitionState.zkVersion);
            partitionStateData.set(REPLICAS_KEY_NAME, partitionState.replicas.toArray());
            partitionStatesData.add(partitionStateData);
        }
        struct.set(PARTITION_STATES_KEY_NAME, partitionStatesData.toArray());

        List<org.apache.shade.kafka.common.protocol.types.Struct> leadersData = new ArrayList<>(liveLeaders.size());
        for (EndPoint leader : liveLeaders) {
            org.apache.shade.kafka.common.protocol.types.Struct leaderData = struct.instance(LIVE_LEADERS_KEY_NAME);
            leaderData.set(END_POINT_ID_KEY_NAME, leader.id);
            leaderData.set(HOST_KEY_NAME, leader.host);
            leaderData.set(PORT_KEY_NAME, leader.port);
            leadersData.add(leaderData);
        }
        struct.set(LIVE_LEADERS_KEY_NAME, leadersData.toArray());

        this.controllerId = controllerId;
        this.controllerEpoch = controllerEpoch;
        this.partitionStates = partitionStates;
        this.liveLeaders = liveLeaders;
    }

    public LeaderAndIsrRequest(org.apache.shade.kafka.common.protocol.types.Struct struct) {
        super(struct);

        Map<TopicPartition, PartitionState> partitionStates = new HashMap<>();
        for (Object partitionStateDataObj : struct.getArray(PARTITION_STATES_KEY_NAME)) {
            org.apache.shade.kafka.common.protocol.types.Struct partitionStateData = (org.apache.shade.kafka.common.protocol.types.Struct) partitionStateDataObj;
            String topic = partitionStateData.getString(TOPIC_KEY_NAME);
            int partition = partitionStateData.getInt(PARTITION_KEY_NAME);
            int controllerEpoch = partitionStateData.getInt(CONTROLLER_EPOCH_KEY_NAME);
            int leader = partitionStateData.getInt(LEADER_KEY_NAME);
            int leaderEpoch = partitionStateData.getInt(LEADER_EPOCH_KEY_NAME);

            Object[] isrArray = partitionStateData.getArray(ISR_KEY_NAME);
            List<Integer> isr = new ArrayList<>(isrArray.length);
            for (Object r : isrArray)
                isr.add((Integer) r);

            int zkVersion = partitionStateData.getInt(ZK_VERSION_KEY_NAME);

            Object[] replicasArray = partitionStateData.getArray(REPLICAS_KEY_NAME);
            Set<Integer> replicas = new HashSet<>(replicasArray.length);
            for (Object r : replicasArray)
                replicas.add((Integer) r);

            PartitionState partitionState = new PartitionState(controllerEpoch, leader, leaderEpoch, isr, zkVersion, replicas);
            partitionStates.put(new TopicPartition(topic, partition), partitionState);

        }

        Set<EndPoint> leaders = new HashSet<>();
        for (Object leadersDataObj : struct.getArray(LIVE_LEADERS_KEY_NAME)) {
            org.apache.shade.kafka.common.protocol.types.Struct leadersData = (org.apache.shade.kafka.common.protocol.types.Struct) leadersDataObj;
            int id = leadersData.getInt(END_POINT_ID_KEY_NAME);
            String host = leadersData.getString(HOST_KEY_NAME);
            int port = leadersData.getInt(PORT_KEY_NAME);
            leaders.add(new EndPoint(id, host, port));
        }

        controllerId = struct.getInt(CONTROLLER_ID_KEY_NAME);
        controllerEpoch = struct.getInt(CONTROLLER_EPOCH_KEY_NAME);
        this.partitionStates = partitionStates;
        this.liveLeaders = leaders;
    }

    @Override
    public AbstractRequestResponse getErrorResponse(int versionId, Throwable e) {
        Map<TopicPartition, Short> responses = new HashMap<>(partitionStates.size());
        for (TopicPartition partition : partitionStates.keySet()) {
            responses.put(partition, Errors.forException(e).code());
        }

        switch (versionId) {
            case 0:
                return new LeaderAndIsrResponse(Errors.NONE.code(), responses);
            default:
                throw new IllegalArgumentException(String.format("Version %d is not valid. Valid versions for %s are 0 to %d",
                        versionId, this.getClass().getSimpleName(), ProtoUtils.latestVersion(ApiKeys.LEADER_AND_ISR.id)));
        }
    }

    public int controllerId() {
        return controllerId;
    }

    public int controllerEpoch() {
        return controllerEpoch;
    }

    public Map<TopicPartition, PartitionState> partitionStates() {
        return partitionStates;
    }

    public Set<EndPoint> liveLeaders() {
        return liveLeaders;
    }

    public static LeaderAndIsrRequest parse(ByteBuffer buffer, int versionId) {
        return new LeaderAndIsrRequest(ProtoUtils.parseRequest(ApiKeys.LEADER_AND_ISR.id, versionId, buffer));
    }

    public static LeaderAndIsrRequest parse(ByteBuffer buffer) {
        return new LeaderAndIsrRequest((Struct) CURRENT_SCHEMA.read(buffer));
    }

}
