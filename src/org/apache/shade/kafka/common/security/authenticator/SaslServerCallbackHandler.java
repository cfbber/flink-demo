/**
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

package org.apache.shade.kafka.common.security.authenticator;

import java.io.IOException;

import org.apache.shade.kafka.common.security.kerberos.KerberosShortNamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;

import org.apache.shade.kafka.common.security.kerberos.KerberosName;
import org.apache.shade.kafka.common.security.JaasUtils;

public class SaslServerCallbackHandler implements CallbackHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SaslServerCallbackHandler.class);
    private final KerberosShortNamer kerberosShortNamer;

    public SaslServerCallbackHandler(Configuration configuration, KerberosShortNamer kerberosNameParser) throws IOException {
        AppConfigurationEntry[] configurationEntries = configuration.getAppConfigurationEntry(JaasUtils.LOGIN_CONTEXT_SERVER);
        if (configurationEntries == null)
            throw new IOException("Could not find a 'KafkaServer' entry in this configuration: Kafka Server cannot start.");
        this.kerberosShortNamer = kerberosNameParser;
    }

    public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof RealmCallback) {
                handleRealmCallback((RealmCallback) callback);
            } else if (callback instanceof AuthorizeCallback) {
                handleAuthorizeCallback((AuthorizeCallback) callback);
            }
        }
    }

    private void handleRealmCallback(RealmCallback rc) {
        LOG.trace("Client supplied realm: {} ", rc.getDefaultText());
        rc.setText(rc.getDefaultText());
    }

    private void handleAuthorizeCallback(AuthorizeCallback ac) {
        String authenticationID = ac.getAuthenticationID();
        String authorizationID = ac.getAuthorizationID();

        LOG.info("Successfully authenticated client: authenticationID={}; authorizationID={}.", authenticationID,
                authorizationID);
        ac.setAuthorized(true);

        KerberosName kerberosName = KerberosName.parse(authenticationID);
        try {
            String userName = kerberosShortNamer.shortName(kerberosName);
            LOG.info("Setting authorizedID: {}", userName);
            ac.setAuthorizedID(userName);
        } catch (IOException e) {
            LOG.error("Failed to set name based on Kerberos authentication rules.");
        }
    }

}
