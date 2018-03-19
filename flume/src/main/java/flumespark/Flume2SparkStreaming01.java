package flumespark;

import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.flume.FlumeUtils;
import org.apache.spark.streaming.flume.SparkFlumeEvent;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/19 15:31
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   flume 通过 push 推送数据
 */
public class Flume2SparkStreaming01 {

    public static void main(String[] args) {

        Configuration conf = new Configuration();
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster("local").setAppName("flume2spark");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        Duration duration = new Duration(Integer.parseInt("5000"));
        JavaStreamingContext javaStreamingContext = new JavaStreamingContext(sparkContext, duration);
        String host = "192.168.102.38";
        int port = 8080;
        JavaReceiverInputDStream<SparkFlumeEvent> flumeStream = FlumeUtils.createPollingStream(javaStreamingContext, host, port);

        flumeStream.foreachRDD(new Function<JavaRDD<SparkFlumeEvent>, Void>() {
            @Override
            public Void call(JavaRDD<SparkFlumeEvent> v1) throws Exception {
                long count = v1.count();
                System.out.printf("接受到的数据为%d条%n",count);
                v1.map(new Function<SparkFlumeEvent, String>() {
                    @Override
                    public String call(SparkFlumeEvent v1) throws Exception {
                        System.out.println(new String(v1.event().getBody().array(), "UTF-8"));
                        return new String(v1.event().getBody().array(), "UTF-8");
                    }
                });
                return null;
            }
        });

        System.out.println("开始启动 JavaStreaming 接受数据.....");
        javaStreamingContext.start();
        javaStreamingContext.awaitTermination();
    }
}
