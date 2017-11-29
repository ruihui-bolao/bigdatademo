import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/27 11:07
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   Spark RDD 的键值转换操作
 */
public class SparkRDD2 implements Serializable {

    /**
     * 输入文本路径
     */
    public static String path = null;
    public static JavaSparkContext sparkContext;

    /**
     * 初始化spark的相关操作
     */
    static {
        path = "sparkrdd/src/main/resources/spark.txt";
        SparkConf conf = new SparkConf();
        conf.setMaster("local").setAppName("SparkMapTest");
        sparkContext = new JavaSparkContext(conf);
    }


    /**
     * def mapValues[U](f: (V) => U): RDD[(K, U)]
     * 同基本转换操作的 map ,只不过 mapValues 是针对[k,v]的v值进行操作的进行 map 操作的。
     */
    public void testMapValues() {
        List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> javaRDD1 = sparkContext.parallelize(list1);
        List<String> list2 = Arrays.asList("a", "b", "c", "d", "e");
        JavaRDD<String> javaRDD2 = sparkContext.parallelize(list2);
        JavaPairRDD<Integer, String> rdd = javaRDD1.zip(javaRDD2);
        JavaPairRDD<Integer, String> res = rdd.mapValues(new Function<String, String>() {
            @Override
            public String call(String s) throws Exception {
                return s + "_";
            }
        });
        Map<Integer, String> resMap = res.collectAsMap();
        for (Map.Entry<Integer, String> entry : resMap.entrySet()) {
            System.out.println(entry.getKey() + "----" + entry.getValue());
        }
    }

    /**
     * def flatMapValues[U](f: (V) => TraversableOnce[U]): RDD[(K, U)]
     * 同基本转换操作中的flatMap，只不过flatMapValues是针对[K,V]中的V值进行flatMap操作。
     */
    public void testFlatMapValues() {
        List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> javaRDD1 = sparkContext.parallelize(list1);
        List<String> list2 = Arrays.asList("a,A", "b,B", "c,C", "d,D", "e,E");
        JavaRDD<String> javaRDD2 = sparkContext.parallelize(list2);
        JavaPairRDD<Integer, String> rdd = javaRDD1.zip(javaRDD2);
        Map<Integer, String> resMap = rdd.flatMapValues(new Function<String, Iterable<String>>() {
            @Override
            public Iterable<String> call(String s) throws Exception {
                String[] strings = s.split(",");
                List<String> list = Arrays.asList(strings);
                return list;
            }
        }).collectAsMap();
        for (Map.Entry<Integer, String> entry : resMap.entrySet()) {
            System.out.println(entry.getKey() + entry.getValue());
        }
    }

    /**
     * def combineByKey[C](createCombiner: (V) => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) => C): RDD[(K, C)]
     * def combineByKey[C](createCombiner: (V) => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) => C, numPartitions: Int): RDD[(K, C)]
     * def combineByKey[C](createCombiner: (V) => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) => C, partitioner: Partitioner, mapSideCombine: Boolean = true, serializer: Serializer = null): RDD[(K, C)]
     * 该函数用于将RDD[K,V]转换成RDD[K,C],这里的V类型和C类型可以相同也可以不同。
     * 其中的参数：
     * createCombiner：组合器函数，用于将V类型转换成C类型，输入参数为RDD[K,V]中的V,输出为C
     * mergeValue：合并值函数，将一个C类型和一个V类型值合并成一个C类型，输入参数为(C,V)，输出为C
     * mergeCombiners：合并组合器函数，用于将两个C类型值合并成一个C类型，输入参数为(C,C)，输出为C
     * numPartitions：结果RDD分区数，默认保持原有的分区数
     * partitioner：分区函数,默认为HashPartitioner
     * mapSideCombine：是否需要在Map端进行combine操作，类似于MapReduce中的combine，默认为true
     */
    public void testCombineByKey() {
        List<Integer> list1 = Arrays.asList(1, 2, 1, 2, 1);
        JavaRDD<Integer> javaRDD1 = sparkContext.parallelize(list1);
        List<String> list2 = Arrays.asList("A", "A", "B", "B", "C");
        JavaRDD<String> javaRDD2 = sparkContext.parallelize(list2);
        JavaPairRDD<String, Integer> rdd = javaRDD2.zip(javaRDD1);
        System.out.println(rdd.collect());
        List<Tuple2<String, String>> res = rdd.combineByKey(new Function<Integer, String>() {
            @Override
            public String call(Integer integer) throws Exception {
                String str = integer + "_";
                return str;
            }
        }, new Function2<String, Integer, String>() {
            @Override
            public String call(String s, Integer integer) throws Exception {
                String str = s + "@" + integer;
                return str;
            }
        }, new Function2<String, String, String>() {
            @Override
            public String call(String s, String s2) throws Exception {
                String str = s + "$" + s2;
                return str;
            }
        }).collect();
        System.out.println(res.toString());
    }

