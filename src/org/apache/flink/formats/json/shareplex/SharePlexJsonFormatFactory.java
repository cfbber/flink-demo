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

import org.apache.flink.annotation.Internal;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.formats.common.TimestampFormat;
import org.apache.flink.formats.json.JsonFormatOptions;
import org.apache.flink.formats.json.JsonFormatOptionsUtil;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.format.DecodingFormat;
import org.apache.flink.table.connector.format.EncodingFormat;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.factories.DeserializationFormatFactory;
import org.apache.flink.table.factories.DynamicTableFactory;
import org.apache.flink.table.factories.FactoryUtil;
import org.apache.flink.table.factories.SerializationFormatFactory;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.logical.RowType;
import org.apache.flink.types.RowKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.apache.flink.formats.json.JsonFormatOptions.ENCODE_DECIMAL_AS_PLAIN_NUMBER;
import static org.apache.flink.formats.json.shareplex.SharePlexJsonFormatOptions.*;

/**
 * Format factory for providing configured instances of shareplex JSON to RowData {@link
 * DeserializationSchema}.
 */
@Internal
public class SharePlexJsonFormatFactory
        implements DeserializationFormatFactory, SerializationFormatFactory {

    public static final String IDENTIFIER = "shareplex-json";
    private static Logger LOG = LoggerFactory.getLogger(SharePlexJsonFormatFactory.class);

    static {
        System.err.println("init........" + SharePlexJsonFormatFactory.class);
    }

    {
       System.err.println("init new.........." + SharePlexJsonFormatFactory.class);

    }

    @Override
    public DecodingFormat<DeserializationSchema<RowData>> createDecodingFormat(
            DynamicTableFactory.Context context, ReadableConfig formatOptions) {
        FactoryUtil.validateFactoryOptions(this, formatOptions);
        validateDecodingFormatOptions(formatOptions);

        final boolean ignoreParseErrors = formatOptions.get(IGNORE_PARSE_ERRORS);
        String defaultTable = formatOptions.get(FILTER_TABLE);
        SharePlexJsonFormatOptions.setDefaultTable(defaultTable);

        final TimestampFormat timestampFormat =
                JsonFormatOptionsUtil.getTimestampFormat(formatOptions);

        System.err.println("config..." + formatOptions);
        System.err.println("config...defa " + defaultTable);

        return new SharePlexJsonDecodingFormat(ignoreParseErrors, timestampFormat, defaultTable);
    }

    @Override
    public EncodingFormat<SerializationSchema<RowData>> createEncodingFormat(
            DynamicTableFactory.Context context, ReadableConfig formatOptions) {
        FactoryUtil.validateFactoryOptions(this, formatOptions);
        validateEncodingFormatOptions(formatOptions);

        TimestampFormat timestampFormat = JsonFormatOptionsUtil.getTimestampFormat(formatOptions);
        JsonFormatOptions.MapNullKeyMode mapNullKeyMode =
                JsonFormatOptionsUtil.getMapNullKeyMode(formatOptions);
        String mapNullKeyLiteral = formatOptions.get(JSON_MAP_NULL_KEY_LITERAL);

        final boolean encodeDecimalAsPlainNumber =
                formatOptions.get(ENCODE_DECIMAL_AS_PLAIN_NUMBER);

        String defaultTable = formatOptions.get(FILTER_TABLE);
        SharePlexJsonFormatOptions.setDefaultTable(defaultTable);

        System.err.println("config..." + formatOptions);
        System.err.println("config...defa " + defaultTable);

        return new EncodingFormat<SerializationSchema<RowData>>() {

            @Override
            public ChangelogMode getChangelogMode() {
                return ChangelogMode.newBuilder()
                        .addContainedKind(RowKind.INSERT)
                        .addContainedKind(RowKind.UPDATE_BEFORE)
                        .addContainedKind(RowKind.UPDATE_AFTER)
                        .addContainedKind(RowKind.DELETE)
                        .build();
            }

            @Override
            public SerializationSchema<RowData> createRuntimeEncoder(
                    DynamicTableSink.Context context, DataType consumedDataType) {
                final RowType rowType = (RowType) consumedDataType.getLogicalType();
                return new SharePlexJsonSerializationSchema(
                        rowType,
                        timestampFormat,
                        mapNullKeyMode,
                        mapNullKeyLiteral,
                        encodeDecimalAsPlainNumber);
            }
        };
    }

    @Override
    public String factoryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        return Collections.emptySet();
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        Set<ConfigOption<?>> options = new HashSet<>();
        options.add(IGNORE_PARSE_ERRORS);
        options.add(TIMESTAMP_FORMAT);
        options.add(JSON_MAP_NULL_KEY_MODE);
        options.add(JSON_MAP_NULL_KEY_LITERAL);
        options.add(ENCODE_DECIMAL_AS_PLAIN_NUMBER);
        return options;
    }

    /**
     * Validator for shareplex decoding format.
     */
    private static void validateDecodingFormatOptions(ReadableConfig tableOptions) {
        JsonFormatOptionsUtil.validateDecodingFormatOptions(tableOptions);
    }

    /**
     * Validator for shareplex encoding format.
     */
    private static void validateEncodingFormatOptions(ReadableConfig tableOptions) {
        JsonFormatOptionsUtil.validateEncodingFormatOptions(tableOptions);
    }
}
