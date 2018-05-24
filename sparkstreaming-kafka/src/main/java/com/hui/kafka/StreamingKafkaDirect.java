package com.hui.kafka;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.HasOffsetRanges;
import org.apache.spark.streaming.kafka.OffsetRange;
import scala.Tuple2;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/5/23 11:41
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   spark streaming direct kafka 读取kafka中的数据（进行词频统计）
 */
public class StreamingKafkaDirect {

    /**
     * 用来加载配置文件
     */
    final private static Properties properties = new Properties();

    /**
     * 初始化 streaming 上下文
     *
     * @return
     */

    public StreamingKafkaDirect() {

        InputStream kafkaProperties = StreamingKafkaDirect.class.getClassLoader().getResourceAsStream("spark.properties");
        try {
            properties.load(kafkaProperties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JavaStreamingContext creatStreamingContext() {

        // 初始化 spark 配置文件
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster(properties.getProperty("spark.master")).setAppName("SparkStreamingDirectKafka");
        sparkConf.set("spark.streaming.kafka.maxRatePerPartition", properties.getProperty("spark.streaming.kafka.maxRatePerPartition"));
        sparkConf.set("spark.serializer", properties.getProperty("spark.serializer"));
        JavaStreamingContext javaStreamingContext = new JavaStreamingContext(sparkConf, Durations.seconds(10));

        //初始化 kafkaParams 配置文件和消费主题topic
        HashMap<String, String> kafkaParams = new HashMap<String, String>();
        kafkaParams.put("metadata.broker.list", properties.getProperty("metadata.broker.list"));
        kafkaParams.put("group.id", properties.getProperty("group.id"));
        kafkaParams.put("auto.offset.reset", "smallest");
        // kafka topics
        HashSet<String> topics = new HashSet<String>();
        topics.add("test");

        // 从kafka 中获取数据
        final JavaKafkaManager javaKafkaManager = new JavaKafkaManager(kafkaParams);
        JavaInputDStream<String> message = javaKafkaManager.creatDirctStream(javaStreamingContext, kafkaParams, topics);

        // 得到 rdd 各个分区对应的offset, 并保存到 offsetRanges 中
        final AtomicReference<OffsetRange[]> offsetRanges = new AtomicReference<OffsetRange[]>();
        JavaDStream<String> words = message.transform(new Function<JavaRDD<String>, JavaRDD<String>>() {
            @Override
            public JavaRDD<String> call(JavaRDD<String> rdd) throws Exception {
                // 用来存放offset.
                OffsetRange[] offsets = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
                offsetRanges.set(offsets);
                System.out.println("@@@@ 获取的offset大小为：" + offsets.length);
                for (OffsetRange offset : offsets) {
                    System.out.println("@@@@ 对应的每个offset为：" + offset.topic() + "|" + offset.count() + "|" + offset.fromOffset() +"|" + offset.toString());
                }
                List<String> collect = rdd.collect();
                System.out.println("**** 获取kafka的数据为：" + collect);
                return rdd;
            }
        });

        // 词语计数并更新kafka
        JavaPairDStream<String, Integer> pairDStream = words.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String word) throws Exception {
                javaKafkaManager.updateZKOffsets(offsetRanges.get());
                return new Tuple2<String, Integer>(word, 1);
            }
        });
        // reduce
        JavaPairDStream<String, Integer> wordcount = pairDStream.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }
        });
        wordcount.print();
        return javaStreamingContext;
    }

    public static void main(String[] args) {

        StreamingKafkaDirect streamingKafkaDirect = new StreamingKafkaDirect();
        JavaStreamingContext jsc = streamingKafkaDirect.creatStreamingContext();
        jsc.start();
        jsc.awaitTermination();
        jsc.close();
    }

}
