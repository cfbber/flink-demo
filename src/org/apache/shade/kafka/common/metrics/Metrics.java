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
package org.apache.shade.kafka.common.metrics;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.shade.kafka.common.MetricName;
import org.apache.shade.kafka.common.utils.SystemTime;
import org.apache.shade.kafka.common.utils.Time;
import org.apache.shade.kafka.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A registry of sensors and metrics.
 * <p>
 * A metric is a named, numerical measurement. A sensor is a handle to record numerical measurements as they occur. Each
 * Sensor has zero or more associated metrics. For example a Sensor might represent message sizes and we might associate
 * with this sensor a metric for the average, maximum, or other statistics computed off the sequence of message sizes
 * that are recorded by the sensor.
 * <p>
 * Usage looks something like this:
 * 
 * <pre>
 * // set up metrics:
 * Metrics metrics = new Metrics(); // this is the global repository of metrics and sensors
 * Sensor sensor = metrics.sensor(&quot;message-sizes&quot;);
 * MetricName metricName = new MetricName(&quot;message-size-avg&quot;, &quot;producer-metrics&quot;);
 * sensor.add(metricName, new Avg());
 * metricName = new MetricName(&quot;message-size-max&quot;, &quot;producer-metrics&quot;);
 * sensor.add(metricName, new Max());
 * 
 * // as messages are sent we record the sizes
 * sensor.record(messageSize);
 * </pre>
 */
public class Metrics implements Closeable {

    private final org.apache.shade.kafka.common.metrics.MetricConfig config;
    private final ConcurrentMap<MetricName, org.apache.shade.kafka.common.metrics.KafkaMetric> metrics;
    private final ConcurrentMap<String, Sensor> sensors;
    private final ConcurrentMap<Sensor, List<Sensor>> childrenSensors;
    private final List<MetricsReporter> reporters;
    private final Time time;
    private final ScheduledThreadPoolExecutor metricsScheduler;
    private static final Logger log = LoggerFactory.getLogger(Metrics.class);

    /**
     * Create a metrics repository with no metric reporters and default configuration.
     * Expiration of Sensors is disabled.
     */
    public Metrics() {
        this(new org.apache.shade.kafka.common.metrics.MetricConfig());
    }

    /**
     * Create a metrics repository with no metric reporters and default configuration.
     * Expiration of Sensors is disabled.
     */
    public Metrics(Time time) {
        this(new org.apache.shade.kafka.common.metrics.MetricConfig(), new ArrayList<MetricsReporter>(0), time);
    }

    /**
     * Create a metrics repository with no reporters and the given default config. This config will be used for any
     * metric that doesn't override its own config. Expiration of Sensors is disabled.
     * @param defaultConfig The default config to use for all metrics that don't override their config
     */
    public Metrics(org.apache.shade.kafka.common.metrics.MetricConfig defaultConfig) {
        this(defaultConfig, new ArrayList<MetricsReporter>(0), new SystemTime());
    }

    /**
     * Create a metrics repository with a default config and the given metric reporters.
     * Expiration of Sensors is disabled.
     * @param defaultConfig The default config
     * @param reporters The metrics reporters
     * @param time The time instance to use with the metrics
     */
    public Metrics(org.apache.shade.kafka.common.metrics.MetricConfig defaultConfig, List<MetricsReporter> reporters, Time time) {
        this(defaultConfig, reporters, time, false);
    }

