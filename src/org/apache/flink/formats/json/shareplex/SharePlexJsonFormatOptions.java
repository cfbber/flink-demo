/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.formats.json.shareplex;

import org.apache.flink.annotation.PublicEvolving;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.formats.json.JsonFormatOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Option utils for shareplex-json format.
 */
@PublicEvolving
public class SharePlexJsonFormatOptions {
    private static Logger LOG = LoggerFactory.getLogger(SharePlexJsonFormatOptions.class);


    public static final ConfigOption<Boolean> IGNORE_PARSE_ERRORS =
            JsonFormatOptions.IGNORE_PARSE_ERRORS;

    public static final ConfigOption<String> TIMESTAMP_FORMAT = JsonFormatOptions.TIMESTAMP_FORMAT;

    public static final ConfigOption<String> JSON_MAP_NULL_KEY_MODE =
            JsonFormatOptions.MAP_NULL_KEY_MODE;

    public static final ConfigOption<String> JSON_MAP_NULL_KEY_LITERAL =
            JsonFormatOptions.MAP_NULL_KEY_LITERAL;

    public static final ConfigOption<String> FILTER_TABLE =
            ConfigOptions.key("property-version")
                    .stringType()
                    .defaultValue("")
                    .withDescription(
                            "Optional flag to control the handling filter table.");

    private static String DEFAULT_TABLE;


    private SharePlexJsonFormatOptions() {
    }

    public static String getDefaultTable() {
        LOG.info("get config defaultTable ..." + DEFAULT_TABLE);

        return DEFAULT_TABLE;
    }

    public static void setDefaultTable(String defaultTable) {
        LOG.info("set config defaultTable ..." + defaultTable);
        DEFAULT_TABLE = defaultTable;
    }
}
