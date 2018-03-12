package com.hui.mulThread;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/12 12:02
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   kafka consumer 多线程
 */
public class ConsumerThread implements Runnable {

    /**
     * consumer records
     */
    private ConsumerRecords<String, String> records;

    /**
     *  kafka consumer
     */
    private KafkaConsumer<String, String> consumer;

    /**
     *  构造器，初始化ConsumerThread ,
     * @param records  consumer 消费到的数据
     * @param consumer kafka consumer
     */
    public ConsumerThread(ConsumerRecords<String,String> records, KafkaConsumer<String,String> consumer){
        this.records = records;
        this.consumer = consumer;
    }

    public void run() {
        // 对kafka consumer 接收到的数据遍历分区
        for (TopicPartition partition : records.partitions()) {
            // 获取对应分区的 consumer 记录
            List<ConsumerRecord<String, String>> partitionRecords = this.records.records(partition);
            // 对分区内数据进行遍历
            for (ConsumerRecord<String, String> record : partitionRecords) {
                System.out.println("当前线程:" + Thread.currentThread() + ","
                        + "偏移量:" + record.offset() + "," + "主题:"
                        + record.topic() + "," + "分区:" + record.partition()
                        + "," + "获取的消息:" + record.value());
            }
            // 手动更新 offset
            long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
//            consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
        }
    }



}
