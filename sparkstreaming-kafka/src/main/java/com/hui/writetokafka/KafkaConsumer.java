package com.hui.writetokafka;

import kafka.serializer.StringDecoder;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/5/25 13:57
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:    SparkStreaming  消费kafka数据
 */
public class KafkaConsumer implements Serializable {

    /**
     * 相关配置文件
     */
    private Properties properties = new Properties();

    /**
     * 注意javaContex不需要序列化!!!
     */
    private transient JavaStreamingContext jssc;

    /**
     * 构造器初始化
     * @param properties
     * @param jssc
     */
    public KafkaConsumer(Properties properties, JavaStreamingContext jssc) {
        this.properties = properties;
        this.jssc = jssc;
    }

    /**
     * kafkaStreaming 从kafka 中读取数据
     *
     * @param topic kafka topic
     */
    public JavaDStream<String> readFromeKafka(String topic) {

        // 添加 kafka topic
        HashSet<String> topicSet = new HashSet<String>();
        topicSet.add(topic);

        // 添加kafka的相关配置文件
        HashMap<String, String> kafkaParma = new HashMap<String, String>();
        kafkaParma.put("metadata.broker.list", properties.getProperty("metadata.broker.list"));
        kafkaParma.put("group.id", properties.getProperty("group.id"));

        // 从kafka中消费数据
        JavaPairInputDStream<String, String> pairInputDStream = KafkaUtils.createDirectStream(jssc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                kafkaParma,
                topicSet);

        JavaDStream<String> message = pairInputDStream.map(new Function<Tuple2<String, String>, String>() {
            @Override
            public String call(Tuple2<String, String> v1) throws Exception {
                return v1._2();
            }
        });
        message.print();
        return message;
    }

}