    /**
     * Create a metrics repository with a default config, given metric reporters and the ability to expire eligible sensors
     * @param defaultConfig The default config
     * @param reporters The metrics reporters
     * @param time The time instance to use with the metrics
     * @param enableExpiration true if the metrics instance can garbage collect inactive sensors, false otherwise
     */
    public Metrics(org.apache.shade.kafka.common.metrics.MetricConfig defaultConfig, List<MetricsReporter> reporters, Time time, boolean enableExpiration) {
        this.config = defaultConfig;
        this.sensors = new ConcurrentHashMap<>();
        this.metrics = new ConcurrentHashMap<>();
        this.childrenSensors = new ConcurrentHashMap<>();
        this.reporters = Utils.notNull(reporters);
        this.time = time;
        for (MetricsReporter reporter : reporters)
            reporter.init(new ArrayList<org.apache.shade.kafka.common.metrics.KafkaMetric>());

        // Create the ThreadPoolExecutor only if expiration of Sensors is enabled.
        if (enableExpiration) {
            this.metricsScheduler = new ScheduledThreadPoolExecutor(1);
            // Creating a daemon thread to not block shutdown
            this.metricsScheduler.setThreadFactory(new ThreadFactory() {
                public Thread newThread(Runnable runnable) {
                    return Utils.newThread("SensorExpiryThread", runnable, true);
                }
            });
            this.metricsScheduler.scheduleAtFixedRate(new ExpireSensorTask(), 30, 30, TimeUnit.SECONDS);
        } else {
            this.metricsScheduler = null;
        }
    }

    /**
     * Get the sensor with the given name if it exists
     * @param name The name of the sensor
     * @return Return the sensor or null if no such sensor exists
     */
    public Sensor getSensor(String name) {
        return this.sensors.get(Utils.notNull(name));
    }

    /**
     * Get or create a sensor with the given unique name and no parent sensors.
     * @param name The sensor name
     * @return The sensor
     */
    public Sensor sensor(String name) {
        return sensor(name, null, (Sensor[]) null);
    }

    /**
     * Get or create a sensor with the given unique name and zero or more parent sensors. All parent sensors will
     * receive every value recorded with this sensor.
     * @param name The name of the sensor
     * @param parents The parent sensors
     * @return The sensor that is created
     */
    public Sensor sensor(String name, Sensor... parents) {
        return sensor(name, null, parents);
    }

    /**
     * Get or create a sensor with the given unique name and zero or more parent sensors. All parent sensors will
     * receive every value recorded with this sensor.
     * @param name The name of the sensor
     * @param config A default configuration to use for this sensor for metrics that don't have their own config
     * @param parents The parent sensors
     * @return The sensor that is created
     */
    public synchronized Sensor sensor(String name, org.apache.shade.kafka.common.metrics.MetricConfig config, Sensor... parents) {
        return sensor(name, config, Long.MAX_VALUE, parents);
    }

    /**
     * Get or create a sensor with the given unique name and zero or more parent sensors. All parent sensors will
     * receive every value recorded with this sensor.
     * @param name The name of the sensor
     * @param config A default configuration to use for this sensor for metrics that don't have their own config
     * @param inactiveSensorExpirationTimeSeconds If no value if recorded on the Sensor for this duration of time,
     *                                        it is eligible for removal
     * @param parents The parent sensors
     * @return The sensor that is created
     */
    public synchronized Sensor sensor(String name, org.apache.shade.kafka.common.metrics.MetricConfig config, long inactiveSensorExpirationTimeSeconds, Sensor... parents) {
        Sensor s = getSensor(name);
        if (s == null) {
            s = new Sensor(this, name, parents, config == null ? this.config : config, time, inactiveSensorExpirationTimeSeconds);
            this.sensors.put(name, s);
            if (parents != null) {
                for (Sensor parent : parents) {
                    List<Sensor> children = childrenSensors.get(parent);
                    if (children == null) {
                        children = new ArrayList<>();
                        childrenSensors.put(parent, children);
                    }
                    children.add(s);
                }
            }
            log.debug("Added sensor with name {}", name);
        }
        return s;
    }

    /**
     * Remove a sensor (if it exists), associated metrics and its children.
     *
     * @param name The name of the sensor to be removed
     */
    public void removeSensor(String name) {
        Sensor sensor = sensors.get(name);
        if (sensor != null) {
            List<Sensor> childSensors = null;
            synchronized (sensor) {
                synchronized (this) {
                    if (sensors.remove(name, sensor)) {
                        for (org.apache.shade.kafka.common.metrics.KafkaMetric metric : sensor.metrics())
                            removeMetric(metric.metricName());
                        log.debug("Removed sensor with name {}", name);
                        childSensors = childrenSensors.remove(sensor);
                    }
                }
            }
            if (childSensors != null) {
                for (Sensor childSensor : childSensors)
                    removeSensor(childSensor.name());
            }
        }
    }

