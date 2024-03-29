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

import java.util.Iterator;
import org.apache.shade.kafka.clients.producer.Callback;
import org.apache.shade.kafka.common.Cluster;
import org.apache.shade.kafka.common.MetricName;
import org.apache.shade.kafka.common.Node;
import org.apache.shade.kafka.common.PartitionInfo;
import org.apache.shade.kafka.common.TopicPartition;
import org.apache.shade.kafka.common.metrics.Measurable;
import org.apache.shade.kafka.common.metrics.MetricConfig;
import org.apache.shade.kafka.common.metrics.Metrics;
import org.apache.shade.kafka.common.metrics.Sensor;
import org.apache.shade.kafka.common.metrics.stats.Rate;
import org.apache.shade.kafka.common.record.CompressionType;
import org.apache.shade.kafka.common.record.MemoryRecords;
import org.apache.shade.kafka.common.record.Record;
import org.apache.shade.kafka.common.record.Records;
import org.apache.shade.kafka.common.utils.CopyOnWriteMap;
import org.apache.shade.kafka.common.utils.Time;
import org.apache.shade.kafka.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class acts as a queue that accumulates records into {@link MemoryRecords}
 * instances to be sent to the server.
 * <p>
 * The accumulator uses a bounded amount of memory and append calls will block when that memory is exhausted, unless
 * this behavior is explicitly disabled.
 */
public final class RecordAccumulator {

    private static final Logger log = LoggerFactory.getLogger(RecordAccumulator.class);

    private volatile boolean closed;
    private int drainIndex;
    private final AtomicInteger flushesInProgress;
    private final AtomicInteger appendsInProgress;
    private final int batchSize;
    private final CompressionType compression;
    private final long lingerMs;
    private final long retryBackoffMs;
    private final org.apache.shade.kafka.clients.producer.internals.BufferPool free;
    private final Time time;
    private final ConcurrentMap<TopicPartition, Deque<org.apache.shade.kafka.clients.producer.internals.RecordBatch>> batches;
    private final IncompleteRecordBatches incomplete;


    /**
     * Create a new record accumulator
     * 
     * @param batchSize The size to use when allocating {@link MemoryRecords} instances
     * @param totalSize The maximum memory the record accumulator can use.
     * @param compression The compression codec for the records
     * @param lingerMs An artificial delay time to add before declaring a records instance that isn't full ready for
     *        sending. This allows time for more records to arrive. Setting a non-zero lingerMs will trade off some
     *        latency for potentially better throughput due to more batching (and hence fewer, larger requests).
     * @param retryBackoffMs An artificial delay time to retry the produce request upon receiving an error. This avoids
     *        exhausting all retries in a short period of time.
     * @param metrics The metrics
     * @param time The time instance to use
     * @param metricTags additional key/value attributes of the metric
     */
    public RecordAccumulator(int batchSize,
                             long totalSize,
                             CompressionType compression,
                             long lingerMs,
                             long retryBackoffMs,
                             Metrics metrics,
                             Time time,
                             Map<String, String> metricTags) {
        this.drainIndex = 0;
        this.closed = false;
        this.flushesInProgress = new AtomicInteger(0);
        this.appendsInProgress = new AtomicInteger(0);
        this.batchSize = batchSize;
        this.compression = compression;
        this.lingerMs = lingerMs;
        this.retryBackoffMs = retryBackoffMs;
        this.batches = new CopyOnWriteMap<TopicPartition, Deque<org.apache.shade.kafka.clients.producer.internals.RecordBatch>>();
        String metricGrpName = "producer-metrics";
        this.free = new BufferPool(totalSize, batchSize, metrics, time , metricGrpName , metricTags);
        this.incomplete = new IncompleteRecordBatches();
        this.time = time;
        registerMetrics(metrics, metricGrpName, metricTags);
    }

