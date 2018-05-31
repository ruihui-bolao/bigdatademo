package com.hui.mulThread;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/12 11:48
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:    readfromkafka 的消费者
 */
public final class ConsumerKafka {

    /**
     * readfromkafka consumer : readfromkafka 消费者不是线程安全的。
     */
    private KafkaConsumer<String, String> consumer;

    /**
     * 创建一个固定线程数的线程池
     */
    private ExecutorService executorService;

    /**
     * 构造器初始化 readfromkafka consumer
     */
    public ConsumerKafka() {
        // 加载配置文件
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            // 加载配置文件
            inputStream = this.getClass().getClassLoader().getResourceAsStream("kafkaConsumer.properties");
            properties.load(inputStream);
            // 初始化 readfromkafka consumer
            consumer = new KafkaConsumer<String, String>(properties);
            // readfromkafka consumer 订阅的 topic
            consumer.subscribe(Arrays.asList("accesslog"));
        } catch (IOException e) {
            System.out.println("readfromkafka consumer 初始化失败： " + e);
        }
    }

    public void execute() {
        // 初始化指定线程数的线程池
        executorService = Executors.newFixedThreadPool(3);
        while (true) {
            // 读取数据，读取超时时间为100ms。
            ConsumerRecords<String, String> records = consumer.poll(10);
            if (null != records) {
                executorService.execute(new ConsumerThread(records, consumer));
            }
        }
    }

    public void shutdown() {
        try {
            if (consumer != null) {
                consumer.close();
            }
            if (executorService != null) {
                executorService.shutdown();
            }
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                System.out.println("Timeout");
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        ConsumerKafka consumerKafka = new ConsumerKafka();
        try {
            consumerKafka.execute();
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            consumerKafka.shutdown();
        }
    }

}
