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

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.shade.kafka.common.config.ConfigException;
import org.apache.shade.kafka.common.network.ChannelBuilder;
import org.apache.shade.kafka.common.network.ChannelBuilders;
import org.apache.shade.kafka.common.network.LoginType;
import org.apache.shade.kafka.common.network.Mode;
import org.apache.shade.kafka.common.protocol.SecurityProtocol;
import org.apache.shade.kafka.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientUtils {
    private static final Logger log = LoggerFactory.getLogger(ClientUtils.class);

    public static List<InetSocketAddress> parseAndValidateAddresses(List<String> urls) {
        List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
        for (String url : urls) {
            if (url != null && url.length() > 0) {
                String host = Utils.getHost(url);
                Integer port = Utils.getPort(url);
                if (host == null || port == null)
                    throw new org.apache.shade.kafka.common.config.ConfigException("Invalid url in " + CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG + ": " + url);
                try {
                    InetSocketAddress address = new InetSocketAddress(host, port);
                    if (address.isUnresolved())
                        throw new org.apache.shade.kafka.common.config.ConfigException("DNS resolution failed for url in " + CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG + ": " + url);
                    addresses.add(address);
                } catch (NumberFormatException e) {
                    throw new org.apache.shade.kafka.common.config.ConfigException("Invalid port in " + CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG + ": " + url);
                }
            }
        }
        if (addresses.size() < 1)
            throw new org.apache.shade.kafka.common.config.ConfigException("No bootstrap urls given in " + CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG);
        return addresses;
    }

    public static void closeQuietly(Closeable c, String name, AtomicReference<Throwable> firstException) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable t) {
                firstException.compareAndSet(null, t);
                log.error("Failed to close " + name, t);
            }
        }
    }

    /**
     * @param configs client/server configs
     * @return configured ChannelBuilder based on the configs.
     */
    public static ChannelBuilder createChannelBuilder(Map<String, ?> configs) {
        org.apache.shade.kafka.common.protocol.SecurityProtocol securityProtocol = org.apache.shade.kafka.common.protocol.SecurityProtocol.valueOf((String) configs.get(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG));
        if (!SecurityProtocol.nonTestingValues().contains(securityProtocol))
            throw new ConfigException("Invalid SecurityProtocol " + securityProtocol);
        return ChannelBuilders.create(securityProtocol, Mode.CLIENT, LoginType.CLIENT, configs);
    }

}
