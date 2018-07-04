package com.hui.mulThread;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/7/4 9:48
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   kafka 实现一个consumer 同时消费多个topic
        */
public class MineKafkaConsumer {

    private KafkaConsumer consumer;

    public void consumerTopic() {
        try {
            Properties properties = new Properties();
            InputStream inputStream = null;
            inputStream = this.getClass().getClassLoader().getResourceAsStream("kafkaConsumer.properties");
            properties.load(inputStream);
            // 初始化 readfromkafka consumer
            consumer = new KafkaConsumer<String, String>(properties);

            //topic lists
            ArrayList<String> topicLists = new ArrayList<String>();
            topicLists.add("hqu_test1");
            topicLists.add("hqu_test2");
            consumer.subscribe(topicLists);
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf(" topidc:%s =>>>offset = %d, key = %s, value = %s \n", record.topic(), record.offset(), record.key(), record.value());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MineKafkaConsumer mineKafkaConsumer = new MineKafkaConsumer();
        mineKafkaConsumer.consumerTopic();
    }

}
