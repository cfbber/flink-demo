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
package org.apache.shade.kafka.common.network;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;

import org.apache.shade.kafka.common.config.SaslConfigs;
import org.apache.shade.kafka.common.KafkaException;

import org.apache.shade.kafka.common.protocol.SecurityProtocol;
import org.apache.shade.kafka.common.security.JaasUtils;
import org.apache.shade.kafka.common.security.authenticator.SaslClientAuthenticator;
import org.apache.shade.kafka.common.security.authenticator.SaslServerAuthenticator;
import org.apache.shade.kafka.common.security.kerberos.KerberosShortNamer;
import org.apache.shade.kafka.common.security.kerberos.LoginManager;
import org.apache.shade.kafka.common.security.ssl.SslFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaslChannelBuilder implements ChannelBuilder {
    private static final Logger log = LoggerFactory.getLogger(SaslChannelBuilder.class);

    private final org.apache.shade.kafka.common.protocol.SecurityProtocol securityProtocol;
    private final org.apache.shade.kafka.common.network.Mode mode;
    private final org.apache.shade.kafka.common.network.LoginType loginType;

    private org.apache.shade.kafka.common.security.kerberos.LoginManager loginManager;
    private org.apache.shade.kafka.common.security.ssl.SslFactory sslFactory;
    private Map<String, ?> configs;
    private org.apache.shade.kafka.common.security.kerberos.KerberosShortNamer kerberosShortNamer;

    public SaslChannelBuilder(org.apache.shade.kafka.common.network.Mode mode, LoginType loginType, org.apache.shade.kafka.common.protocol.SecurityProtocol securityProtocol) {
        this.mode = mode;
        this.loginType = loginType;
        this.securityProtocol = securityProtocol;
    }

    public void configure(Map<String, ?> configs) throws KafkaException {
        try {
            this.configs = configs;
            this.loginManager = LoginManager.acquireLoginManager(loginType, configs);

            String defaultRealm;
            try {
                defaultRealm = JaasUtils.defaultRealm();
            } catch (Exception ke) {
                defaultRealm = "";
            }

            List<String> principalToLocalRules = (List<String>) configs.get(SaslConfigs.SASL_KERBEROS_PRINCIPAL_TO_LOCAL_RULES);
            if (principalToLocalRules != null)
                kerberosShortNamer = KerberosShortNamer.fromUnparsedRules(defaultRealm, principalToLocalRules);

            if (this.securityProtocol == org.apache.shade.kafka.common.protocol.SecurityProtocol.SASL_SSL) {
                // Disable SSL client authentication as we are using SASL authentication
                this.sslFactory = new SslFactory(mode, "none");
                this.sslFactory.configure(configs);
            }
        } catch (Exception e) {
            throw new KafkaException(e);
        }
    }

    public org.apache.shade.kafka.common.network.KafkaChannel buildChannel(String id, SelectionKey key, int maxReceiveSize) throws KafkaException {
        try {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            TransportLayer transportLayer = buildTransportLayer(id, key, socketChannel);
            Authenticator authenticator;
            if (mode == Mode.SERVER)
                authenticator = new SaslServerAuthenticator(id, loginManager.subject(), kerberosShortNamer, maxReceiveSize);
            else
                authenticator = new SaslClientAuthenticator(id, loginManager.subject(), loginManager.serviceName(),
                        socketChannel.socket().getInetAddress().getHostName());
            // Both authenticators don't use `PrincipalBuilder`, so we pass `null` for now. Reconsider if this changes.
            authenticator.configure(transportLayer, null, this.configs);
            return new KafkaChannel(id, transportLayer, authenticator, maxReceiveSize);
        } catch (Exception e) {
            log.info("Failed to create channel due to ", e);
            throw new KafkaException(e);
        }
    }

    public void close()  {
        this.loginManager.release();
    }

    protected TransportLayer buildTransportLayer(String id, SelectionKey key, SocketChannel socketChannel) throws IOException {
        if (this.securityProtocol == SecurityProtocol.SASL_SSL) {
            return SslTransportLayer.create(id, key,
                sslFactory.createSslEngine(socketChannel.socket().getInetAddress().getHostName(), socketChannel.socket().getPort()));
        } else {
            return new PlaintextTransportLayer(key);
        }
    }

}
