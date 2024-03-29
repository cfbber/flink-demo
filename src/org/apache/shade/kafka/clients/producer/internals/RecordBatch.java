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
package org.apache.shade.kafka.clients.producer.internals;

import java.util.ArrayList;
import java.util.List;

import org.apache.shade.kafka.clients.producer.Callback;
import org.apache.shade.kafka.clients.producer.RecordMetadata;
import org.apache.shade.kafka.common.TopicPartition;
import org.apache.shade.kafka.common.errors.TimeoutException;
import org.apache.shade.kafka.common.record.MemoryRecords;
import org.apache.shade.kafka.common.record.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A batch of records that is or will be sent.
 * 
 * This class is not thread safe and external synchronization must be used when modifying it
 */
public final class RecordBatch {

    private static final Logger log = LoggerFactory.getLogger(RecordBatch.class);

    public int recordCount = 0;
    public int maxRecordSize = 0;
    public volatile int attempts = 0;
    public final long createdMs;
    public long drainedMs;
    public long lastAttemptMs;
    public final MemoryRecords records;
    public final TopicPartition topicPartition;
    public final ProduceRequestResult produceFuture;
    public long lastAppendTime;
    private final List<Thunk> thunks;
    private boolean retry;

    public RecordBatch(TopicPartition tp, MemoryRecords records, long now) {
        this.createdMs = now;
        this.lastAttemptMs = now;
        this.records = records;
        this.topicPartition = tp;
        this.produceFuture = new ProduceRequestResult();
        this.thunks = new ArrayList<Thunk>();
        this.lastAppendTime = createdMs;
        this.retry = false;
    }

    /**
     * Append the record to the current record set and return the relative offset within that record set
     * 
     * @return The RecordSend corresponding to this record or null if there isn't sufficient room.
     */
    public org.apache.shade.kafka.clients.producer.internals.FutureRecordMetadata tryAppend(byte[] key, byte[] value, Callback callback, long now) {
        if (!this.records.hasRoomFor(key, value)) {
            return null;
        } else {
            this.records.append(0L, key, value);
            this.maxRecordSize = Math.max(this.maxRecordSize, Record.recordSize(key, value));
            this.lastAppendTime = now;
            org.apache.shade.kafka.clients.producer.internals.FutureRecordMetadata future = new org.apache.shade.kafka.clients.producer.internals.FutureRecordMetadata(this.produceFuture, this.recordCount);
            if (callback != null)
                thunks.add(new Thunk(callback, future));
            this.recordCount++;
            return future;
        }
    }

    /**
     * Complete the request
     * 
     * @param baseOffset The base offset of the messages assigned by the server
     * @param exception The exception that occurred (or null if the request was successful)
     */
    public void done(long baseOffset, RuntimeException exception) {
        log.trace("Produced messages to topic-partition {} with base offset offset {} and error: {}.",
                  topicPartition,
                  baseOffset,
                  exception);
        // execute callbacks
        for (int i = 0; i < this.thunks.size(); i++) {
            try {
                Thunk thunk = this.thunks.get(i);
                if (exception == null) {
                    RecordMetadata metadata = new RecordMetadata(this.topicPartition,  baseOffset, thunk.future.relativeOffset());
                    thunk.callback.onCompletion(metadata, null);
                } else {
                    thunk.callback.onCompletion(null, exception);
                }
            } catch (Exception e) {
                log.error("Error executing user-provided callback on message for topic-partition {}:", topicPartition, e);
            }
        }
        this.produceFuture.done(topicPartition, baseOffset, exception);
    }

    /**
     * A callback and the associated FutureRecordMetadata argument to pass to it.
     */
    final private static class Thunk {
        final Callback callback;
        final org.apache.shade.kafka.clients.producer.internals.FutureRecordMetadata future;

        public Thunk(Callback callback, FutureRecordMetadata future) {
            this.callback = callback;
            this.future = future;
        }
    }

    @Override
    public String toString() {
        return "RecordBatch(topicPartition=" + topicPartition + ", recordCount=" + recordCount + ")";
    }

    /**
     * Expire the batch that is ready but is sitting in accumulator for more than requestTimeout due to metadata being unavailable.
     * We need to explicitly check if the record is full or linger time is met because the accumulator's partition may not be ready
     * if the leader is unavailable.
     */
    public boolean maybeExpire(int requestTimeout, long now, long lingerMs) {
        boolean expire = false;
        if ((this.records.isFull() && requestTimeout < (now - this.lastAppendTime)) || requestTimeout < (now - (this.lastAttemptMs + lingerMs))) {
            expire = true;
            this.records.close();
            this.done(-1L, new TimeoutException("Batch Expired"));
        }

        return expire;
    }

    /**
     * Returns if the batch is been retried for sending to kafka
     */
    public boolean inRetry() {
        return this.retry;
    }

    /**
     * Set retry to true if the batch is being retried (for send)
     */
    public void setRetry() {
        this.retry = true;
    }
}