    private void registerMetrics(Metrics metrics, String metricGrpName, Map<String, String> metricTags) {
        MetricName metricName = new MetricName("waiting-threads", metricGrpName, "The number of user threads blocked waiting for buffer memory to enqueue their records", metricTags);
        Measurable waitingThreads = new Measurable() {
            public double measure(MetricConfig config, long now) {
                return free.queued();
            }
        };
        metrics.addMetric(metricName, waitingThreads);

        metricName = new MetricName("buffer-total-bytes", metricGrpName, "The maximum amount of buffer memory the client can use (whether or not it is currently used).", metricTags);
        Measurable totalBytes = new Measurable() {
            public double measure(MetricConfig config, long now) {
                return free.totalMemory();
            }
        };
        metrics.addMetric(metricName, totalBytes);

        metricName = new MetricName("buffer-available-bytes", metricGrpName, "The total amount of buffer memory that is not being used (either unallocated or in the free list).", metricTags);
        Measurable availableBytes = new Measurable() {
            public double measure(MetricConfig config, long now) {
                return free.availableMemory();
            }
        };
        metrics.addMetric(metricName, availableBytes);

        Sensor bufferExhaustedRecordSensor = metrics.sensor("buffer-exhausted-records");
        metricName = new MetricName("buffer-exhausted-rate", metricGrpName, "The average per-second number of record sends that are dropped due to buffer exhaustion", metricTags);
        bufferExhaustedRecordSensor.add(metricName, new Rate());
    }

    /**
     * Add a record to the accumulator, return the append result
     * <p>
     * The append result will contain the future metadata, and flag for whether the appended batch is full or a new batch is created
     * <p>
     *
     * @param tp The topic/partition to which this record is being sent
     * @param key The key for the record
     * @param value The value for the record
     * @param callback The user-supplied callback to execute when the request is complete
     * @param maxTimeToBlock The maximum time in milliseconds to block for buffer memory to be available
     */
    public RecordAppendResult append(TopicPartition tp, byte[] key, byte[] value, Callback callback, long maxTimeToBlock) throws InterruptedException {
        // We keep track of the number of appending thread to make sure we do not miss batches in
        // abortIncompleteBatches().
        appendsInProgress.incrementAndGet();
        try {
            if (closed)
                throw new IllegalStateException("Cannot send after the producer is closed.");
            // check if we have an in-progress batch
            Deque<org.apache.shade.kafka.clients.producer.internals.RecordBatch> dq = dequeFor(tp);
            synchronized (dq) {
                org.apache.shade.kafka.clients.producer.internals.RecordBatch last = dq.peekLast();
                if (last != null) {
                    org.apache.shade.kafka.clients.producer.internals.FutureRecordMetadata future = last.tryAppend(key, value, callback, time.milliseconds());
                    if (future != null)
                        return new RecordAppendResult(future, dq.size() > 1 || last.records.isFull(), false);
                }
            }

            // we don't have an in-progress record batch try to allocate a new batch
            int size = Math.max(this.batchSize, Records.LOG_OVERHEAD + Record.recordSize(key, value));
            log.trace("Allocating a new {} byte message buffer for topic {} partition {}", size, tp.topic(), tp.partition());
            ByteBuffer buffer = free.allocate(size, maxTimeToBlock);
            synchronized (dq) {
                // Need to check if producer is closed again after grabbing the dequeue lock.
                if (closed)
                    throw new IllegalStateException("Cannot send after the producer is closed.");
                org.apache.shade.kafka.clients.producer.internals.RecordBatch last = dq.peekLast();
                if (last != null) {
                    org.apache.shade.kafka.clients.producer.internals.FutureRecordMetadata future = last.tryAppend(key, value, callback, time.milliseconds());
                    if (future != null) {
                        // Somebody else found us a batch, return the one we waited for! Hopefully this doesn't happen often...
                        free.deallocate(buffer);
                        return new RecordAppendResult(future, dq.size() > 1 || last.records.isFull(), false);
                    }
                }
                MemoryRecords records = MemoryRecords.emptyRecords(buffer, compression, this.batchSize);
                org.apache.shade.kafka.clients.producer.internals.RecordBatch batch = new org.apache.shade.kafka.clients.producer.internals.RecordBatch(tp, records, time.milliseconds());
                org.apache.shade.kafka.clients.producer.internals.FutureRecordMetadata future = Utils.notNull(batch.tryAppend(key, value, callback, time.milliseconds()));

                dq.addLast(batch);
                incomplete.add(batch);
                return new RecordAppendResult(future, dq.size() > 1 || batch.records.isFull(), true);
            }
        } finally {
            appendsInProgress.decrementAndGet();
        }
    }

