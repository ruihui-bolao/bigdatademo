package com.hui.spark.firstdemo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/30 10:45
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  Spark 简单的例子: 词频统计
 */
public class SparkDemo {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf();
        conf.setMaster("local").setAppName("JavaWordCount");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        JavaRDD<String> javaRDD = sparkContext.textFile("file:///C:/Users/sssd/Desktop/spark.txt");
        Map<String, Integer> wordCountMap = javaRDD.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String s) throws Exception {
                return new Tuple2<String, Integer>(s, 1);
            }
        }).reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }
        }).collectAsMap();
        for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
            System.out.println("统计结果为：" + entry.getKey() + entry.getValue());
        }
    }
}
