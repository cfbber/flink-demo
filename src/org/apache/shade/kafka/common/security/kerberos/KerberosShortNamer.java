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

package org.apache.shade.kafka.common.security.kerberos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class implements parsing and handling of Kerberos principal names. In
 * particular, it splits them apart and translates them down into local
 * operating system names.
 */
public class KerberosShortNamer {

    /**
     * A pattern for parsing a auth_to_local rule.
     */
    private static final Pattern RULE_PARSER = Pattern.compile("((DEFAULT)|(RULE:\\[(\\d*):([^\\]]*)](\\(([^)]*)\\))?(s/([^/]*)/([^/]*)/(g)?)?))");

    /* Rules for the translation of the principal name into an operating system name */
    private final List<org.apache.shade.kafka.common.security.kerberos.KerberosRule> principalToLocalRules;

    public KerberosShortNamer(List<org.apache.shade.kafka.common.security.kerberos.KerberosRule> principalToLocalRules) {
        this.principalToLocalRules = principalToLocalRules;
    }

    public static KerberosShortNamer fromUnparsedRules(String defaultRealm, List<String> principalToLocalRules) {
        List<String> rules = principalToLocalRules == null ? Collections.singletonList("DEFAULT") : principalToLocalRules;
        return new KerberosShortNamer(parseRules(defaultRealm, rules));
    }

    private static List<org.apache.shade.kafka.common.security.kerberos.KerberosRule> parseRules(String defaultRealm, List<String> rules) {
        List<org.apache.shade.kafka.common.security.kerberos.KerberosRule> result = new ArrayList<>();
        for (String rule : rules) {
            Matcher matcher = RULE_PARSER.matcher(rule);
            if (!matcher.lookingAt()) {
                throw new IllegalArgumentException("Invalid rule: " + rule);
            }
            if (rule.length() != matcher.end())
                throw new IllegalArgumentException("Invalid rule: `" + rule + "`, unmatched substring: `" + rule.substring(matcher.end()) + "`");
            if (matcher.group(2) != null) {
                result.add(new org.apache.shade.kafka.common.security.kerberos.KerberosRule(defaultRealm));
            } else {
                result.add(new org.apache.shade.kafka.common.security.kerberos.KerberosRule(defaultRealm,
                        Integer.parseInt(matcher.group(4)),
                        matcher.group(5),
                        matcher.group(7),
                        matcher.group(9),
                        matcher.group(10),
                        "g".equals(matcher.group(11))));

            }
        }
        return result;
    }

    /**
     * Get the translation of the principal name into an operating system
     * user name.
     * @return the short name
     * @throws IOException
     */
    public String shortName(KerberosName kerberosName) throws IOException {
        String[] params;
        if (kerberosName.hostName() == null) {
            // if it is already simple, just return it
            if (kerberosName.realm() == null)
                return kerberosName.serviceName();
            params = new String[]{kerberosName.realm(), kerberosName.serviceName()};
        } else {
            params = new String[]{kerberosName.realm(), kerberosName.serviceName(), kerberosName.hostName()};
        }
        for (org.apache.shade.kafka.common.security.kerberos.KerberosRule r : principalToLocalRules) {
            String result = r.apply(params);
            if (result != null)
                return result;
        }
        throw new NoMatchingRule("No rules applied to " + toString());
    }

}