    /**
     * def groupByKey(): RDD[(K, Iterable[V])]
     * def groupByKey(numPartitions: Int): RDD[(K, Iterable[V])]
     * def groupByKey(partitioner: Partitioner): RDD[(K, Iterable[V])]
     * 该函数用于将RDD[K,V]中每个K对应的V值，合并到一个集合Iterable[V]中
     * 参数numPartitions用于指定分区数；
     * 参数partitioner用于指定分区函数；
     */
    public void testGroupByKey(){
        List<Integer> list1 = Arrays.asList(1, 2, 1, 2, 1);
        JavaRDD<Integer> javaRDD1 = sparkContext.parallelize(list1);
        List<String> list2 = Arrays.asList("A", "A", "B", "B", "C");
        JavaRDD<String> javaRDD2 = sparkContext.parallelize(list2);
        JavaPairRDD<String, Integer> rdd = javaRDD2.zip(javaRDD1);
        System.out.println(rdd.collect());
        System.out.println(rdd.groupByKey().collect());
    }

    /**
     * def reduceByKey(func: (V, V) => V): RDD[(K, V)
     * def reduceByKey(func: (V, V) => V, numPartitions: Int): RDD[(K, V)]
     * def reduceByKey(partitioner: Partitioner, func: (V, V) => V): RDD[(K, V)]
     * 该函数用于将RDD[K,V]中每个K对应的V值根据映射函数来运算。
     * 参数numPartitions用于指定分区数；
     * 参数partitioner用于指定分区函数；
     */
    public void testReduceByKey(){
        List<Integer> list1 = Arrays.asList(1, 2, 1, 2, 1);
        JavaRDD<Integer> javaRDD1 = sparkContext.parallelize(list1);
        List<String> list2 = Arrays.asList("A", "A", "B", "B", "C");
        JavaRDD<String> javaRDD2 = sparkContext.parallelize(list2);
        JavaPairRDD<String, Integer> rdd = javaRDD2.zip(javaRDD1);
        List<Tuple2<String, Integer>> resList = rdd.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }
        }).collect();
        System.out.println(resList);
    }

    /**
     * def reduceByKeyLocally(func: (V, V) => V): Map[K, V]
     * 该函数将RDD[K,V]中每个K对应的V值根据映射函数来运算，运算结果映射到一个Map[K,V]中，而不是RDD[K,V]。
     */
    public void testReduceByKeyLocally(){
        List<Integer> list1 = Arrays.asList(1, 2, 1, 2, 1);
        JavaRDD<Integer> javaRDD1 = sparkContext.parallelize(list1);
        List<String> list2 = Arrays.asList("A", "A", "B", "B", "C");
        JavaRDD<String> javaRDD2 = sparkContext.parallelize(list2);
        JavaPairRDD<String, Integer> rdd = javaRDD2.zip(javaRDD1);
        Map<String, Integer> resMap = rdd.reduceByKeyLocally(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }
        });
        System.out.println(resMap.toString());
    }

    public static void main(String[] args) {
        SparkRDD2 sparkRDD2 = new SparkRDD2();
        sparkRDD2.testReduceByKeyLocally();
    }

}
