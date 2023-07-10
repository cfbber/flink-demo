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

import org.apache.shade.kafka.common.protocol.types.ArrayOf;
import org.apache.shade.kafka.common.protocol.types.Field;
import org.apache.shade.kafka.common.protocol.types.Schema;
import org.apache.shade.kafka.common.protocol.types.Type;

public class Protocol {

    public static final org.apache.shade.kafka.common.protocol.types.Schema REQUEST_HEADER = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("api_key", Type.INT16, "The id of the request type."),
                                                           new org.apache.shade.kafka.common.protocol.types.Field("api_version", Type.INT16, "The version of the API."),
                                                           new org.apache.shade.kafka.common.protocol.types.Field("correlation_id",
                                                                     Type.INT32,
                                                                     "A user-supplied integer value that will be passed back with the response"),
                                                           new org.apache.shade.kafka.common.protocol.types.Field("client_id",
                                                                     Type.STRING,
                                                                     "A user specified identifier for the client making the request."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema RESPONSE_HEADER = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("correlation_id",
                                                                      Type.INT32,
                                                                      "The user-supplied value passed in with the request"));

    /* Metadata api */

    public static final org.apache.shade.kafka.common.protocol.types.Schema METADATA_REQUEST_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topics",
                                                                          new org.apache.shade.kafka.common.protocol.types.ArrayOf(Type.STRING),
                                                                          "An array of topics to fetch metadata for. If no topics are specified fetch metadata for all topics."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema BROKER = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("node_id", Type.INT32, "The broker id."),
                                                   new org.apache.shade.kafka.common.protocol.types.Field("host", Type.STRING, "The hostname of the broker."),
                                                   new org.apache.shade.kafka.common.protocol.types.Field("port",
                                                             Type.INT32,
                                                             "The port on which the broker accepts requests."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema PARTITION_METADATA_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("partition_error_code",
                                                                            Type.INT16,
                                                                            "The error code for the partition, if any."),
                                                                  new org.apache.shade.kafka.common.protocol.types.Field("partition_id",
                                                                            Type.INT32,
                                                                            "The id of the partition."),
                                                                  new org.apache.shade.kafka.common.protocol.types.Field("leader",
                                                                            Type.INT32,
                                                                            "The id of the broker acting as leader for this partition."),
                                                                  new org.apache.shade.kafka.common.protocol.types.Field("replicas",
                                                                            new org.apache.shade.kafka.common.protocol.types.ArrayOf(Type.INT32),
                                                                            "The set of all nodes that host this partition."),
                                                                  new org.apache.shade.kafka.common.protocol.types.Field("isr",
                                                                            new org.apache.shade.kafka.common.protocol.types.ArrayOf(Type.INT32),
                                                                            "The set of nodes that are in sync with the leader for this partition."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema TOPIC_METADATA_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic_error_code",
                                                                        Type.INT16,
                                                                        "The error code for the given topic."),
                                                              new org.apache.shade.kafka.common.protocol.types.Field("topic", Type.STRING, "The name of the topic"),
                                                              new org.apache.shade.kafka.common.protocol.types.Field("partition_metadata",
                                                                        new org.apache.shade.kafka.common.protocol.types.ArrayOf(PARTITION_METADATA_V0),
                                                                        "Metadata for each partition of the topic."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema METADATA_RESPONSE_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("brokers",
                                                                           new org.apache.shade.kafka.common.protocol.types.ArrayOf(BROKER),
                                                                           "Host and port information for all brokers."),
                                                                 new org.apache.shade.kafka.common.protocol.types.Field("topic_metadata",
                                                                           new org.apache.shade.kafka.common.protocol.types.ArrayOf(TOPIC_METADATA_V0)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema[] METADATA_REQUEST = new org.apache.shade.kafka.common.protocol.types.Schema[] {METADATA_REQUEST_V0};
    public static final org.apache.shade.kafka.common.protocol.types.Schema[] METADATA_RESPONSE = new org.apache.shade.kafka.common.protocol.types.Schema[] {METADATA_RESPONSE_V0};

    /* Produce api */

    public static final org.apache.shade.kafka.common.protocol.types.Schema TOPIC_PRODUCE_DATA_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic", Type.STRING),
                                                                  new org.apache.shade.kafka.common.protocol.types.Field("data", new org.apache.shade.kafka.common.protocol.types.ArrayOf(new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("partition", Type.INT32),
                                                                                                     new org.apache.shade.kafka.common.protocol.types.Field("record_set", Type.BYTES)))));

    public static final org.apache.shade.kafka.common.protocol.types.Schema PRODUCE_REQUEST_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("acks",
                                                                   Type.INT16,
                                                                   "The number of nodes that should replicate the produce before returning. -1 indicates the full ISR."),
                                                               new org.apache.shade.kafka.common.protocol.types.Field("timeout", Type.INT32, "The time to await a response in ms."),
                                                               new org.apache.shade.kafka.common.protocol.types.Field("topic_data", new org.apache.shade.kafka.common.protocol.types.ArrayOf(TOPIC_PRODUCE_DATA_V0)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema PRODUCE_RESPONSE_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("responses",
                                                                    new org.apache.shade.kafka.common.protocol.types.ArrayOf(new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic", Type.STRING),
                                                                                           new org.apache.shade.kafka.common.protocol.types.Field("partition_responses",
                                                                                                     new org.apache.shade.kafka.common.protocol.types.ArrayOf(new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("partition",
                                                                                                                                      Type.INT32),
                                                                                                                            new org.apache.shade.kafka.common.protocol.types.Field("error_code",
                                                                                                                                      Type.INT16),
                                                                                                                            new org.apache.shade.kafka.common.protocol.types.Field("base_offset",
                                                                                                                                      Type.INT64))))))));
    public static final org.apache.shade.kafka.common.protocol.types.Schema PRODUCE_REQUEST_V1 = PRODUCE_REQUEST_V0;

    public static final org.apache.shade.kafka.common.protocol.types.Schema PRODUCE_RESPONSE_V1 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("responses",
                                                                          new org.apache.shade.kafka.common.protocol.types.ArrayOf(new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic", Type.STRING),
                                                                                                 new org.apache.shade.kafka.common.protocol.types.Field("partition_responses",
                                                                                                           new org.apache.shade.kafka.common.protocol.types.ArrayOf(new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("partition",
                                                                                                                                            Type.INT32),
                                                                                                                                  new org.apache.shade.kafka.common.protocol.types.Field("error_code",
                                                                                                                                            Type.INT16),
                                                                                                                                  new org.apache.shade.kafka.common.protocol.types.Field("base_offset",
                                                                                                                                            Type.INT64))))))),
                                                                new org.apache.shade.kafka.common.protocol.types.Field("throttle_time_ms",
                                                                          Type.INT32,
                                                                          "Duration in milliseconds for which the request was throttled" +
                                                                              " due to quota violation. (Zero if the request did not violate any quota.)",
                                                                          0));

    public static final org.apache.shade.kafka.common.protocol.types.Schema[] PRODUCE_REQUEST = new org.apache.shade.kafka.common.protocol.types.Schema[] {PRODUCE_REQUEST_V0, PRODUCE_REQUEST_V1};
    public static final org.apache.shade.kafka.common.protocol.types.Schema[] PRODUCE_RESPONSE = new org.apache.shade.kafka.common.protocol.types.Schema[] {PRODUCE_RESPONSE_V0, PRODUCE_RESPONSE_V1};

    /* Offset commit api */
    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_COMMIT_REQUEST_PARTITION_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("partition",
                                                                                         Type.INT32,
                                                                                         "Topic partition id."),
                                                                               new org.apache.shade.kafka.common.protocol.types.Field("offset",
                                                                                         Type.INT64,
                                                                                         "Message offset to be committed."),
                                                                               new org.apache.shade.kafka.common.protocol.types.Field("metadata",
                                                                                         Type.STRING,
                                                                                         "Any associated metadata the client wants to keep."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_COMMIT_REQUEST_PARTITION_V1 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("partition",
                                                                                         Type.INT32,
                                                                                         "Topic partition id."),
                                                                               new org.apache.shade.kafka.common.protocol.types.Field("offset",
                                                                                         Type.INT64,
                                                                                         "Message offset to be committed."),
                                                                               new org.apache.shade.kafka.common.protocol.types.Field("timestamp",
                                                                                         Type.INT64,
                                                                                         "Timestamp of the commit"),
                                                                               new org.apache.shade.kafka.common.protocol.types.Field("metadata",
                                                                                         Type.STRING,
                                                                                         "Any associated metadata the client wants to keep."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_COMMIT_REQUEST_PARTITION_V2 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("partition",
                                                                                         Type.INT32,
                                                                                         "Topic partition id."),
                                                                               new org.apache.shade.kafka.common.protocol.types.Field("offset",
                                                                                         Type.INT64,
                                                                                         "Message offset to be committed."),
                                                                               new org.apache.shade.kafka.common.protocol.types.Field("metadata",
                                                                                         Type.STRING,
                                                                                         "Any associated metadata the client wants to keep."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_COMMIT_REQUEST_TOPIC_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic",
                                                                                     Type.STRING,
                                                                                     "Topic to commit."),
                                                                           new org.apache.shade.kafka.common.protocol.types.Field("partitions",
                                                                                     new org.apache.shade.kafka.common.protocol.types.ArrayOf(OFFSET_COMMIT_REQUEST_PARTITION_V0),
                                                                                     "Partitions to commit offsets."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_COMMIT_REQUEST_TOPIC_V1 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic",
                                                                                     Type.STRING,
                                                                                     "Topic to commit."),
                                                                           new org.apache.shade.kafka.common.protocol.types.Field("partitions",
                                                                                     new org.apache.shade.kafka.common.protocol.types.ArrayOf(OFFSET_COMMIT_REQUEST_PARTITION_V1),
                                                                                     "Partitions to commit offsets."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_COMMIT_REQUEST_TOPIC_V2 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic",
                                                                                     Type.STRING,
                                                                                     "Topic to commit."),
                                                                           new org.apache.shade.kafka.common.protocol.types.Field("partitions",
                                                                                     new org.apache.shade.kafka.common.protocol.types.ArrayOf(OFFSET_COMMIT_REQUEST_PARTITION_V2),
                                                                                     "Partitions to commit offsets."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_COMMIT_REQUEST_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("group_id",
                                                                               Type.STRING,
                                                                               "The group id."),
                                                                     new org.apache.shade.kafka.common.protocol.types.Field("topics",
                                                                               new org.apache.shade.kafka.common.protocol.types.ArrayOf(OFFSET_COMMIT_REQUEST_TOPIC_V0),
                                                                               "Topics to commit offsets."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_COMMIT_REQUEST_V1 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("group_id",
                                                                               Type.STRING,
                                                                               "The group id."),
                                                                     new org.apache.shade.kafka.common.protocol.types.Field("group_generation_id",
                                                                               Type.INT32,
                                                                               "The generation of the group."),
                                                                     new org.apache.shade.kafka.common.protocol.types.Field("member_id",
                                                                               Type.STRING,
                                                                               "The member id assigned by the group coordinator."),
                                                                     new org.apache.shade.kafka.common.protocol.types.Field("topics",
                                                                               new org.apache.shade.kafka.common.protocol.types.ArrayOf(OFFSET_COMMIT_REQUEST_TOPIC_V1),
                                                                               "Topics to commit offsets."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_COMMIT_REQUEST_V2 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("group_id",
                                                                               Type.STRING,
                                                                               "The group id."),
                                                                     new org.apache.shade.kafka.common.protocol.types.Field("group_generation_id",
                                                                               Type.INT32,
                                                                               "The generation of the consumer group."),
                                                                     new org.apache.shade.kafka.common.protocol.types.Field("member_id",
                                                                               Type.STRING,
                                                                               "The consumer id assigned by the group coordinator."),
                                                                     new org.apache.shade.kafka.common.protocol.types.Field("retention_time",
                                                                               Type.INT64,
                                                                               "Time period in ms to retain the offset."),
                                                                     new org.apache.shade.kafka.common.protocol.types.Field("topics",
                                                                               new org.apache.shade.kafka.common.protocol.types.ArrayOf(OFFSET_COMMIT_REQUEST_TOPIC_V2),
                                                                               "Topics to commit offsets."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_COMMIT_RESPONSE_PARTITION_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("partition",
                                                                                          Type.INT32,
                                                                                          "Topic partition id."),
                                                                                new org.apache.shade.kafka.common.protocol.types.Field("error_code",
                                                                                          Type.INT16));

    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_COMMIT_RESPONSE_TOPIC_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic", Type.STRING),
                                                                            new org.apache.shade.kafka.common.protocol.types.Field("partition_responses",
                                                                                      new org.apache.shade.kafka.common.protocol.types.ArrayOf(OFFSET_COMMIT_RESPONSE_PARTITION_V0)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_COMMIT_RESPONSE_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("responses",
                                                                                new org.apache.shade.kafka.common.protocol.types.ArrayOf(OFFSET_COMMIT_RESPONSE_TOPIC_V0)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema[] OFFSET_COMMIT_REQUEST = new org.apache.shade.kafka.common.protocol.types.Schema[] {OFFSET_COMMIT_REQUEST_V0, OFFSET_COMMIT_REQUEST_V1, OFFSET_COMMIT_REQUEST_V2};

    /* The response types for V0, V1 and V2 of OFFSET_COMMIT_REQUEST are the same. */
    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_COMMIT_RESPONSE_V1 = OFFSET_COMMIT_RESPONSE_V0;
    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_COMMIT_RESPONSE_V2 = OFFSET_COMMIT_RESPONSE_V0;

    public static final org.apache.shade.kafka.common.protocol.types.Schema[] OFFSET_COMMIT_RESPONSE = new org.apache.shade.kafka.common.protocol.types.Schema[] {OFFSET_COMMIT_RESPONSE_V0, OFFSET_COMMIT_RESPONSE_V1, OFFSET_COMMIT_RESPONSE_V2};

    /* Offset fetch api */

    /*
     * Wire formats of version 0 and 1 are the same, but with different functionality.
     * Version 0 will read the offsets from ZK;
     * Version 1 will read the offsets from Kafka.
     */
    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_FETCH_REQUEST_PARTITION_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("partition",
                                                                                        Type.INT32,
                                                                                        "Topic partition id."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_FETCH_REQUEST_TOPIC_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic",
                                                                                    Type.STRING,
                                                                                    "Topic to fetch offset."),
                                                                          new org.apache.shade.kafka.common.protocol.types.Field("partitions",
                                                                                    new org.apache.shade.kafka.common.protocol.types.ArrayOf(OFFSET_FETCH_REQUEST_PARTITION_V0),
                                                                                    "Partitions to fetch offsets."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_FETCH_REQUEST_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("group_id",
                                                                              Type.STRING,
                                                                              "The consumer group id."),
                                                                    new org.apache.shade.kafka.common.protocol.types.Field("topics",
                                                                              new org.apache.shade.kafka.common.protocol.types.ArrayOf(OFFSET_FETCH_REQUEST_TOPIC_V0),
                                                                              "Topics to fetch offsets."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_FETCH_RESPONSE_PARTITION_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("partition",
                                                                                         Type.INT32,
                                                                                         "Topic partition id."),
                                                                               new org.apache.shade.kafka.common.protocol.types.Field("offset",
                                                                                         Type.INT64,
                                                                                         "Last committed message offset."),
                                                                               new org.apache.shade.kafka.common.protocol.types.Field("metadata",
                                                                                         Type.STRING,
                                                                                         "Any associated metadata the client wants to keep."),
                                                                               new org.apache.shade.kafka.common.protocol.types.Field("error_code", Type.INT16));

    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_FETCH_RESPONSE_TOPIC_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic", Type.STRING),
                                                                           new org.apache.shade.kafka.common.protocol.types.Field("partition_responses",
                                                                                     new org.apache.shade.kafka.common.protocol.types.ArrayOf(OFFSET_FETCH_RESPONSE_PARTITION_V0)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_FETCH_RESPONSE_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("responses",
                                                                               new org.apache.shade.kafka.common.protocol.types.ArrayOf(OFFSET_FETCH_RESPONSE_TOPIC_V0)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_FETCH_REQUEST_V1 = OFFSET_FETCH_REQUEST_V0;
    public static final org.apache.shade.kafka.common.protocol.types.Schema OFFSET_FETCH_RESPONSE_V1 = OFFSET_FETCH_RESPONSE_V0;

    public static final org.apache.shade.kafka.common.protocol.types.Schema[] OFFSET_FETCH_REQUEST = new org.apache.shade.kafka.common.protocol.types.Schema[] {OFFSET_FETCH_REQUEST_V0, OFFSET_FETCH_REQUEST_V1};
    public static final org.apache.shade.kafka.common.protocol.types.Schema[] OFFSET_FETCH_RESPONSE = new org.apache.shade.kafka.common.protocol.types.Schema[] {OFFSET_FETCH_RESPONSE_V0, OFFSET_FETCH_RESPONSE_V1};

    /* List offset api */
    public static final org.apache.shade.kafka.common.protocol.types.Schema LIST_OFFSET_REQUEST_PARTITION_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("partition",
                                                                                       Type.INT32,
                                                                                       "Topic partition id."),
                                                                             new org.apache.shade.kafka.common.protocol.types.Field("timestamp", Type.INT64, "Timestamp."),
                                                                             new org.apache.shade.kafka.common.protocol.types.Field("max_num_offsets",
                                                                                       Type.INT32,
                                                                                       "Maximum offsets to return."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema LIST_OFFSET_REQUEST_TOPIC_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic",
                                                                                   Type.STRING,
                                                                                   "Topic to list offset."),
                                                                         new org.apache.shade.kafka.common.protocol.types.Field("partitions",
                                                                                   new org.apache.shade.kafka.common.protocol.types.ArrayOf(LIST_OFFSET_REQUEST_PARTITION_V0),
                                                                                   "Partitions to list offset."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema LIST_OFFSET_REQUEST_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("replica_id",
                                                                             Type.INT32,
                                                                             "Broker id of the follower. For normal consumers, use -1."),
                                                                   new org.apache.shade.kafka.common.protocol.types.Field("topics",
                                                                             new org.apache.shade.kafka.common.protocol.types.ArrayOf(LIST_OFFSET_REQUEST_TOPIC_V0),
                                                                             "Topics to list offsets."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema LIST_OFFSET_RESPONSE_PARTITION_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("partition",
                                                                                        Type.INT32,
                                                                                        "Topic partition id."),
                                                                              new org.apache.shade.kafka.common.protocol.types.Field("error_code", Type.INT16),
                                                                              new org.apache.shade.kafka.common.protocol.types.Field("offsets",
                                                                                        new org.apache.shade.kafka.common.protocol.types.ArrayOf(Type.INT64),
                                                                                        "A list of offsets."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema LIST_OFFSET_RESPONSE_TOPIC_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic", Type.STRING),
                                                                          new org.apache.shade.kafka.common.protocol.types.Field("partition_responses",
                                                                                    new org.apache.shade.kafka.common.protocol.types.ArrayOf(LIST_OFFSET_RESPONSE_PARTITION_V0)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema LIST_OFFSET_RESPONSE_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("responses",
                                                                              new org.apache.shade.kafka.common.protocol.types.ArrayOf(LIST_OFFSET_RESPONSE_TOPIC_V0)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema[] LIST_OFFSET_REQUEST = new org.apache.shade.kafka.common.protocol.types.Schema[] {LIST_OFFSET_REQUEST_V0};
    public static final org.apache.shade.kafka.common.protocol.types.Schema[] LIST_OFFSET_RESPONSE = new org.apache.shade.kafka.common.protocol.types.Schema[] {LIST_OFFSET_RESPONSE_V0};

    /* Fetch api */
    public static final org.apache.shade.kafka.common.protocol.types.Schema FETCH_REQUEST_PARTITION_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("partition",
                                                                                 Type.INT32,
                                                                                 "Topic partition id."),
                                                                       new org.apache.shade.kafka.common.protocol.types.Field("fetch_offset",
                                                                                 Type.INT64,
                                                                                 "Message offset."),
                                                                       new org.apache.shade.kafka.common.protocol.types.Field("max_bytes",
                                                                                 Type.INT32,
                                                                                 "Maximum bytes to fetch."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema FETCH_REQUEST_TOPIC_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic", Type.STRING, "Topic to fetch."),
                                                                   new org.apache.shade.kafka.common.protocol.types.Field("partitions",
                                                                             new org.apache.shade.kafka.common.protocol.types.ArrayOf(FETCH_REQUEST_PARTITION_V0),
                                                                             "Partitions to fetch."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema FETCH_REQUEST_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("replica_id",
                                                                       Type.INT32,
                                                                       "Broker id of the follower. For normal consumers, use -1."),
                                                             new org.apache.shade.kafka.common.protocol.types.Field("max_wait_time",
                                                                       Type.INT32,
                                                                       "Maximum time in ms to wait for the response."),
                                                             new org.apache.shade.kafka.common.protocol.types.Field("min_bytes",
                                                                       Type.INT32,
                                                                       "Minimum bytes to accumulate in the response."),
                                                             new org.apache.shade.kafka.common.protocol.types.Field("topics",
                                                                       new org.apache.shade.kafka.common.protocol.types.ArrayOf(FETCH_REQUEST_TOPIC_V0),
                                                                       "Topics to fetch."));

    // The V1 Fetch Request body is the same as V0.
    // Only the version number is incremented to indicate a newer client
    public static final org.apache.shade.kafka.common.protocol.types.Schema FETCH_REQUEST_V1 = FETCH_REQUEST_V0;
    public static final org.apache.shade.kafka.common.protocol.types.Schema FETCH_RESPONSE_PARTITION_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("partition",
                                                                                  Type.INT32,
                                                                                  "Topic partition id."),
                                                                        new org.apache.shade.kafka.common.protocol.types.Field("error_code", Type.INT16),
                                                                        new org.apache.shade.kafka.common.protocol.types.Field("high_watermark",
                                                                                  Type.INT64,
                                                                                  "Last committed offset."),
                                                                        new org.apache.shade.kafka.common.protocol.types.Field("record_set", Type.BYTES));

    public static final org.apache.shade.kafka.common.protocol.types.Schema FETCH_RESPONSE_TOPIC_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic", Type.STRING),
                                                                    new org.apache.shade.kafka.common.protocol.types.Field("partition_responses",
                                                                              new org.apache.shade.kafka.common.protocol.types.ArrayOf(FETCH_RESPONSE_PARTITION_V0)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema FETCH_RESPONSE_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("responses",
                                                                        new org.apache.shade.kafka.common.protocol.types.ArrayOf(FETCH_RESPONSE_TOPIC_V0)));
    public static final org.apache.shade.kafka.common.protocol.types.Schema FETCH_RESPONSE_V1 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("throttle_time_ms",
                                                                        Type.INT32,
                                                                        "Duration in milliseconds for which the request was throttled" +
                                                                            " due to quota violation. (Zero if the request did not violate any quota.)",
                                                                        0),
                                                              new org.apache.shade.kafka.common.protocol.types.Field("responses",
                                                                      new org.apache.shade.kafka.common.protocol.types.ArrayOf(FETCH_RESPONSE_TOPIC_V0)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema[] FETCH_REQUEST = new org.apache.shade.kafka.common.protocol.types.Schema[] {FETCH_REQUEST_V0, FETCH_REQUEST_V1};
    public static final org.apache.shade.kafka.common.protocol.types.Schema[] FETCH_RESPONSE = new org.apache.shade.kafka.common.protocol.types.Schema[] {FETCH_RESPONSE_V0, FETCH_RESPONSE_V1};

    /* List groups api */
    public static final org.apache.shade.kafka.common.protocol.types.Schema LIST_GROUPS_REQUEST_V0 = new org.apache.shade.kafka.common.protocol.types.Schema();

    public static final org.apache.shade.kafka.common.protocol.types.Schema LIST_GROUPS_RESPONSE_GROUP_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("group_id", Type.STRING),
                                                                          new org.apache.shade.kafka.common.protocol.types.Field("protocol_type", Type.STRING));
    public static final org.apache.shade.kafka.common.protocol.types.Schema LIST_GROUPS_RESPONSE_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("error_code", Type.INT16),
                                                                    new org.apache.shade.kafka.common.protocol.types.Field("groups", new org.apache.shade.kafka.common.protocol.types.ArrayOf(LIST_GROUPS_RESPONSE_GROUP_V0)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema[] LIST_GROUPS_REQUEST = new org.apache.shade.kafka.common.protocol.types.Schema[] {LIST_GROUPS_REQUEST_V0};
    public static final org.apache.shade.kafka.common.protocol.types.Schema[] LIST_GROUPS_RESPONSE = new org.apache.shade.kafka.common.protocol.types.Schema[] {LIST_GROUPS_RESPONSE_V0};

    /* Describe group api */
    public static final org.apache.shade.kafka.common.protocol.types.Schema DESCRIBE_GROUPS_REQUEST_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("group_ids",
                                                                                 new org.apache.shade.kafka.common.protocol.types.ArrayOf(Type.STRING),
                                                                                 "List of groupIds to request metadata for (an empty groupId array will return empty group metadata)."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema DESCRIBE_GROUPS_RESPONSE_MEMBER_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("member_id",
                                                                                         Type.STRING,
                                                                                         "The memberId assigned by the coordinator"),
                                                                               new org.apache.shade.kafka.common.protocol.types.Field("client_id",
                                                                                         Type.STRING,
                                                                                         "The client id used in the member's latest join group request"),
                                                                               new org.apache.shade.kafka.common.protocol.types.Field("client_host",
                                                                                         Type.STRING,
                                                                                         "The client host used in the request session corresponding to the member's join group."),
                                                                               new org.apache.shade.kafka.common.protocol.types.Field("member_metadata",
                                                                                         Type.BYTES,
                                                                                         "The metadata corresponding to the current group protocol in use (will only be present if the group is stable)."),
                                                                               new org.apache.shade.kafka.common.protocol.types.Field("member_assignment",
                                                                                         Type.BYTES,
                                                                                         "The current assignment provided by the group leader (will only be present if the group is stable)."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema DESCRIBE_GROUPS_RESPONSE_GROUP_METADATA_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("error_code", Type.INT16),
                                                                                       new org.apache.shade.kafka.common.protocol.types.Field("group_id",
                                                                                                 Type.STRING),
                                                                                       new org.apache.shade.kafka.common.protocol.types.Field("state",
                                                                                                 Type.STRING,
                                                                                                 "The current state of the group (one of: Dead, Stable, AwaitingSync, or PreparingRebalance, or empty if there is no active group)"),
                                                                                       new org.apache.shade.kafka.common.protocol.types.Field("protocol_type",
                                                                                                 Type.STRING,
                                                                                                 "The current group protocol type (will be empty if the there is no active group)"),
                                                                                       new org.apache.shade.kafka.common.protocol.types.Field("protocol",
                                                                                                 Type.STRING,
                                                                                                 "The current group protocol (only provided if the group is Stable)"),
                                                                                       new org.apache.shade.kafka.common.protocol.types.Field("members",
                                                                                                 new org.apache.shade.kafka.common.protocol.types.ArrayOf(DESCRIBE_GROUPS_RESPONSE_MEMBER_V0),
                                                                                                 "Current group members (only provided if the group is not Dead)"));

    public static final org.apache.shade.kafka.common.protocol.types.Schema DESCRIBE_GROUPS_RESPONSE_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("groups", new org.apache.shade.kafka.common.protocol.types.ArrayOf(DESCRIBE_GROUPS_RESPONSE_GROUP_METADATA_V0)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema[] DESCRIBE_GROUPS_REQUEST = new org.apache.shade.kafka.common.protocol.types.Schema[] {DESCRIBE_GROUPS_REQUEST_V0};
    public static final org.apache.shade.kafka.common.protocol.types.Schema[] DESCRIBE_GROUPS_RESPONSE = new org.apache.shade.kafka.common.protocol.types.Schema[] {DESCRIBE_GROUPS_RESPONSE_V0};

    /* Group coordinator api */
    public static final org.apache.shade.kafka.common.protocol.types.Schema GROUP_COORDINATOR_REQUEST_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("group_id",
                                                                                   Type.STRING,
                                                                                   "The unique group id."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema GROUP_COORDINATOR_RESPONSE_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("error_code", Type.INT16),
                                                                          new org.apache.shade.kafka.common.protocol.types.Field("coordinator",
                                                                                    BROKER,
                                                                                    "Host and port information for the coordinator for a consumer group."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema[] GROUP_COORDINATOR_REQUEST = new org.apache.shade.kafka.common.protocol.types.Schema[] {GROUP_COORDINATOR_REQUEST_V0};
    public static final org.apache.shade.kafka.common.protocol.types.Schema[] GROUP_COORDINATOR_RESPONSE = new org.apache.shade.kafka.common.protocol.types.Schema[] {GROUP_COORDINATOR_RESPONSE_V0};

    /* Controlled shutdown api */
    public static final org.apache.shade.kafka.common.protocol.types.Schema CONTROLLED_SHUTDOWN_REQUEST_V1 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("broker_id",
                                                                                     Type.INT32,
                                                                                     "The id of the broker for which controlled shutdown has been requested."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema CONTROLLED_SHUTDOWN_PARTITION_V1 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic", Type.STRING),
                                                                             new org.apache.shade.kafka.common.protocol.types.Field("partition",
                                                                                       Type.INT32,
                                                                                       "Topic partition id."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema CONTROLLED_SHUTDOWN_RESPONSE_V1 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("error_code", Type.INT16),
                                                                            new org.apache.shade.kafka.common.protocol.types.Field("partitions_remaining",
                                                                                      new org.apache.shade.kafka.common.protocol.types.ArrayOf(CONTROLLED_SHUTDOWN_PARTITION_V1),
                                                                                      "The partitions that the broker still leads."));

    /* V0 is not supported as it would require changes to the request header not to include `clientId` */
    public static final org.apache.shade.kafka.common.protocol.types.Schema[] CONTROLLED_SHUTDOWN_REQUEST = new org.apache.shade.kafka.common.protocol.types.Schema[] {null, CONTROLLED_SHUTDOWN_REQUEST_V1};
    public static final org.apache.shade.kafka.common.protocol.types.Schema[] CONTROLLED_SHUTDOWN_RESPONSE = new org.apache.shade.kafka.common.protocol.types.Schema[] {null, CONTROLLED_SHUTDOWN_RESPONSE_V1};

    /* Join group api */
    public static final org.apache.shade.kafka.common.protocol.types.Schema JOIN_GROUP_REQUEST_PROTOCOL_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("protocol_name", Type.STRING),
                                                                           new org.apache.shade.kafka.common.protocol.types.Field("protocol_metadata", Type.BYTES));

    public static final org.apache.shade.kafka.common.protocol.types.Schema JOIN_GROUP_REQUEST_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("group_id",
                                                                            Type.STRING,
                                                                            "The group id."),
                                                                  new org.apache.shade.kafka.common.protocol.types.Field("session_timeout",
                                                                            Type.INT32,
                                                                            "The coordinator considers the consumer dead if it receives no heartbeat after this timeout in ms."),
                                                                  new org.apache.shade.kafka.common.protocol.types.Field("member_id",
                                                                            Type.STRING,
                                                                            "The assigned consumer id or an empty string for a new consumer."),
                                                                  new org.apache.shade.kafka.common.protocol.types.Field("protocol_type",
                                                                            Type.STRING,
                                                                            "Unique name for class of protocols implemented by group"),
                                                                  new org.apache.shade.kafka.common.protocol.types.Field("group_protocols",
                                                                            new org.apache.shade.kafka.common.protocol.types.ArrayOf(JOIN_GROUP_REQUEST_PROTOCOL_V0),
                                                                            "List of protocols that the member supports"));


    public static final org.apache.shade.kafka.common.protocol.types.Schema JOIN_GROUP_RESPONSE_MEMBER_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("member_id", Type.STRING),
                                                                          new org.apache.shade.kafka.common.protocol.types.Field("member_metadata", Type.BYTES));
    public static final org.apache.shade.kafka.common.protocol.types.Schema JOIN_GROUP_RESPONSE_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("error_code", Type.INT16),
                                                                   new org.apache.shade.kafka.common.protocol.types.Field("generation_id",
                                                                             Type.INT32,
                                                                             "The generation of the consumer group."),
                                                                   new org.apache.shade.kafka.common.protocol.types.Field("group_protocol",
                                                                             Type.STRING,
                                                                             "The group protocol selected by the coordinator"),
                                                                   new org.apache.shade.kafka.common.protocol.types.Field("leader_id",
                                                                             Type.STRING,
                                                                             "The leader of the group"),
                                                                   new org.apache.shade.kafka.common.protocol.types.Field("member_id",
                                                                             Type.STRING,
                                                                             "The consumer id assigned by the group coordinator."),
                                                                   new org.apache.shade.kafka.common.protocol.types.Field("members",
                                                                             new org.apache.shade.kafka.common.protocol.types.ArrayOf(JOIN_GROUP_RESPONSE_MEMBER_V0)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema[] JOIN_GROUP_REQUEST = new org.apache.shade.kafka.common.protocol.types.Schema[] {JOIN_GROUP_REQUEST_V0};
    public static final org.apache.shade.kafka.common.protocol.types.Schema[] JOIN_GROUP_RESPONSE = new org.apache.shade.kafka.common.protocol.types.Schema[] {JOIN_GROUP_RESPONSE_V0};

    /* SyncGroup api */
    public static final org.apache.shade.kafka.common.protocol.types.Schema SYNC_GROUP_REQUEST_MEMBER_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("member_id", Type.STRING),
                                                                         new org.apache.shade.kafka.common.protocol.types.Field("member_assignment", Type.BYTES));
    public static final org.apache.shade.kafka.common.protocol.types.Schema SYNC_GROUP_REQUEST_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("group_id", Type.STRING),
                                                                  new org.apache.shade.kafka.common.protocol.types.Field("generation_id", Type.INT32),
                                                                  new org.apache.shade.kafka.common.protocol.types.Field("member_id", Type.STRING),
                                                                  new org.apache.shade.kafka.common.protocol.types.Field("group_assignment", new org.apache.shade.kafka.common.protocol.types.ArrayOf(SYNC_GROUP_REQUEST_MEMBER_V0)));
    public static final org.apache.shade.kafka.common.protocol.types.Schema SYNC_GROUP_RESPONSE_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("error_code", Type.INT16),
                                                                   new org.apache.shade.kafka.common.protocol.types.Field("member_assignment", Type.BYTES));
    public static final org.apache.shade.kafka.common.protocol.types.Schema[] SYNC_GROUP_REQUEST = new org.apache.shade.kafka.common.protocol.types.Schema[] {SYNC_GROUP_REQUEST_V0};
    public static final org.apache.shade.kafka.common.protocol.types.Schema[] SYNC_GROUP_RESPONSE = new org.apache.shade.kafka.common.protocol.types.Schema[] {SYNC_GROUP_RESPONSE_V0};

    /* Heartbeat api */
    public static final org.apache.shade.kafka.common.protocol.types.Schema HEARTBEAT_REQUEST_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("group_id", Type.STRING, "The group id."),
                                                                 new org.apache.shade.kafka.common.protocol.types.Field("group_generation_id",
                                                                           Type.INT32,
                                                                           "The generation of the group."),
                                                                 new org.apache.shade.kafka.common.protocol.types.Field("member_id",
                                                                           Type.STRING,
                                                                           "The member id assigned by the group coordinator."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema HEARTBEAT_RESPONSE_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("error_code", Type.INT16));

    public static final org.apache.shade.kafka.common.protocol.types.Schema[] HEARTBEAT_REQUEST = new org.apache.shade.kafka.common.protocol.types.Schema[] {HEARTBEAT_REQUEST_V0};
    public static final org.apache.shade.kafka.common.protocol.types.Schema[] HEARTBEAT_RESPONSE = new org.apache.shade.kafka.common.protocol.types.Schema[] {HEARTBEAT_RESPONSE_V0};

    /* Leave group api */
    public static final org.apache.shade.kafka.common.protocol.types.Schema LEAVE_GROUP_REQUEST_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("group_id", Type.STRING, "The group id."),
                                                                   new org.apache.shade.kafka.common.protocol.types.Field("member_id",
                                                                             Type.STRING,
                                                                             "The member id assigned by the group coordinator."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema LEAVE_GROUP_RESPONSE_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("error_code", Type.INT16));

    public static final org.apache.shade.kafka.common.protocol.types.Schema[] LEAVE_GROUP_REQUEST = new org.apache.shade.kafka.common.protocol.types.Schema[] {LEAVE_GROUP_REQUEST_V0};
    public static final org.apache.shade.kafka.common.protocol.types.Schema[] LEAVE_GROUP_RESPONSE = new org.apache.shade.kafka.common.protocol.types.Schema[] {LEAVE_GROUP_RESPONSE_V0};

    /* Leader and ISR api */
    public static final org.apache.shade.kafka.common.protocol.types.Schema LEADER_AND_ISR_REQUEST_PARTITION_STATE_V0 =
            new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic", Type.STRING, "Topic name."),
                       new org.apache.shade.kafka.common.protocol.types.Field("partition", Type.INT32, "Topic partition id."),
                       new org.apache.shade.kafka.common.protocol.types.Field("controller_epoch", Type.INT32, "The controller epoch."),
                       new org.apache.shade.kafka.common.protocol.types.Field("leader", Type.INT32, "The broker id for the leader."),
                       new org.apache.shade.kafka.common.protocol.types.Field("leader_epoch", Type.INT32, "The leader epoch."),
                       new org.apache.shade.kafka.common.protocol.types.Field("isr", new org.apache.shade.kafka.common.protocol.types.ArrayOf(Type.INT32), "The in sync replica ids."),
                       new org.apache.shade.kafka.common.protocol.types.Field("zk_version", Type.INT32, "The ZK version."),
                       new org.apache.shade.kafka.common.protocol.types.Field("replicas", new org.apache.shade.kafka.common.protocol.types.ArrayOf(Type.INT32), "The replica ids."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema LEADER_AND_ISR_REQUEST_LIVE_LEADER_V0 =
            new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("id", Type.INT32, "The broker id."),
                       new org.apache.shade.kafka.common.protocol.types.Field("host", Type.STRING, "The hostname of the broker."),
                       new org.apache.shade.kafka.common.protocol.types.Field("port", Type.INT32, "The port on which the broker accepts requests."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema LEADER_AND_ISR_REQUEST_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("controller_id", Type.INT32, "The controller id."),
                                                                      new org.apache.shade.kafka.common.protocol.types.Field("controller_epoch", Type.INT32, "The controller epoch."),
                                                                      new org.apache.shade.kafka.common.protocol.types.Field("partition_states",
                                                                                new org.apache.shade.kafka.common.protocol.types.ArrayOf(LEADER_AND_ISR_REQUEST_PARTITION_STATE_V0)),
                                                                      new org.apache.shade.kafka.common.protocol.types.Field("live_leaders", new org.apache.shade.kafka.common.protocol.types.ArrayOf(LEADER_AND_ISR_REQUEST_LIVE_LEADER_V0)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema LEADER_AND_ISR_RESPONSE_PARTITION_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic", Type.STRING, "Topic name."),
                                                                                 new org.apache.shade.kafka.common.protocol.types.Field("partition", Type.INT32, "Topic partition id."),
                                                                                 new org.apache.shade.kafka.common.protocol.types.Field("error_code", Type.INT16, "Error code."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema LEADER_AND_ISR_RESPONSE_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("error_code", Type.INT16, "Error code."),
                                                                       new org.apache.shade.kafka.common.protocol.types.Field("partitions",
                                                                                 new org.apache.shade.kafka.common.protocol.types.ArrayOf(LEADER_AND_ISR_RESPONSE_PARTITION_V0)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema[] LEADER_AND_ISR_REQUEST = new org.apache.shade.kafka.common.protocol.types.Schema[] {LEADER_AND_ISR_REQUEST_V0};
    public static final org.apache.shade.kafka.common.protocol.types.Schema[] LEADER_AND_ISR_RESPONSE = new org.apache.shade.kafka.common.protocol.types.Schema[] {LEADER_AND_ISR_RESPONSE_V0};

    /* Replica api */
    public static final org.apache.shade.kafka.common.protocol.types.Schema STOP_REPLICA_REQUEST_PARTITION_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic", Type.STRING, "Topic name."),
                                                                              new org.apache.shade.kafka.common.protocol.types.Field("partition", Type.INT32, "Topic partition id."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema STOP_REPLICA_REQUEST_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("controller_id", Type.INT32, "The controller id."),
                                                                    new org.apache.shade.kafka.common.protocol.types.Field("controller_epoch", Type.INT32, "The controller epoch."),
                                                                    new org.apache.shade.kafka.common.protocol.types.Field("delete_partitions",
                                                                              Type.INT8,
                                                                              "Boolean which indicates if replica's partitions must be deleted."),
                                                                    new org.apache.shade.kafka.common.protocol.types.Field("partitions",
                                                                              new org.apache.shade.kafka.common.protocol.types.ArrayOf(STOP_REPLICA_REQUEST_PARTITION_V0)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema STOP_REPLICA_RESPONSE_PARTITION_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("topic", Type.STRING, "Topic name."),
                                                                               new org.apache.shade.kafka.common.protocol.types.Field("partition", Type.INT32, "Topic partition id."),
                                                                               new org.apache.shade.kafka.common.protocol.types.Field("error_code", Type.INT16, "Error code."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema STOP_REPLICA_RESPONSE_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("error_code", Type.INT16, "Error code."),
                                                                     new org.apache.shade.kafka.common.protocol.types.Field("partitions",
                                                                               new org.apache.shade.kafka.common.protocol.types.ArrayOf(STOP_REPLICA_RESPONSE_PARTITION_V0)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema[] STOP_REPLICA_REQUEST = new org.apache.shade.kafka.common.protocol.types.Schema[] {STOP_REPLICA_REQUEST_V0};
    public static final org.apache.shade.kafka.common.protocol.types.Schema[] STOP_REPLICA_RESPONSE = new org.apache.shade.kafka.common.protocol.types.Schema[] {STOP_REPLICA_RESPONSE_V0};

    /* Update metadata api */

    public static final org.apache.shade.kafka.common.protocol.types.Schema UPDATE_METADATA_REQUEST_PARTITION_STATE_V0 = LEADER_AND_ISR_REQUEST_PARTITION_STATE_V0;

    public static final org.apache.shade.kafka.common.protocol.types.Schema UPDATE_METADATA_REQUEST_BROKER_V0 =
            new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("id", Type.INT32, "The broker id."),
                       new org.apache.shade.kafka.common.protocol.types.Field("host", Type.STRING, "The hostname of the broker."),
                       new org.apache.shade.kafka.common.protocol.types.Field("port", Type.INT32, "The port on which the broker accepts requests."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema UPDATE_METADATA_REQUEST_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("controller_id", Type.INT32, "The controller id."),
                                                                       new org.apache.shade.kafka.common.protocol.types.Field("controller_epoch", Type.INT32, "The controller epoch."),
                                                                       new org.apache.shade.kafka.common.protocol.types.Field("partition_states",
                                                                                 new org.apache.shade.kafka.common.protocol.types.ArrayOf(UPDATE_METADATA_REQUEST_PARTITION_STATE_V0)),
                                                                       new org.apache.shade.kafka.common.protocol.types.Field("live_brokers",
                                                                                 new org.apache.shade.kafka.common.protocol.types.ArrayOf(UPDATE_METADATA_REQUEST_BROKER_V0)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema UPDATE_METADATA_RESPONSE_V0 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("error_code", Type.INT16, "Error code."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema UPDATE_METADATA_REQUEST_PARTITION_STATE_V1 = UPDATE_METADATA_REQUEST_PARTITION_STATE_V0;

    public static final org.apache.shade.kafka.common.protocol.types.Schema UPDATE_METADATA_REQUEST_END_POINT_V1 =
            // for some reason, V1 sends `port` before `host` while V0 sends `host` before `port
            new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("port", Type.INT32, "The port on which the broker accepts requests."),
                       new org.apache.shade.kafka.common.protocol.types.Field("host", Type.STRING, "The hostname of the broker."),
                       new org.apache.shade.kafka.common.protocol.types.Field("security_protocol_type", Type.INT16, "The security protocol type."));

    public static final org.apache.shade.kafka.common.protocol.types.Schema UPDATE_METADATA_REQUEST_BROKER_V1 =
            new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("id", Type.INT32, "The broker id."),
                       new org.apache.shade.kafka.common.protocol.types.Field("end_points", new org.apache.shade.kafka.common.protocol.types.ArrayOf(UPDATE_METADATA_REQUEST_END_POINT_V1)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema UPDATE_METADATA_REQUEST_V1 = new org.apache.shade.kafka.common.protocol.types.Schema(new org.apache.shade.kafka.common.protocol.types.Field("controller_id", Type.INT32, "The controller id."),
                                                                       new org.apache.shade.kafka.common.protocol.types.Field("controller_epoch", Type.INT32, "The controller epoch."),
                                                                       new org.apache.shade.kafka.common.protocol.types.Field("partition_states",
                                                                                 new org.apache.shade.kafka.common.protocol.types.ArrayOf(UPDATE_METADATA_REQUEST_PARTITION_STATE_V1)),
                                                                       new Field("live_brokers",
                                                                                 new ArrayOf(UPDATE_METADATA_REQUEST_BROKER_V1)));

    public static final org.apache.shade.kafka.common.protocol.types.Schema UPDATE_METADATA_RESPONSE_V1 = UPDATE_METADATA_RESPONSE_V0;

    public static final org.apache.shade.kafka.common.protocol.types.Schema[] UPDATE_METADATA_REQUEST = new org.apache.shade.kafka.common.protocol.types.Schema[] {UPDATE_METADATA_REQUEST_V0, UPDATE_METADATA_REQUEST_V1};
    public static final org.apache.shade.kafka.common.protocol.types.Schema[] UPDATE_METADATA_RESPONSE = new org.apache.shade.kafka.common.protocol.types.Schema[] {UPDATE_METADATA_RESPONSE_V0, UPDATE_METADATA_RESPONSE_V1};

    /* an array of all requests and responses with all schema versions; a null value in the inner array means that the
     * particular version is not supported */
    public static final org.apache.shade.kafka.common.protocol.types.Schema[][] REQUESTS = new org.apache.shade.kafka.common.protocol.types.Schema[org.apache.shade.kafka.common.protocol.ApiKeys.MAX_API_KEY + 1][];
    public static final org.apache.shade.kafka.common.protocol.types.Schema[][] RESPONSES = new Schema[org.apache.shade.kafka.common.protocol.ApiKeys.MAX_API_KEY + 1][];

    /* the latest version of each api */
    public static final short[] CURR_VERSION = new short[org.apache.shade.kafka.common.protocol.ApiKeys.MAX_API_KEY + 1];

    static {
        REQUESTS[org.apache.shade.kafka.common.protocol.ApiKeys.PRODUCE.id] = PRODUCE_REQUEST;
        REQUESTS[org.apache.shade.kafka.common.protocol.ApiKeys.FETCH.id] = FETCH_REQUEST;
        REQUESTS[org.apache.shade.kafka.common.protocol.ApiKeys.LIST_OFFSETS.id] = LIST_OFFSET_REQUEST;
        REQUESTS[org.apache.shade.kafka.common.protocol.ApiKeys.METADATA.id] = METADATA_REQUEST;
        REQUESTS[org.apache.shade.kafka.common.protocol.ApiKeys.LEADER_AND_ISR.id] = LEADER_AND_ISR_REQUEST;
        REQUESTS[org.apache.shade.kafka.common.protocol.ApiKeys.STOP_REPLICA.id] = STOP_REPLICA_REQUEST;
        REQUESTS[org.apache.shade.kafka.common.protocol.ApiKeys.UPDATE_METADATA_KEY.id] = UPDATE_METADATA_REQUEST;
        REQUESTS[org.apache.shade.kafka.common.protocol.ApiKeys.CONTROLLED_SHUTDOWN_KEY.id] = CONTROLLED_SHUTDOWN_REQUEST;
        REQUESTS[org.apache.shade.kafka.common.protocol.ApiKeys.OFFSET_COMMIT.id] = OFFSET_COMMIT_REQUEST;
        REQUESTS[org.apache.shade.kafka.common.protocol.ApiKeys.OFFSET_FETCH.id] = OFFSET_FETCH_REQUEST;
        REQUESTS[org.apache.shade.kafka.common.protocol.ApiKeys.GROUP_COORDINATOR.id] = GROUP_COORDINATOR_REQUEST;
        REQUESTS[org.apache.shade.kafka.common.protocol.ApiKeys.JOIN_GROUP.id] = JOIN_GROUP_REQUEST;
        REQUESTS[org.apache.shade.kafka.common.protocol.ApiKeys.HEARTBEAT.id] = HEARTBEAT_REQUEST;
        REQUESTS[org.apache.shade.kafka.common.protocol.ApiKeys.LEAVE_GROUP.id] = LEAVE_GROUP_REQUEST;
        REQUESTS[org.apache.shade.kafka.common.protocol.ApiKeys.SYNC_GROUP.id] = SYNC_GROUP_REQUEST;
        REQUESTS[org.apache.shade.kafka.common.protocol.ApiKeys.DESCRIBE_GROUPS.id] = DESCRIBE_GROUPS_REQUEST;
        REQUESTS[org.apache.shade.kafka.common.protocol.ApiKeys.LIST_GROUPS.id] = LIST_GROUPS_REQUEST;

        RESPONSES[org.apache.shade.kafka.common.protocol.ApiKeys.PRODUCE.id] = PRODUCE_RESPONSE;
        RESPONSES[org.apache.shade.kafka.common.protocol.ApiKeys.FETCH.id] = FETCH_RESPONSE;
        RESPONSES[org.apache.shade.kafka.common.protocol.ApiKeys.LIST_OFFSETS.id] = LIST_OFFSET_RESPONSE;
        RESPONSES[org.apache.shade.kafka.common.protocol.ApiKeys.METADATA.id] = METADATA_RESPONSE;
        RESPONSES[org.apache.shade.kafka.common.protocol.ApiKeys.LEADER_AND_ISR.id] = LEADER_AND_ISR_RESPONSE;
        RESPONSES[org.apache.shade.kafka.common.protocol.ApiKeys.STOP_REPLICA.id] = STOP_REPLICA_RESPONSE;
        RESPONSES[org.apache.shade.kafka.common.protocol.ApiKeys.UPDATE_METADATA_KEY.id] = UPDATE_METADATA_RESPONSE;
        RESPONSES[org.apache.shade.kafka.common.protocol.ApiKeys.CONTROLLED_SHUTDOWN_KEY.id] = CONTROLLED_SHUTDOWN_RESPONSE;
        RESPONSES[org.apache.shade.kafka.common.protocol.ApiKeys.OFFSET_COMMIT.id] = OFFSET_COMMIT_RESPONSE;
        RESPONSES[org.apache.shade.kafka.common.protocol.ApiKeys.OFFSET_FETCH.id] = OFFSET_FETCH_RESPONSE;
        RESPONSES[org.apache.shade.kafka.common.protocol.ApiKeys.GROUP_COORDINATOR.id] = GROUP_COORDINATOR_RESPONSE;
        RESPONSES[org.apache.shade.kafka.common.protocol.ApiKeys.JOIN_GROUP.id] = JOIN_GROUP_RESPONSE;
        RESPONSES[org.apache.shade.kafka.common.protocol.ApiKeys.HEARTBEAT.id] = HEARTBEAT_RESPONSE;
        RESPONSES[org.apache.shade.kafka.common.protocol.ApiKeys.LEAVE_GROUP.id] = LEAVE_GROUP_RESPONSE;
        RESPONSES[org.apache.shade.kafka.common.protocol.ApiKeys.SYNC_GROUP.id] = SYNC_GROUP_RESPONSE;
        RESPONSES[org.apache.shade.kafka.common.protocol.ApiKeys.DESCRIBE_GROUPS.id] = DESCRIBE_GROUPS_RESPONSE;
        RESPONSES[org.apache.shade.kafka.common.protocol.ApiKeys.LIST_GROUPS.id] = LIST_GROUPS_RESPONSE;

        /* set the maximum version of each api */
        for (org.apache.shade.kafka.common.protocol.ApiKeys api : org.apache.shade.kafka.common.protocol.ApiKeys.values())
            CURR_VERSION[api.id] = (short) (REQUESTS[api.id].length - 1);

        /* sanity check that we have the same number of request and response versions for each api */
        for (org.apache.shade.kafka.common.protocol.ApiKeys api : ApiKeys.values())
            if (REQUESTS[api.id].length != RESPONSES[api.id].length)
                throw new IllegalStateException(REQUESTS[api.id].length + " request versions for api " + api.name
                        + " but " + RESPONSES[api.id].length + " response versions.");
    }

}
