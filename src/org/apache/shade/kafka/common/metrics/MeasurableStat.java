/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shade.kafka.common.metrics;

import org.apache.shade.kafka.common.metrics.stats.Avg;
import org.apache.shade.kafka.common.metrics.stats.Count;
import org.apache.shade.kafka.common.metrics.stats.Max;

/**
 * A MeasurableStat is a {@link Stat} that is also {@link Measurable} (i.e. can produce a single floating point value).
 * This is the interface used for most of the simple statistics such as {@link Avg},
 * {@link Max}, {@link Count}, etc.
 */
public interface MeasurableStat extends Stat, Measurable {

}
