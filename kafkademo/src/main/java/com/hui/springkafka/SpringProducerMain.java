package com.hui.springkafka;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/2/26 16:32
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class SpringProducerMain {

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring-producer.xml");
        KafkaTemplate kafkaTemplate = ctx.getBean("kafkaTemplate", KafkaTemplate.class);
        for (int i = 1; i < 5; i++) {
            String msg = "msg-" + i;
            //向topicOne发送消息
            kafkaTemplate.send("testhui", msg);
            System.out.println("send msg  : " + msg);
        }
    }

}
