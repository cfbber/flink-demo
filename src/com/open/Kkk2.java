package com.open;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.shade.flink.streaming.connectors.kafka.FlinkKafkaConsumer09;
import org.apache.shade.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.shade.kafka.clients.consumer.ConsumerRecord;
import org.apache.shade.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

public class Kkk2 {
    public static void main(String[] args) throws Exception {

        Properties pros = new Properties();
        pros.put("bootstrap.servers", "192.168.62.128:9092");
        pros.setProperty("group.id", "tesx"); // 消费组 ID
        pros.setProperty("enable.auto.commit", "true"); // 是否启动自动提交消者偏移量
        pros.setProperty("auto.commit.interval.ms", "1000"); //  每间隔多长时间提交一次偏移量信息
        pros.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"); // 指定反序列化的key类型
        pros.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        pros.setProperty("auto.offset.reset", "earliest");


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        CheckpointConfig config = env.enableCheckpointing(30000).getCheckpointConfig();
        config.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        config.setCheckpointStorage("file:\\C:\\workspace\\flink-demo\\cp\\");

        FlinkKafkaConsumer09<String> fks = new FlinkKafkaConsumer09<>("test", new MyDes(), pros);

        SingleOutputStreamOperator<String> ds = env.addSource(
                fks).returns(String.class);
//        ds.print();

        ds.addSink(new TestSink());
//        Kafka09TableSource.bu

        env.execute();
    }

    static class MyDes implements KafkaDeserializationSchema<String> {


        @Override
        public boolean isEndOfStream(String nextElement) {
            return false;
        }

        @Override
        public String deserialize(ConsumerRecord<byte[], byte[]> record) throws Exception {
            StringDeserializer deserializer = new StringDeserializer();
            return deserializer.deserialize("xx-topic", record.value());
        }

        @Override
        public TypeInformation<String> getProducedType() {
            return null;
        }
    }
}
