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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.shade.kafka.clients.producer.RecordMetadata;

/**
 * The future result of a record send
 */
public final class FutureRecordMetadata implements Future<RecordMetadata> {

    private final ProduceRequestResult result;
    private final long relativeOffset;

    public FutureRecordMetadata(ProduceRequestResult result, long relativeOffset) {
        this.result = result;
        this.relativeOffset = relativeOffset;
    }

    @Override
    public boolean cancel(boolean interrupt) {
        return false;
    }

    @Override
    public RecordMetadata get() throws InterruptedException, ExecutionException {
        this.result.await();
        return valueOrError();
    }

    @Override
    public RecordMetadata get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean occurred = this.result.await(timeout, unit);
        if (!occurred)
            throw new TimeoutException("Timeout after waiting for " + TimeUnit.MILLISECONDS.convert(timeout, unit) + " ms.");
        return valueOrError();
    }

    RecordMetadata valueOrError() throws ExecutionException {
        if (this.result.error() != null)
            throw new ExecutionException(this.result.error());
        else
            return value();
    }
    
    RecordMetadata value() {
        return new RecordMetadata(result.topicPartition(), this.result.baseOffset(), this.relativeOffset);
    }
    
    public long relativeOffset() {
        return this.relativeOffset;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return this.result.completed();
    }

}
