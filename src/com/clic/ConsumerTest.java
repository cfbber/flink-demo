package com.clic;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Collections;
import java.util.Properties;

public class ConsumerTest {
    public static void main(String[] args) {


        Properties pros = new Properties();
        pros.put("bootstrap.servers", "192.168.62.128:9092");
        pros.setProperty("group.id", "tes44t23"); // 消费组 ID
        pros.setProperty("enable.auto.commit", "true"); // 是否启动自动提交消者偏移量
        pros.setProperty("auto.commit.interval.ms", "1000"); //  每间隔多长时间提交一次偏移量信息
        pros.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"); // 指定反序列化的key类型
        pros.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        pros.setProperty("auto.offset.reset", "earliest");


//        pros.put("offsets.topic.replication.factor", "1");

        KafkaConsumer<String, String> consumer2 = new KafkaConsumer<>(pros);

        //2. 指定消费者要监听那些topic

                consumer2.assign(Collections.singletonList(new TopicPartition("test", 0)));
        consumer2.seek(new TopicPartition("test", 0), 0L);
//        consumer2.subscribe(Arrays.asList("test"));
        while (true) {
            //3. 获取数据:
            ConsumerRecords<String, String> records = consumer2.poll(3537);

            for (ConsumerRecord<String, String> record : records) {
                //System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                System.out.println("从哪个分片中获取数据: "+record.partition() +";获取数据: "+record.value());
            }
        }

    }
}
