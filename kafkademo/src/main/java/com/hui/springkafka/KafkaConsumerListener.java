package com.hui.springkafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/2/26 16:38
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */

@Component("kafkaConsumerListener")
public class KafkaConsumerListener  implements MessageListener<String, String> {


    public void onMessage(ConsumerRecord<String, String> record) {
        String topic = record.topic();
        String key = record.key();
        String val = record.value();
        long offset = record.offset();
        int partition = record.partition();
        System.out.printf("receive msg -- topic:%s   key:%s  val:%s  offset:%s  partition:%s \r\n",topic,key,val,offset,partition);
    }
}