    /**
     * Abort the batches that have been sitting in RecordAccumulator for more than the configured requestTimeout
     * due to metadata being unavailable
     */
    public List<org.apache.shade.kafka.clients.producer.internals.RecordBatch> abortExpiredBatches(int requestTimeout, Cluster cluster, long now) {
        List<org.apache.shade.kafka.clients.producer.internals.RecordBatch> expiredBatches = new ArrayList<org.apache.shade.kafka.clients.producer.internals.RecordBatch>();
        int count = 0;
        for (Map.Entry<TopicPartition, Deque<org.apache.shade.kafka.clients.producer.internals.RecordBatch>> entry : this.batches.entrySet()) {
            TopicPartition topicAndPartition = entry.getKey();
            Deque<org.apache.shade.kafka.clients.producer.internals.RecordBatch> dq = entry.getValue();
            synchronized (dq) {
                // iterate over the batches and expire them if they have stayed in accumulator for more than requestTimeOut
                Iterator<org.apache.shade.kafka.clients.producer.internals.RecordBatch> batchIterator = dq.iterator();
                while (batchIterator.hasNext()) {
                    org.apache.shade.kafka.clients.producer.internals.RecordBatch batch = batchIterator.next();
                    // check if the batch is expired
                    if (batch.maybeExpire(requestTimeout, now, this.lingerMs)) {
                        expiredBatches.add(batch);
                        count++;
                        batchIterator.remove();
                        deallocate(batch);
                    } else {
                        if (!batch.inRetry()) {
                            break;
                        }
                    }
                }
            }
        }
        if (expiredBatches.size() > 0)
            log.trace("Expired {} batches in accumulator", count);

        return expiredBatches;
    }

    /**
     * Re-enqueue the given record batch in the accumulator to retry
     */
    public void reenqueue(org.apache.shade.kafka.clients.producer.internals.RecordBatch batch, long now) {
        batch.attempts++;
        batch.lastAttemptMs = now;
        batch.lastAppendTime = now;
        batch.setRetry();
        Deque<org.apache.shade.kafka.clients.producer.internals.RecordBatch> deque = dequeFor(batch.topicPartition);
        synchronized (deque) {
            deque.addFirst(batch);
        }
    }

    /**
     * Get a list of nodes whose partitions are ready to be sent, and the earliest time at which any non-sendable
     * partition will be ready; Also return the flag for whether there are any unknown leaders for the accumulated
     * partition batches.
     * <p>
     * A destination node is ready to send data if ANY one of its partition is not backing off the send and ANY of the
     * following are true :
     * <ol>
     * <li>The record set is full
     * <li>The record set has sat in the accumulator for at least lingerMs milliseconds
     * <li>The accumulator is out of memory and threads are blocking waiting for data (in this case all partitions are
     * immediately considered ready).
     * <li>The accumulator has been closed
     * </ol>
     */
    public ReadyCheckResult ready(Cluster cluster, long nowMs) {
        Set<Node> readyNodes = new HashSet<Node>();
        long nextReadyCheckDelayMs = Long.MAX_VALUE;
        boolean unknownLeadersExist = false;

        boolean exhausted = this.free.queued() > 0;
        for (Map.Entry<TopicPartition, Deque<org.apache.shade.kafka.clients.producer.internals.RecordBatch>> entry : this.batches.entrySet()) {
            TopicPartition part = entry.getKey();
            Deque<org.apache.shade.kafka.clients.producer.internals.RecordBatch> deque = entry.getValue();

            Node leader = cluster.leaderFor(part);
            if (leader == null) {
                unknownLeadersExist = true;
            } else if (!readyNodes.contains(leader)) {
                synchronized (deque) {
                    org.apache.shade.kafka.clients.producer.internals.RecordBatch batch = deque.peekFirst();
                    if (batch != null) {
                        boolean backingOff = batch.attempts > 0 && batch.lastAttemptMs + retryBackoffMs > nowMs;
                        long waitedTimeMs = nowMs - batch.lastAttemptMs;
                        long timeToWaitMs = backingOff ? retryBackoffMs : lingerMs;
                        long timeLeftMs = Math.max(timeToWaitMs - waitedTimeMs, 0);
                        boolean full = deque.size() > 1 || batch.records.isFull();
                        boolean expired = waitedTimeMs >= timeToWaitMs;
                        boolean sendable = full || expired || exhausted || closed || flushInProgress();
                        if (sendable && !backingOff) {
                            readyNodes.add(leader);
                        } else {
                            // Note that this results in a conservative estimate since an un-sendable partition may have
                            // a leader that will later be found to have sendable data. However, this is good enough
                            // since we'll just wake up and then sleep again for the remaining time.
                            nextReadyCheckDelayMs = Math.min(timeLeftMs, nextReadyCheckDelayMs);
                        }
                    }
                }
            }
        }

        return new ReadyCheckResult(readyNodes, nextReadyCheckDelayMs, unknownLeadersExist);
    }

