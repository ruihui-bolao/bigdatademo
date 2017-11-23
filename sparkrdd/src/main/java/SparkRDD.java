import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/22 15:36
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   总结sparkRDD的基本操作
 */
public class SparkRDD implements Serializable {

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
     * 测试 spark 的 map 操作:
     * 将一个RDD中的每个数据项，通过map中的函数映射变为一个新的元素
     * 输入分区和输出分区是一对一的，即：有所少个输入分区，就有多少个输出分区。
     */
    public void testMap() {
        JavaRDD<String> javaRDD = sparkContext.textFile(path);
        List<String> res = javaRDD.map(new Function<String, String>() {
            @Override
            public String call(String s) throws Exception {
                String[] strings = s.split(" ");
                return strings[1];
            }
        }).collect();
        for (String s : res) {
            System.out.println(s);
        }
    }

    /**
     * 测试 spark flatMap 操作：
     *  其中flatMap 会将字符串看成是一个字符数组，最后将所有的输出分区合并成一个字符数组
     */
    public void testFlatMap() {
        JavaRDD<String> javaRDD = sparkContext.textFile(path);
        List<String> resList = javaRDD.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterator<String> call(String s) throws Exception {
                String[] strings = s.split(" ");
                List<String> resList = Arrays.asList(strings);
                return resList.iterator();
            }
        }).collect();

        for (String s : resList) {
            System.out.println(s);
        }
    }

    /**
     * 测试 spark 的 distinct 操作
     * 其会对RDD中的元素进行去除操作
     */
    public void testDistinct(){
        JavaRDD<String> javaRDD = sparkContext.textFile(path);
        List<String> resList = javaRDD.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterator<String> call(String s) throws Exception {
                String[] strings = s.split(" ");
                List<String> resList = Arrays.asList(strings);
                return resList.iterator();
            }
        }).distinct().collect();
        for (String s : resList) {
            System.out.println(s);
        }
    }

    /**
     * 测试 spark 的 coalesce 的操作
     * 对JavaRDD 重新分区
     */
    public void testCoalesce(){
        JavaRDD<String> javaRDD = sparkContext.textFile(path);
        // 缓冲JavaRDD
        System.out.println(javaRDD.collect());
        //打印JavaRDD分区的大小（默认是2个）
        System.out.println(javaRDD.partitions().size());
        //对JavaRDD进行重分区
        System.out.println(javaRDD.coalesce(1).partitions().size());
        //对JavaRDD 进行重分区(),如果重分区的数目大于原来的分区数，那么必须指定shuffle参数为true，否则，分区数不变.
        System.out.println(javaRDD.coalesce(4).partitions().size());
        // 对JavaRDD进行重分区
        System.out.println(javaRDD.coalesce(4,true).partitions().size());
    }

    /**
     *  测试 spark 的repartition操作：
     *  该函数其实就是coalesce函数第二个参数为true的实现
     */
    public void testRepartition() {
        JavaRDD<String> javaRDD = sparkContext.textFile(path);
        System.out.println(javaRDD.partitions().size());
        System.out.println(javaRDD.repartition(4).partitions().size());
    }

    /**
     * 测试 spark 的 randomSplit 操作：
     * 该函数根据weights权重，将一个RDD切分成多个RDD.该权重参数为一个Double数组
     */
    public void testRandomSplit() {
        // 初始化 list
        List<Integer> data = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        // 利用spark parallelize 转换成 RDD
        JavaRDD<Integer> javaRDD = sparkContext.parallelize(data);
        System.out.println(javaRDD.partitions().size());
        // 初始化分配权重
        double[] weights = {1.0, 2.0, 3.0, 4.0};
        JavaRDD<Integer>[] javaRDDS = javaRDD.randomSplit(weights);
        System.out.println(javaRDDS.length);
        System.out.println(javaRDDS[0].collect());
        System.out.println(javaRDDS[1].collect());
        System.out.println(javaRDDS[2].collect());
        System.out.println(javaRDDS[3].collect());
    }

    /**
     * 测试 spark 的 glom 操作
     * 该函数是将RDD中每一个分区中类型为T的元素转换成Array[T],这样每一个分区就只有一个数组元素。
     */
    public void testGlom() {
        List<Integer> data = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        JavaRDD<Integer> javaRDD = sparkContext.parallelize(data,3);
        System.out.println(javaRDD.partitions().size());
        List<List<Integer>> lists = javaRDD.glom().collect();
        System.out.println(lists);
    }

    public static void main(String[] args) {
        SparkRDD sparkRDD = new SparkRDD();
        sparkRDD.testGlom();
    }

}
