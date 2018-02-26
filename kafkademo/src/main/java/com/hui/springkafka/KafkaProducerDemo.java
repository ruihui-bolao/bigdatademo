package com.hui.springkafka;


import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/2/26 11:45
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:    spring  Kafka 生产者
 */
public class KafkaProducerDemo {

    /**
     * kafka 生产者的初始化配置文件
     */
    private Properties properties;

    /**
     * 初始化的配置文件
     */
    protected String location = "producer.properties";

    /**
     * kafka topic
     */
    protected String destTopic;

    /**
     * kafka 集群配置
     */
    public String brokers;


    /**
     * kafkaProducer 初始化配置文件
     */
    public void setConf() {
        Properties props = new Properties();
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(location);
        try {
            props.load(inputStream);
            this.properties = props;
            this.properties.setProperty("metadata.broker.list", brokers);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * kafka 生产消息
     */
    public void produceMes(String mesage) {

        Producer<String, String> producer = null;

        // 如果 kafka 生产者为空，则初始化新的 producer
        if (producer == null) {
            producer = new Producer<String, String>(new ProducerConfig(properties));
        }
        producer.send(new KeyedMessage(destTopic, mesage));
        producer.close();
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getDestTopic() {
        return destTopic;
    }

    public void setDestTopic(String destTopic) {
        this.destTopic = destTopic;
    }

    public String getBrokers() {
        return brokers;
    }

    public void setBrokers(String brokers) {
        this.brokers = brokers;
    }


    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"kafka-producer.xml"});
        BeanFactory factory = context;
        KafkaProducerDemo kafkaProducer = (KafkaProducerDemo) factory.getBean("kafkaProducer");
        kafkaProducer.setConf();
        kafkaProducer.produceMes("hello");
    }

}