    /**
     * @return Whether there is any unsent record in the accumulator.
     */
    public boolean hasUnsent() {
        for (Map.Entry<TopicPartition, Deque<org.apache.shade.kafka.clients.producer.internals.RecordBatch>> entry : this.batches.entrySet()) {
            Deque<org.apache.shade.kafka.clients.producer.internals.RecordBatch> deque = entry.getValue();
            synchronized (deque) {
                if (deque.size() > 0)
                    return true;
            }
        }
        return false;
    }

    /**
     * Drain all the data for the given nodes and collate them into a list of batches that will fit within the specified
     * size on a per-node basis. This method attempts to avoid choosing the same topic-node over and over.
     * 
     * @param cluster The current cluster metadata
     * @param nodes The list of node to drain
     * @param maxSize The maximum number of bytes to drain
     * @param now The current unix time in milliseconds
     * @return A list of {@link org.apache.shade.kafka.clients.producer.internals.RecordBatch} for each node specified with total size less than the requested maxSize.
     */
    public Map<Integer, List<org.apache.shade.kafka.clients.producer.internals.RecordBatch>> drain(Cluster cluster, Set<Node> nodes, int maxSize, long now) {
        if (nodes.isEmpty())
            return Collections.emptyMap();

        Map<Integer, List<org.apache.shade.kafka.clients.producer.internals.RecordBatch>> batches = new HashMap<Integer, List<org.apache.shade.kafka.clients.producer.internals.RecordBatch>>();
        for (Node node : nodes) {
            int size = 0;
            List<PartitionInfo> parts = cluster.partitionsForNode(node.id());
            List<org.apache.shade.kafka.clients.producer.internals.RecordBatch> ready = new ArrayList<org.apache.shade.kafka.clients.producer.internals.RecordBatch>();
            /* to make starvation less likely this loop doesn't start at 0 */
            int start = drainIndex = drainIndex % parts.size();
            do {
                PartitionInfo part = parts.get(drainIndex);
                Deque<org.apache.shade.kafka.clients.producer.internals.RecordBatch> deque = dequeFor(new TopicPartition(part.topic(), part.partition()));
                if (deque != null) {
                    synchronized (deque) {
                        org.apache.shade.kafka.clients.producer.internals.RecordBatch first = deque.peekFirst();
                        if (first != null) {
                            boolean backoff = first.attempts > 0 && first.lastAttemptMs + retryBackoffMs > now;
                            // Only drain the batch if it is not during backoff period.
                            if (!backoff) {
                                if (size + first.records.sizeInBytes() > maxSize && !ready.isEmpty()) {
                                    // there is a rare case that a single batch size is larger than the request size due
                                    // to compression; in this case we will still eventually send this batch in a single
                                    // request
                                    break;
                                } else {
                                    org.apache.shade.kafka.clients.producer.internals.RecordBatch batch = deque.pollFirst();
                                    batch.records.close();
                                    size += batch.records.sizeInBytes();
                                    ready.add(batch);
                                    batch.drainedMs = now;
                                }
                            }
                        }
                    }
                }
                this.drainIndex = (this.drainIndex + 1) % parts.size();
            } while (start != drainIndex);
            batches.put(node.id(), ready);
        }
        return batches;
    }

    /**
     * Get the deque for the given topic-partition, creating it if necessary.
     */
    private Deque<org.apache.shade.kafka.clients.producer.internals.RecordBatch> dequeFor(TopicPartition tp) {
        Deque<org.apache.shade.kafka.clients.producer.internals.RecordBatch> d = this.batches.get(tp);
        if (d != null)
            return d;
        d = new ArrayDeque<>();
        Deque<org.apache.shade.kafka.clients.producer.internals.RecordBatch> previous = this.batches.putIfAbsent(tp, d);
        if (previous == null)
            return d;
        else
            return previous;
    }

    /**
     * Deallocate the record batch
     */
    public void deallocate(org.apache.shade.kafka.clients.producer.internals.RecordBatch batch) {
        incomplete.remove(batch);
        free.deallocate(batch.records.buffer(), batch.records.initialCapacity());
    }
    
