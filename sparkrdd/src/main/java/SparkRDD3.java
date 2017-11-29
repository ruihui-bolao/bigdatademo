import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/29 17:22
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description: Spark RDD action 的相关操纵
 */
public class SparkRDD3 implements Serializable{

    /**
     * 输入文本路径
     */
    public static String path = null;
    public static JavaSparkContext sparkContext;

    /**
     * 初始化 spark 相关操作
     */
    static {
        path = "sparkrdd/src/main/resources/spark.txt";
        SparkConf conf = new SparkConf();
        conf.setMaster("local").setAppName("SparkMapTest");
        sparkContext = new JavaSparkContext(conf);
    }

    /**
     * def first(): T
     * first返回RDD中的第一个元素，不排序。
     */
    public void testFirst(){
        List<String> list1 = Arrays.asList("A", "B", "C");
        JavaRDD<String> javaRDD1 = sparkContext.parallelize(list1);
        List<Integer> list2 = Arrays.asList(1, 2, 3);
        JavaRDD<Integer> javaRDD2 = sparkContext.parallelize(list2);
        JavaPairRDD<String, Integer> rdd = javaRDD1.zip(javaRDD2);
        Tuple2<String, Integer> res = rdd.first();
        System.out.println(res);
    }

    /**
     * def count(): Long
     * count返回RDD中的元素数量。
     */
    public void testCount(){
        List<String> list1 = Arrays.asList("A", "B", "C");
        JavaRDD<String> javaRDD1 = sparkContext.parallelize(list1);
        List<Integer> list2 = Arrays.asList(1, 2, 3);
        JavaRDD<Integer> javaRDD2 = sparkContext.parallelize(list2);
        JavaPairRDD<String, Integer> rdd = javaRDD1.zip(javaRDD2);
        long count = rdd.count();
        System.out.println("RDD的总数为：" + count);
    }

    /**
     * def reduce(f: (T, T) ⇒ T): T
     * 根据映射函数f，对RDD中的元素进行二元计算，返回计算结果。
     */
    public void testReduce(){
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        JavaRDD<Integer> javaRDD = sparkContext.parallelize(list);
        Integer sum = javaRDD.reduce(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) throws Exception {
                return integer + integer2;
            }
        });
        System.out.println("RDD的和为：" + sum);
    }

    /**
     * def collect(): Array[T]
     * collect用于将一个RDD转换成数组。
     */
    public void testCollect(){
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        JavaRDD<Integer> javaRDD = sparkContext.parallelize(list);
        List<Integer> res = javaRDD.collect();
        for (Integer re : res) {
            System.out.println(re);
        }
    }

    /**
     * def take(num: Int): Array[T]
     * take用于获取RDD中从0到num-1下标的元素，不排序。
     */
    public void testTake(){
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        JavaRDD<Integer> javaRDD = sparkContext.parallelize(list);
        System.out.println(javaRDD.take(1));
        System.out.println(javaRDD.take(3));
    }

    /**
     * def top(num: Int)(implicit ord: Ordering[T]): Array[T]
     * top函数用于从RDD中，按照默认（降序）或者指定的排序规则，返回前num个元素。
     */
    public void testTop(){
        List<Integer> list = Arrays.asList(1, 5, 3, 7, 2, 6, 4);
        JavaRDD<Integer> javaRDD = sparkContext.parallelize(list);
        System.out.println(javaRDD.top(3));
        System.out.println(javaRDD.top(7));
    }

    /**
     * def takeOrdered(num: Int)(implicit ord: Ordering[T]): Array[T]
     * takeOrdered和top类似，只不过以和top相反的顺序返回元素。
     */
    public void testTakeOrdered(){
        List<Integer> list = Arrays.asList(1, 5, 3, 7, 2, 6, 4);
        JavaRDD<Integer> javaRDD = sparkContext.parallelize(list);
        System.out.println(javaRDD.takeOrdered(3));
        System.out.println(javaRDD.takeOrdered(7));
    }

    // TODO: 2017/11/29 aggregate:
    // def aggregate[U](zeroValue: U)(seqOp: (U, T) ⇒ U, combOp: (U, U) ⇒ U)(implicit arg0: ClassTag[U]): U
    //aggregate用户聚合RDD中的元素，先使用seqOp将RDD中每个分区中的T类型元素聚合成U类型，
    // 再使用combOp将之前每个分区聚合后的U类型聚合成U类型，特别注意seqOp和combOp都会使用zeroValue的值，
    // zeroValue的类型为U。

    // TODO: 2017/11/29 fold
    //def fold(zeroValue: T)(op: (T, T) ⇒ T): T
    //fold是aggregate的简化，将aggregate中的seqOp和combOp使用同一个函数op。

    /**
     * def lookup(key: K): Seq[V]
     * lookup用于(K,V)类型的RDD,指定K值，返回RDD中该K对应的所有V值。
     */
    public void testLookup(){
        List<String> list1 = Arrays.asList("A", "B", "C");
        JavaRDD<String> javaRDD1 = sparkContext.parallelize(list1);
        List<Integer> list2 = Arrays.asList(1, 2, 3);
        JavaRDD<Integer> javaRDD2 = sparkContext.parallelize(list2);
        JavaPairRDD<String, Integer> rdd = javaRDD1.zip(javaRDD2);
        List<Integer> a = rdd.lookup("B");
        System.out.println("查询的结果为：" + a);
    }

    /**
     * def countByKey(): Map[K, Long]
     * countByKey用于统计RDD[K,V]中每个K的数量。
     */
    public void testCountByKey(){
        List<String> list1 = Arrays.asList("A", "A", "B", "B", "B", "C");
        JavaRDD<String> javaRDD1 = sparkContext.parallelize(list1);
        List<Integer> list2 = Arrays.asList(1, 2, 1, 2, 3, 1);
        JavaRDD<Integer> javaRDD2 = sparkContext.parallelize(list2);
        JavaPairRDD<String, Integer> rdd = javaRDD1.zip(javaRDD2);
        Map<String, Long> map = rdd.countByKey();
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            System.out.println("获取数据的key:" + entry.getKey() + "-----" +entry.getValue());
        }
    }

    // TODO: 2017/11/29  foreach
    // def foreach(f: (T) ⇒ Unit): Unit
    // foreach用于遍历RDD,将函数f应用于每一个元素。
    // 但要注意，如果对RDD执行foreach，只会在Executor端有效，而并不是Driver端。
    // 比如：rdd.foreach(println)，只会在Executor的stdout中打印出来，Driver端是看不到的。

    // TODO: 2017/11/29 foreachPartition
    // def foreachPartition(f: (Iterator[T]) ⇒ Unit): Unit
    // foreachPartition和foreach类似，只不过是对每一个分区使用f。


    public static void main(String[] args) {
        SparkRDD3 sparkRDD3 = new SparkRDD3();
        sparkRDD3.testCountByKey();
    }

}
