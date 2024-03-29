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

package org.apache.shade.kafka.common.config;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

public class SslConfigs {
    /*
     * NOTE: DO NOT CHANGE EITHER CONFIG NAMES AS THESE ARE PART OF THE PUBLIC API AND CHANGE WILL BREAK USER CODE.
     */

    public static final String PRINCIPAL_BUILDER_CLASS_CONFIG = "principal.builder.class";
    public static final String PRINCIPAL_BUILDER_CLASS_DOC = "The fully qualified name of a class that implements the PrincipalBuilder interface, " +
            "which is currently used to build the Principal for connections with the SSL SecurityProtocol.";
    public static final String DEFAULT_PRINCIPAL_BUILDER_CLASS = "org.apache.shade.kafka.common.security.auth.DefaultPrincipalBuilder";

    public static final String SSL_PROTOCOL_CONFIG = "ssl.protocol";
    public static final String SSL_PROTOCOL_DOC = "The SSL protocol used to generate the SSLContext. "
            + "Default setting is TLS, which is fine for most cases. "
            + "Allowed values in recent JVMs are TLS, TLSv1.1 and TLSv1.2. SSL, SSLv2 and SSLv3 "
            + "may be supported in older JVMs, but their usage is discouraged due to known security vulnerabilities.";

    public static final String DEFAULT_SSL_PROTOCOL = "TLS";

    public static final String SSL_PROVIDER_CONFIG = "ssl.provider";
    public static final String SSL_PROVIDER_DOC = "The name of the security provider used for SSL connections. Default value is the default security provider of the JVM.";

    public static final String SSL_CIPHER_SUITES_CONFIG = "ssl.cipher.suites";
    public static final String SSL_CIPHER_SUITES_DOC = "A list of cipher suites. This is a named combination of authentication, encryption, MAC and key exchange algorithm used to negotiate the security settings for a network connection using TLS or SSL network protocol."
            + "By default all the available cipher suites are supported.";

    public static final String SSL_ENABLED_PROTOCOLS_CONFIG = "ssl.enabled.protocols";
    public static final String SSL_ENABLED_PROTOCOLS_DOC = "The list of protocols enabled for SSL connections.";
    public static final String DEFAULT_SSL_ENABLED_PROTOCOLS = "TLSv1.2,TLSv1.1,TLSv1";

    public static final String SSL_KEYSTORE_TYPE_CONFIG = "ssl.keystore.type";
    public static final String SSL_KEYSTORE_TYPE_DOC = "The file format of the key store file. "
            + "This is optional for client.";
    public static final String DEFAULT_SSL_KEYSTORE_TYPE = "JKS";

    public static final String SSL_KEYSTORE_LOCATION_CONFIG = "ssl.keystore.location";
    public static final String SSL_KEYSTORE_LOCATION_DOC = "The location of the key store file. "
        + "This is optional for client and can be used for two-way authentication for client.";

    public static final String SSL_KEYSTORE_PASSWORD_CONFIG = "ssl.keystore.password";
    public static final String SSL_KEYSTORE_PASSWORD_DOC = "The store password for the key store file."
        + "This is optional for client and only needed if ssl.keystore.location is configured. ";

    public static final String SSL_KEY_PASSWORD_CONFIG = "ssl.key.password";
    public static final String SSL_KEY_PASSWORD_DOC = "The password of the private key in the key store file. "
            + "This is optional for client.";

    public static final String SSL_TRUSTSTORE_TYPE_CONFIG = "ssl.truststore.type";
    public static final String SSL_TRUSTSTORE_TYPE_DOC = "The file format of the trust store file.";
    public static final String DEFAULT_SSL_TRUSTSTORE_TYPE = "JKS";

    public static final String SSL_TRUSTSTORE_LOCATION_CONFIG = "ssl.truststore.location";
    public static final String SSL_TRUSTSTORE_LOCATION_DOC = "The location of the trust store file. ";

    public static final String SSL_TRUSTSTORE_PASSWORD_CONFIG = "ssl.truststore.password";
    public static final String SSL_TRUSTSTORE_PASSWORD_DOC = "The password for the trust store file. ";

    public static final String SSL_KEYMANAGER_ALGORITHM_CONFIG = "ssl.keymanager.algorithm";
    public static final String SSL_KEYMANAGER_ALGORITHM_DOC = "The algorithm used by key manager factory for SSL connections. "
            + "Default value is the key manager factory algorithm configured for the Java Virtual Machine.";
    public static final String DEFAULT_SSL_KEYMANGER_ALGORITHM = KeyManagerFactory.getDefaultAlgorithm();

    public static final String SSL_TRUSTMANAGER_ALGORITHM_CONFIG = "ssl.trustmanager.algorithm";
    public static final String SSL_TRUSTMANAGER_ALGORITHM_DOC = "The algorithm used by trust manager factory for SSL connections. "
            + "Default value is the trust manager factory algorithm configured for the Java Virtual Machine.";
    public static final String DEFAULT_SSL_TRUSTMANAGER_ALGORITHM = TrustManagerFactory.getDefaultAlgorithm();

