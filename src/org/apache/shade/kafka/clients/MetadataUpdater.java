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

package org.apache.shade.kafka.clients;

import org.apache.shade.kafka.common.Node;
import org.apache.shade.kafka.common.protocol.types.Struct;

import java.util.List;

/**
 * The interface used by `NetworkClient` to request cluster metadata info to be updated and to retrieve the cluster nodes
 * from such metadata. This is an internal class.
 * <p>
 * This class is not thread-safe!
 */
interface MetadataUpdater {

    /**
     * Gets the current cluster info without blocking.
     */
    List<Node> fetchNodes();

    /**
     * Returns true if an update to the cluster metadata info is due.
     */
    boolean isUpdateDue(long now);

    /**
     * Starts a cluster metadata update if needed and possible. Returns the time until the metadata update (which would
     * be 0 if an update has been started as a result of this call).
     *
     * If the implementation relies on `NetworkClient` to send requests, the completed receive will be passed to
     * `maybeHandleCompletedReceive`.
     *
     * The semantics of `needed` and `possible` are implementation-dependent and may take into account a number of
     * factors like node availability, how long since the last metadata update, etc.
     */
    long maybeUpdate(long now);

    /**
     * If `request` is a metadata request, handles it and return `true`. Otherwise, returns `false`.
     *
     * This provides a mechanism for the `MetadataUpdater` implementation to use the NetworkClient instance for its own
     * requests with special handling for disconnections of such requests.
     */
    boolean maybeHandleDisconnection(org.apache.shade.kafka.clients.ClientRequest request);

    /**
     * If `request` is a metadata request, handles it and returns `true`. Otherwise, returns `false`.
     *
     * This provides a mechanism for the `MetadataUpdater` implementation to use the NetworkClient instance for its own
     * requests with special handling for completed receives of such requests.
     */
    boolean maybeHandleCompletedReceive(ClientRequest request, long now, Struct body);

    /**
     * Schedules an update of the current cluster metadata info. A subsequent call to `maybeUpdate` would trigger the
     * start of the update if possible (see `maybeUpdate` for more information).
     */
    void requestUpdate();
}
