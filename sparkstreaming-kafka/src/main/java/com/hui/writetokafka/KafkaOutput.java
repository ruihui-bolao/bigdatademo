package com.hui.writetokafka;

import kafka.producer.KeyedMessage;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/5/25 9:56
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  SparkStreaming 向kafka中写入数据（首先是从一个topic中读取数据，然后再向另一个topic发送数据）
 */
public class KafkaOutput implements Serializable {

    /**
     * 加载配置文件
     */
    private transient Properties properties = new Properties();

    /**
     * javaStreamingContext 配置文件
     */
    private transient JavaStreamingContext jssc;

    /**
     * spark 广播 kafka brokerList
     */
    private Broadcast<String> brokerListBroadcast;

    /**
     * spark 广播  kafka topic
     */
    private Broadcast<String> topicBroadcast;


    /**
     * 初始化操作
     */
    public KafkaOutput() {
        InputStream inputStream = KafkaOutput.class.getClassLoader().getResourceAsStream("spark.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化 JavaStreamingContext
     *
     * @return
     */
    public JavaStreamingContext initContext() {
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster(properties.getProperty("spark.master"));
        sparkConf.setAppName("streaming-kafka-output-app");
        sparkConf.get("spark.broadcast.factory", "org.apache.spark.broadcast.TorrentBroadcastFactory");
        this.jssc = new JavaStreamingContext(sparkConf, new Duration(5 * 1000));
        brokerListBroadcast = jssc.sparkContext().broadcast(properties.getProperty("metadata.broker.list"));
        topicBroadcast = jssc.sparkContext().broadcast("test1");
        return jssc;
    }

    /**
     * 从kafka中消费数据
     *
     * @param topic
     */
    public JavaDStream<String> readFromKafka(String topic) {
        KafkaConsumer kafkaConsumer = new KafkaConsumer(properties, jssc);
        JavaDStream<String> message = kafkaConsumer.readFromeKafka(topic);
        return message;
    }


    /**
     * 将 kakfa 读取的数据，返送到另一个topic。
     *
     * @param message
     */
    public void writeToKafka(JavaDStream<String> message) {
        message.foreachRDD(new VoidFunction<JavaRDD<String>>() {
            @Override
            public void call(JavaRDD<String> javaRDD) throws Exception {
                javaRDD.foreachPartition(new VoidFunction<Iterator<String>>() {
                    @Override
                    public void call(Iterator<String> iterator) throws Exception {
                        // 首先是得到单例的 kafka producer
                        KafkaProducer kafkaProducer = KafkaProducer.getInstance(brokerListBroadcast.getValue());
                        // 批量发送
                        ArrayList<KeyedMessage<String, String>> messageArrayList = new ArrayList<KeyedMessage<String, String>>();
                        while (iterator.hasNext()) {
                            messageArrayList.add(new KeyedMessage<String, String>(topicBroadcast.getValue(), iterator.next()));
                        }
                        kafkaProducer.send(messageArrayList);
/*                        // 逐条发送信息
                        while (iterator.hasNext()){
                            kafkaProducer.send(new KeyedMessage<String, String>(topicBroadcast.getValue(),iterator.next()));
                        }*/
                    }
                });
            }
        });
    }

    public static void main(String[] args) {

        KafkaOutput kafkaOutput = new KafkaOutput();
        JavaStreamingContext jssc = kafkaOutput.initContext();
        JavaDStream<String> message = kafkaOutput.readFromKafka("test");
        message.print();
        kafkaOutput.writeToKafka(message);
        jssc.start();
        jssc.awaitTermination();
    }

}
