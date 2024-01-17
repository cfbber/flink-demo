package com.open;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSink implements SinkFunction<String> {
    private static Logger LOG = LoggerFactory.getLogger(TestSink.class);

    @Override
    public void invoke(String value, Context context) throws Exception {
        LOG.info("Thread  " + Thread.currentThread().getId() + "---->" + value);
    }
}
