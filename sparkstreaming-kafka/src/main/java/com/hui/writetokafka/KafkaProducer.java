package com.hui.writetokafka;

import com.clearspring.analytics.util.Preconditions;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/5/25 11:05
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   封装底层 kafka producer  实现序列化
 */
public class KafkaProducer implements Serializable{


    /**
     * kafka metadata.broker.list
     */
    public static final String METADATA_BROKER_LIST_KEY = "metadata.broker.list";

    /**
     * kafka serializer
     */
    public static final String SERIALIZER_CLASS_KEY = "serializer.class";

    /**
     * kafka serializer StringEncoder
     */
    public static final String SERIALIZER_CLASS_VALUE = "kafka.serializer.StringEncoder";

    /**
     * KafkaProducer 单例
     */
    private static KafkaProducer instance = null;

    /**
     * kafka 底层 producer
     */
    private transient Producer producer;

    private KafkaProducer(String brokerList) {
        // 判断 brokerList 是否为空
        Preconditions.checkArgument(StringUtils.isNotBlank(brokerList), "kafka brokerList is blank...");

        // 设置配置条件
        Properties properties = new Properties();
        properties.put(METADATA_BROKER_LIST_KEY, brokerList);
        properties.put(SERIALIZER_CLASS_KEY, SERIALIZER_CLASS_VALUE);
        properties.put("kafka.message.CompressionCodec", "1");
        properties.put("client.id", "streaming-kafka-output");

        // 初始化producer
        ProducerConfig producerConfig = new ProducerConfig(properties);
        this.producer = new Producer(producerConfig);
    }

    /**
     * 获取kafka实例
     * 其中synchronized 表示的是同步锁，一个线程在访问一个对象中的synchronized(this)同步代码块时，其他试图访问该对象的线程将被阻塞。
     *
     * @param brokerList
     * @return
     */
    public static synchronized KafkaProducer getInstance(String brokerList) {
        if (instance == null) {
            instance = new KafkaProducer(brokerList);
            System.out.println("init kafka sucesss ...");
        }
        return instance;
    }

    /**
     * 发送当条消息
     *
     * @param keyedMessage
     */
    public void send(KeyedMessage<String, String> keyedMessage) {
        producer.send(keyedMessage);
    }

    /**
     * 批量发送消息
     *
     * @param keyedMessageList
     */
    public void send(List<KeyedMessage<String, String>> keyedMessageList) {
        producer.send(keyedMessageList);
    }

    /**
     * producer 关闭
     */
    public void shutdown() {
        producer.close();
    }
}