    /**
     * Are there any threads currently waiting on a flush?
     */
    private boolean flushInProgress() {
        return flushesInProgress.get() > 0;
    }
    
    /**
     * Initiate the flushing of data from the accumulator...this makes all requests immediately ready
     */
    public void beginFlush() {
        this.flushesInProgress.getAndIncrement();
    }

    /**
     * Are there any threads currently appending messages?
     */
    private boolean appendsInProgress() {
        return appendsInProgress.get() > 0;
    }

    /**
     * Mark all partitions as ready to send and block until the send is complete
     */
    public void awaitFlushCompletion() throws InterruptedException {
        for (org.apache.shade.kafka.clients.producer.internals.RecordBatch batch: this.incomplete.all())
            batch.produceFuture.await();
        this.flushesInProgress.decrementAndGet();
    }

    /**
     * This function is only called when sender is closed forcefully. It will fail all the
     * incomplete batches and return.
     */
    public void abortIncompleteBatches() {
        // We need to keep aborting the incomplete batch until no thread is trying to append to
        // 1. Avoid losing batches.
        // 2. Free up memory in case appending threads are blocked on buffer full.
        // This is a tight loop but should be able to get through very quickly.
        do {
            abortBatches();
        } while (appendsInProgress());
        // After this point, no thread will append any messages because they will see the close
        // flag set. We need to do the last abort after no thread was appending in case the there was a new
        // batch appended by the last appending thread.
        abortBatches();
        this.batches.clear();
    }

    /**
     * Go through incomplete batches and abort them.
     */
    private void abortBatches() {
        for (org.apache.shade.kafka.clients.producer.internals.RecordBatch batch : incomplete.all()) {
            Deque<org.apache.shade.kafka.clients.producer.internals.RecordBatch> dq = dequeFor(batch.topicPartition);
            // Close the batch before aborting
            synchronized (dq) {
                batch.records.close();
            }
            batch.done(-1L, new IllegalStateException("Producer is closed forcefully."));
            deallocate(batch);
        }
    }

    /**
     * Close this accumulator and force all the record buffers to be drained
     */
    public void close() {
        this.closed = true;
    }

    /*
     * Metadata about a record just appended to the record accumulator
     */
    public final static class RecordAppendResult {
        public final org.apache.shade.kafka.clients.producer.internals.FutureRecordMetadata future;
        public final boolean batchIsFull;
        public final boolean newBatchCreated;

        public RecordAppendResult(FutureRecordMetadata future, boolean batchIsFull, boolean newBatchCreated) {
            this.future = future;
            this.batchIsFull = batchIsFull;
            this.newBatchCreated = newBatchCreated;
        }
    }

    /*
     * The set of nodes that have at least one complete record batch in the accumulator
     */
    public final static class ReadyCheckResult {
        public final Set<Node> readyNodes;
        public final long nextReadyCheckDelayMs;
        public final boolean unknownLeadersExist;

        public ReadyCheckResult(Set<Node> readyNodes, long nextReadyCheckDelayMs, boolean unknownLeadersExist) {
            this.readyNodes = readyNodes;
            this.nextReadyCheckDelayMs = nextReadyCheckDelayMs;
            this.unknownLeadersExist = unknownLeadersExist;
        }
    }
    
    /*
     * A threadsafe helper class to hold RecordBatches that haven't been ack'd yet
     */
    private final static class IncompleteRecordBatches {
        private final Set<org.apache.shade.kafka.clients.producer.internals.RecordBatch> incomplete;

        public IncompleteRecordBatches() {
            this.incomplete = new HashSet<org.apache.shade.kafka.clients.producer.internals.RecordBatch>();
        }
        
        public void add(org.apache.shade.kafka.clients.producer.internals.RecordBatch batch) {
            synchronized (incomplete) {
                this.incomplete.add(batch);
            }
        }
        
        public void remove(org.apache.shade.kafka.clients.producer.internals.RecordBatch batch) {
            synchronized (incomplete) {
                boolean removed = this.incomplete.remove(batch);
                if (!removed)
                    throw new IllegalStateException("Remove from the incomplete set failed. This should be impossible.");
            }
        }
        
        public Iterable<org.apache.shade.kafka.clients.producer.internals.RecordBatch> all() {
            synchronized (incomplete) {
                return new ArrayList<RecordBatch>(this.incomplete);
            }
        }
    }
}