    public static final String SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG = "ssl.endpoint.identification.algorithm";
    public static final String SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_DOC = "The endpoint identification algorithm to validate server hostname using server certificate. ";

    public static final String SSL_CLIENT_AUTH_CONFIG = "ssl.client.auth";
    public static final String SSL_CLIENT_AUTH_DOC = "Configures kafka broker to request client authentication."
                                           + " The following settings are common: "
                                           + " <ul>"
                                           + " <li><code>ssl.client.auth=required</code> If set to required"
                                           + " client authentication is required."
                                           + " <li><code>ssl.client.auth=requested</code> This means client authentication is optional."
                                           + " unlike requested , if this option is set client can choose not to provide authentication information about itself"
                                           + " <li><code>ssl.client.auth=none</code> This means client authentication is not needed.";

    public static void addClientSslSupport(org.apache.shade.kafka.common.config.ConfigDef config) {
        config.define(SslConfigs.SSL_PROTOCOL_CONFIG, org.apache.shade.kafka.common.config.ConfigDef.Type.STRING, SslConfigs.DEFAULT_SSL_PROTOCOL, org.apache.shade.kafka.common.config.ConfigDef.Importance.MEDIUM, SslConfigs.SSL_PROTOCOL_DOC)
                .define(SslConfigs.SSL_PROVIDER_CONFIG, org.apache.shade.kafka.common.config.ConfigDef.Type.STRING, null, org.apache.shade.kafka.common.config.ConfigDef.Importance.MEDIUM, SslConfigs.SSL_PROVIDER_DOC)
                .define(SslConfigs.SSL_CIPHER_SUITES_CONFIG, org.apache.shade.kafka.common.config.ConfigDef.Type.LIST, null, org.apache.shade.kafka.common.config.ConfigDef.Importance.LOW, SslConfigs.SSL_CIPHER_SUITES_DOC)
                .define(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, org.apache.shade.kafka.common.config.ConfigDef.Type.LIST, SslConfigs.DEFAULT_SSL_ENABLED_PROTOCOLS, org.apache.shade.kafka.common.config.ConfigDef.Importance.MEDIUM, SslConfigs.SSL_ENABLED_PROTOCOLS_DOC)
                .define(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, org.apache.shade.kafka.common.config.ConfigDef.Type.STRING, SslConfigs.DEFAULT_SSL_KEYSTORE_TYPE, org.apache.shade.kafka.common.config.ConfigDef.Importance.MEDIUM, SslConfigs.SSL_KEYSTORE_TYPE_DOC)
                .define(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, org.apache.shade.kafka.common.config.ConfigDef.Type.STRING, null,  org.apache.shade.kafka.common.config.ConfigDef.Importance.HIGH, SslConfigs.SSL_KEYSTORE_LOCATION_DOC)
                .define(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, org.apache.shade.kafka.common.config.ConfigDef.Type.PASSWORD, null, org.apache.shade.kafka.common.config.ConfigDef.Importance.HIGH, SslConfigs.SSL_KEYSTORE_PASSWORD_DOC)
                .define(SslConfigs.SSL_KEY_PASSWORD_CONFIG, org.apache.shade.kafka.common.config.ConfigDef.Type.PASSWORD, null, org.apache.shade.kafka.common.config.ConfigDef.Importance.HIGH, SslConfigs.SSL_KEY_PASSWORD_DOC)
                .define(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, org.apache.shade.kafka.common.config.ConfigDef.Type.STRING, SslConfigs.DEFAULT_SSL_TRUSTSTORE_TYPE, org.apache.shade.kafka.common.config.ConfigDef.Importance.MEDIUM, SslConfigs.SSL_TRUSTSTORE_TYPE_DOC)
                .define(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, org.apache.shade.kafka.common.config.ConfigDef.Type.STRING, null, org.apache.shade.kafka.common.config.ConfigDef.Importance.HIGH, SslConfigs.SSL_TRUSTSTORE_LOCATION_DOC)
                .define(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, org.apache.shade.kafka.common.config.ConfigDef.Type.PASSWORD, null, org.apache.shade.kafka.common.config.ConfigDef.Importance.HIGH, SslConfigs.SSL_TRUSTSTORE_PASSWORD_DOC)
                .define(SslConfigs.SSL_KEYMANAGER_ALGORITHM_CONFIG, org.apache.shade.kafka.common.config.ConfigDef.Type.STRING, SslConfigs.DEFAULT_SSL_KEYMANGER_ALGORITHM, org.apache.shade.kafka.common.config.ConfigDef.Importance.LOW, SslConfigs.SSL_KEYMANAGER_ALGORITHM_DOC)
                .define(SslConfigs.SSL_TRUSTMANAGER_ALGORITHM_CONFIG, org.apache.shade.kafka.common.config.ConfigDef.Type.STRING, SslConfigs.DEFAULT_SSL_TRUSTMANAGER_ALGORITHM, org.apache.shade.kafka.common.config.ConfigDef.Importance.LOW, SslConfigs.SSL_TRUSTMANAGER_ALGORITHM_DOC)
                .define(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, org.apache.shade.kafka.common.config.ConfigDef.Type.STRING, null, ConfigDef.Importance.LOW, SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_DOC);
    }
}
