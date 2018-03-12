package com.hui.mulThread;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/12 11:48
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:    kafka 的生产者
 */
public final class ConsumerKafka {

    /**
     *  kafka consumer : kafka 消费者不是线程安全的。
     */
    private  KafkaConsumer<String, String> consumer;

    private ExecutorService executorService;

    public ConsumerKafka() {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getClassLoader().getResourceAsStream("kafkaConsumer.properties");
            properties.load(inputStream);
            consumer = new KafkaConsumer<String, String>(properties);
            consumer.subscribe(Arrays.asList("accesslog"));
        } catch (IOException e) {
            System.out.println("kafka consumer 初始化失败： " + e);
        }
    }

    public void execute(){
         executorService = Executors.newFixedThreadPool(3);
         while (true){
             ConsumerRecords<String, String> records = consumer.poll(10);
             if (null != records){
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
