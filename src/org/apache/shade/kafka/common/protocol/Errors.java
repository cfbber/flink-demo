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

import java.util.HashMap;
import java.util.Map;

import org.apache.shade.kafka.common.errors.ControllerMovedException;
import org.apache.shade.kafka.common.errors.NotEnoughReplicasAfterAppendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains all the client-server errors--those errors that must be sent from the server to the client. These
 * are thus part of the protocol. The names can be changed but the error code cannot.
 * 
 * Do not add exceptions that occur only on the client or only on the server here.
 */
public enum Errors {
    UNKNOWN(-1, new org.apache.shade.kafka.common.errors.UnknownServerException("The server experienced an unexpected error when processing the request")),
    NONE(0, null),
    OFFSET_OUT_OF_RANGE(1,
            new org.apache.shade.kafka.common.errors.ApiException("The requested offset is not within the range of offsets maintained by the server.")),
    CORRUPT_MESSAGE(2,
            new org.apache.shade.kafka.common.errors.CorruptRecordException("The message contents does not match the message CRC or the message is otherwise corrupt.")),
    UNKNOWN_TOPIC_OR_PARTITION(3,
            new org.apache.shade.kafka.common.errors.UnknownTopicOrPartitionException("This server does not host this topic-partition.")),
    // TODO: errorCode 4 for InvalidFetchSize
    LEADER_NOT_AVAILABLE(5,
            new org.apache.shade.kafka.common.errors.LeaderNotAvailableException("There is no leader for this topic-partition as we are in the middle of a leadership election.")),
    NOT_LEADER_FOR_PARTITION(6,
            new org.apache.shade.kafka.common.errors.NotLeaderForPartitionException("This server is not the leader for that topic-partition.")),
    REQUEST_TIMED_OUT(7,
            new org.apache.shade.kafka.common.errors.TimeoutException("The request timed out.")),
    BROKER_NOT_AVAILABLE(8,
            new org.apache.shade.kafka.common.errors.BrokerNotAvailableException("The broker is not available.")),
    REPLICA_NOT_AVAILABLE(9,
            new org.apache.shade.kafka.common.errors.ApiException("The replica is not available for the requested topic-partition")),
    MESSAGE_TOO_LARGE(10,
            new org.apache.shade.kafka.common.errors.RecordTooLargeException("The request included a message larger than the max message size the server will accept.")),
    STALE_CONTROLLER_EPOCH(11,
            new ControllerMovedException("The controller moved to another broker.")),
    OFFSET_METADATA_TOO_LARGE(12,
            new org.apache.shade.kafka.common.errors.OffsetMetadataTooLarge("The metadata field of the offset request was too large.")),
    NETWORK_EXCEPTION(13,
            new org.apache.shade.kafka.common.errors.NetworkException("The server disconnected before a response was received.")),
    GROUP_LOAD_IN_PROGRESS(14,
            new org.apache.shade.kafka.common.errors.GroupLoadInProgressException("The coordinator is loading and hence can't process requests for this group.")),
    GROUP_COORDINATOR_NOT_AVAILABLE(15,
            new org.apache.shade.kafka.common.errors.GroupCoordinatorNotAvailableException("The group coordinator is not available.")),
    NOT_COORDINATOR_FOR_GROUP(16,
            new org.apache.shade.kafka.common.errors.NotCoordinatorForGroupException("This is not the correct coordinator for this group.")),
    INVALID_TOPIC_EXCEPTION(17,
            new org.apache.shade.kafka.common.errors.InvalidTopicException("The request attempted to perform an operation on an invalid topic.")),
    RECORD_LIST_TOO_LARGE(18,
            new org.apache.shade.kafka.common.errors.RecordBatchTooLargeException("The request included message batch larger than the configured segment size on the server.")),
    NOT_ENOUGH_REPLICAS(19,
            new org.apache.shade.kafka.common.errors.NotEnoughReplicasException("Messages are rejected since there are fewer in-sync replicas than required.")),
    NOT_ENOUGH_REPLICAS_AFTER_APPEND(20,
            new NotEnoughReplicasAfterAppendException("Messages are written to the log, but to fewer in-sync replicas than required.")),
    INVALID_REQUIRED_ACKS(21,
            new org.apache.shade.kafka.common.errors.InvalidRequiredAcksException("Produce request specified an invalid value for required acks.")),
    ILLEGAL_GENERATION(22,
            new org.apache.shade.kafka.common.errors.IllegalGenerationException("Specified group generation id is not valid.")),
    INCONSISTENT_GROUP_PROTOCOL(23,
            new org.apache.shade.kafka.common.errors.ApiException("The group member's supported protocols are incompatible with those of existing members.")),
    INVALID_GROUP_ID(24,
            new org.apache.shade.kafka.common.errors.ApiException("The configured groupId is invalid")),
    UNKNOWN_MEMBER_ID(25,
            new org.apache.shade.kafka.common.errors.UnknownMemberIdException("The coordinator is not aware of this member.")),
    INVALID_SESSION_TIMEOUT(26,
            new org.apache.shade.kafka.common.errors.ApiException("The session timeout is not within an acceptable range.")),
    REBALANCE_IN_PROGRESS(27,
            new org.apache.shade.kafka.common.errors.RebalanceInProgressException("The group is rebalancing, so a rejoin is needed.")),
    INVALID_COMMIT_OFFSET_SIZE(28,
            new org.apache.shade.kafka.common.errors.ApiException("The committing offset data size is not valid")),
    TOPIC_AUTHORIZATION_FAILED(29,
            new org.apache.shade.kafka.common.errors.AuthorizationException("Topic authorization failed.")),
    GROUP_AUTHORIZATION_FAILED(30,
            new org.apache.shade.kafka.common.errors.AuthorizationException("Group authorization failed.")),
    CLUSTER_AUTHORIZATION_FAILED(31,
            new org.apache.shade.kafka.common.errors.AuthorizationException("Cluster authorization failed."));

    private static final Logger log = LoggerFactory.getLogger(Errors.class);

    private static Map<Class<?>, Errors> classToError = new HashMap<Class<?>, Errors>();
    private static Map<Short, Errors> codeToError = new HashMap<Short, Errors>();

    static {
        for (Errors error : Errors.values()) {
            codeToError.put(error.code(), error);
            if (error.exception != null)
                classToError.put(error.exception.getClass(), error);
        }
    }

    private final short code;
    private final org.apache.shade.kafka.common.errors.ApiException exception;

    private Errors(int code, org.apache.shade.kafka.common.errors.ApiException exception) {
        this.code = (short) code;
        this.exception = exception;
    }

    /**
     * An instance of the exception
     */
    public org.apache.shade.kafka.common.errors.ApiException exception() {
        return this.exception;
    }

    /**
     * The error code for the exception
     */
    public short code() {
        return this.code;
    }

    /**
     * Throw the exception corresponding to this error if there is one
     */
    public void maybeThrow() {
        if (exception != null) {
            throw this.exception;
        }
    }

    /**
     * Throw the exception if there is one
     */
    public static Errors forCode(short code) {
        Errors error = codeToError.get(code);
        if (error != null) {
            return error;
        } else {
            log.warn("Unexpected error code: {}.", code);
            return UNKNOWN;
        }
    }

    /**
     * Return the error instance associated with this exception (or UNKNOWN if there is none)
     */
    public static Errors forException(Throwable t) {
        Errors error = classToError.get(t.getClass());
        return error == null ? UNKNOWN : error;
    }
}