    /**
     * Add a metric to monitor an object that implements measurable. This metric won't be associated with any sensor.
     * This is a way to expose existing values as metrics.
     * @param metricName The name of the metric
     * @param measurable The measurable that will be measured by this metric
     */
    public void addMetric(MetricName metricName, org.apache.shade.kafka.common.metrics.Measurable measurable) {
        addMetric(metricName, null, measurable);
    }

    /**
     * Add a metric to monitor an object that implements measurable. This metric won't be associated with any sensor.
     * This is a way to expose existing values as metrics.
     * @param metricName The name of the metric
     * @param config The configuration to use when measuring this measurable
     * @param measurable The measurable that will be measured by this metric
     */
    public synchronized void addMetric(MetricName metricName, MetricConfig config, Measurable measurable) {
        org.apache.shade.kafka.common.metrics.KafkaMetric m = new org.apache.shade.kafka.common.metrics.KafkaMetric(new Object(),
                                        Utils.notNull(metricName),
                                        Utils.notNull(measurable),
                                        config == null ? this.config : config,
                                        time);
        registerMetric(m);
    }

    /**
     * Remove a metric if it exists and return it. Return null otherwise. If a metric is removed, `metricRemoval`
     * will be invoked for each reporter.
     *
     * @param metricName The name of the metric
     * @return the removed `KafkaMetric` or null if no such metric exists
     */
    public synchronized org.apache.shade.kafka.common.metrics.KafkaMetric removeMetric(MetricName metricName) {
        org.apache.shade.kafka.common.metrics.KafkaMetric metric = this.metrics.remove(metricName);
        if (metric != null) {
            for (MetricsReporter reporter : reporters)
                reporter.metricRemoval(metric);
        }
        return metric;
    }

    /**
     * Add a MetricReporter
     */
    public synchronized void addReporter(MetricsReporter reporter) {
        Utils.notNull(reporter).init(new ArrayList<>(metrics.values()));
        this.reporters.add(reporter);
    }

    synchronized void registerMetric(org.apache.shade.kafka.common.metrics.KafkaMetric metric) {
        MetricName metricName = metric.metricName();
        if (this.metrics.containsKey(metricName))
            throw new IllegalArgumentException("A metric named '" + metricName + "' already exists, can't register another one.");
        this.metrics.put(metricName, metric);
        for (MetricsReporter reporter : reporters)
            reporter.metricChange(metric);
    }

    /**
     * Get all the metrics currently maintained indexed by metricName
     */
    public Map<MetricName, KafkaMetric> metrics() {
        return this.metrics;
    }

    /**
     * This iterates over every Sensor and triggers a removeSensor if it has expired
     * Package private for testing
     */
    class ExpireSensorTask implements Runnable {
        public void run() {
            for (Map.Entry<String, Sensor> sensorEntry : sensors.entrySet()) {
                // removeSensor also locks the sensor object. This is fine because synchronized is reentrant
                // There is however a minor race condition here. Assume we have a parent sensor P and child sensor C.
                // Calling record on C would cause a record on P as well.
                // So expiration time for P == expiration time for C. If the record on P happens via C just after P is removed,
                // that will cause C to also get removed.
                // Since the expiration time is typically high it is not expected to be a significant concern
                // and thus not necessary to optimize
                synchronized (sensorEntry.getValue()) {
                    if (sensorEntry.getValue().hasExpired()) {
                        log.debug("Removing expired sensor {}", sensorEntry.getKey());
                        removeSensor(sensorEntry.getKey());
                    }
                }
            }
        }
    }

    /* For testing use only. */
    Map<Sensor, List<Sensor>> childrenSensors() {
        return Collections.unmodifiableMap(childrenSensors);
    }

    /**
     * Close this metrics repository.
     */
    @Override
    public void close() {
        if (this.metricsScheduler != null) {
            this.metricsScheduler.shutdown();
            try {
                this.metricsScheduler.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                // ignore and continue shutdown
            }
        }

        for (MetricsReporter reporter : this.reporters)
            reporter.close();
    }

}
