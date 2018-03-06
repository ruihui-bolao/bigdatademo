package com.hui.springkafka;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/2/26 16:42
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   Spring + kafka 消费
 */
public class SpringConsumerMain {

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("classpath:kafka-consumer.xml");
        TimeUnit.HOURS.sleep(1);
    }
}
