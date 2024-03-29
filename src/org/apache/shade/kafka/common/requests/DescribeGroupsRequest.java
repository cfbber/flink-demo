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

import org.apache.shade.kafka.common.protocol.ApiKeys;
import org.apache.shade.kafka.common.protocol.Errors;
import org.apache.shade.kafka.common.protocol.ProtoUtils;
import org.apache.shade.kafka.common.protocol.types.Schema;
import org.apache.shade.kafka.common.protocol.types.Struct;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DescribeGroupsRequest extends AbstractRequest {
    private static final Schema CURRENT_SCHEMA = ProtoUtils.currentRequestSchema(ApiKeys.DESCRIBE_GROUPS.id);
    private static final String GROUP_IDS_KEY_NAME = "group_ids";

    private final List<String> groupIds;

    public DescribeGroupsRequest(List<String> groupIds) {
        super(new org.apache.shade.kafka.common.protocol.types.Struct(CURRENT_SCHEMA));
        struct.set(GROUP_IDS_KEY_NAME, groupIds.toArray());
        this.groupIds = groupIds;
    }

    public DescribeGroupsRequest(org.apache.shade.kafka.common.protocol.types.Struct struct) {
        super(struct);
        this.groupIds = new ArrayList<>();
        for (Object groupId : struct.getArray(GROUP_IDS_KEY_NAME))
            this.groupIds.add((String) groupId);
    }

    public List<String> groupIds() {
        return groupIds;
    }

    @Override
    public AbstractRequestResponse getErrorResponse(int versionId, Throwable e) {
        switch (versionId) {
            case 0:
                return DescribeGroupsResponse.fromError(Errors.forException(e), groupIds);

            default:
                throw new IllegalArgumentException(String.format("Version %d is not valid. Valid versions for %s are 0 to %d",
                        versionId, this.getClass().getSimpleName(), ProtoUtils.latestVersion(ApiKeys.DESCRIBE_GROUPS.id)));
        }
    }

    public static DescribeGroupsRequest parse(ByteBuffer buffer, int versionId) {
        return new DescribeGroupsRequest(ProtoUtils.parseRequest(ApiKeys.DESCRIBE_GROUPS.id, versionId, buffer));
    }

    public static DescribeGroupsRequest parse(ByteBuffer buffer) {
        return new DescribeGroupsRequest((Struct) CURRENT_SCHEMA.read(buffer));
    }

}
