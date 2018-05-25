package com.hui.mulThread;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/12 10:49
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:    readfromkafka 生产者单例模式
 */
public class KafkaProducerSingleton {

    /**
     * log 日志
     */
    private static final Logger LOG = LoggerFactory.getLogger(KafkaProducerSingleton.class);

    /**
     * kafkaproducer  readfromkafka 生产这是线程安全的。
     */
    private static KafkaProducer<String, String> kafkaProducer;

    /**
     * 随机数
     */
    private Random random = new Random();

    /**
     * readfromkafka topic
     */
    private String topic;

    /**
     * readfromkafka 尝试发送次数
     */
    private int retry;

    private KafkaProducerSingleton() {

    }

    /**
     * 内部静态类
     */
    private static class LazyHandler {
        private static final KafkaProducerSingleton instance = new KafkaProducerSingleton();
    }

    /**
     * 单例模式，kafkaProducer 是线程安全的，可以多个线程共享一个实例
     *
     * @return
     */
    public static final KafkaProducerSingleton getInstance() {
        return LazyHandler.instance;
    }

    /**
     * readfromkafka 生产者进行初始化
     *
     * @param topic  readfromkafka topic
     * @param retry  发送不成功尝试次数
     */
    public void init(String topic, int retry) {
        // readfromkafka topic
        this.topic = topic;
        // 发送消息失败后，尝试的次数
        this.retry = retry;
        if (null == kafkaProducer){
            // 加载配置文件
            Properties props = new Properties();
            InputStream inputStream = null;
            try {
                inputStream = this.getClass().getClassLoader().getResourceAsStream("kafkaProducer.properties");
                props.load(inputStream);
                // 初始化 readfromkafka producer
                kafkaProducer = new KafkaProducer<String, String>(props);
            } catch (IOException e) {
                System.out.println("初始化失败：" + e);
            } finally {
                if (null != inputStream) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        System.out.println("加载文件 inputStream 关闭异常" + e);
                    }
                }
            }
        }
    }

    /**
     * 通过 kafkaProducer 发送消息
     *
     * @param message
     */
    public void sendKafkaMessage(final String message) {
        /**
         * 1、如果指定了某个分区,会只讲消息发到这个分区上
         * 2、如果同时指定了某个分区和key,则也会将消息发送到指定分区上,key不起作用
         * 3、如果没有指定分区和key,那么将会随机发送到topic的分区中
         * 4、如果指定了key,那么将会以hash<key>的方式发送到分区中
         */
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, random.nextInt(3), "", message);
        kafkaProducer.send(record, new Callback() {
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if (null != e) {
                    System.out.println("kafka发送消息失败：" + e);

                }
            }
        });
    }

    /**
     * 当kafka 消息失败后，重试。
     */
    private void retryKakfaMessage(final String retryMessage) {
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, random.nextInt(3), "", retryMessage);
        for (int i = 0; i < retry; i++) {
            try {
                kafkaProducer.send(record);
            } catch (Exception e) {
                System.out.println("kafka发送消息失败" + e);
            }
        }
    }

    /**
     * readfromkafka 的实例销毁
     */
    public void close(){
        if (null != kafkaProducer){
            kafkaProducer.close();
        }
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }
}
