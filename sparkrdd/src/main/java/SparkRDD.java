import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.FlatMapFunction2;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
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
     * def randomSplit(weights: Array[Double], seed: Long = Utils.random.nextLong): Array[RDD[T]]
     * 该函数根据weights权重，将一个RDD切分成多个RDD.该权重参数为一个Double数组,第二个参数为random的种子，基本可忽略。
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

    /**
     * def union(other: RDD[T]): RDD[T]
     * 该函数比较简单，就是将两个RDD进行合并，不去重。
     */
    public void testUnion(){
        List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> javaRDD1 = sparkContext.parallelize(data);
        List<Integer> data2 = Arrays.asList(6, 7, 8, 9, 10);
        JavaRDD<Integer> javaRDD2 = sparkContext.parallelize(data2);
        System.out.println(javaRDD1.collect());
        System.out.println(javaRDD2.collect());
        System.out.println(javaRDD1.union(javaRDD2).collect());
    }

    /**
     * def intersection(other: RDD[T]): RDD[T]
     *def intersection(other: RDD[T], numPartitions: Int): RDD[T]
     *def intersection(other: RDD[T], partitioner: Partitioner)(implicit ord: Ordering[T] = null): RDD[T]
     * 该函数返回两个RDD的交集，并且去重。参数numPartitions指定返回的RDD的分区数。参数partitioner用于指定分区函数
     */
    public void testIntersection(){
        List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> javaRDD1 = sparkContext.parallelize(data);
        List<Integer> data2 = Arrays.asList(3, 4, 5, 5, 6);
        JavaRDD<Integer> javaRDD2 = sparkContext.parallelize(data2);
        JavaRDD<Integer> javaRDD3 = javaRDD1.intersection(javaRDD2);
        System.out.println(javaRDD3.collect());
    }

    /**
     * def subtract(other: RDD[T]): RDD[T]
     * def subtract(other: RDD[T], numPartitions: Int): RDD[T]
     * def subtract(other: RDD[T], partitioner: Partitioner)(implicit ord: Ordering[T] = null): RDD[T]
     * 该函数类似于intersection，但返回在RDD中出现，并且不在otherRDD中出现的元素，不去重。参数含义同intersection
     */
    public void testSubtract(){
        List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> javaRDD1 = sparkContext.parallelize(data);
        List<Integer> data2 = Arrays.asList(3, 4, 5, 5, 6);
        JavaRDD<Integer> javaRDD2 = sparkContext.parallelize(data2);
        System.out.println(javaRDD1.subtract(javaRDD2).collect());
    }

    /**
     * def mapPartitions[U](f: (Iterator[T]) => Iterator[U], preservesPartitioning: Boolean = false)(implicit arg0: ClassTag[U]): RDD[U]
     * 该函数和map函数类似，只不过映射函数的参数由RDD中的每一个元素变成了RDD中每一个分区的迭代器。
     * 如果在映射的过程中需要频繁创建额外的对象，使用mapPartitions要比map高效的过。
     * 比如，将RDD中的所有数据通过JDBC连接写入数据库，如果使用map函数，可能要为每一个元素都创建一个connection，
     * 这样开销很大，如果使用mapPartitions，那么只需要针对每一个分区建立一个connection。
     * 参数preservesPartitioning表示是否保留父RDD的partitioner分区信息。
     */
    public void testMapPartitions(){
        List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> javaRDD1 = sparkContext.parallelize(data);
        JavaRDD<Integer> integerJavaRDD = javaRDD1.mapPartitions(new FlatMapFunction<Iterator<Integer>, Integer>() {
            @Override
            public Iterator<Integer> call(Iterator<Integer> integerIterator) throws Exception {
                LinkedList<Integer> lists = new LinkedList<Integer>();
                while (integerIterator.hasNext()) {
                    lists.add(integerIterator.next());
                }
                return lists.iterator();
            }
        });
        System.out.println(integerJavaRDD.collect());
    }

    /**
     * def mapPartitionsWithIndex[U](f: (Int, Iterator[T]) => Iterator[U], preservesPartitioning: Boolean = false)(implicit arg0: ClassTag[U]): RDD[U]
     * 函数作用同mapPartitions，不过提供了两个参数，第一个参数为分区的索引。
     */
    public void testMapPartitionsWithIndex(){
        List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> javaRDD1 = sparkContext.parallelize(data,3);
        JavaRDD<String> res = javaRDD1.mapPartitionsWithIndex(new Function2<Integer, Iterator<Integer>, Iterator<String>>() {
            @Override
            public Iterator<String> call(Integer integer, Iterator<Integer> integerIterator) throws Exception {
                LinkedList<String> list = new LinkedList<String>();
                while (integerIterator.hasNext()) {
                    list.add(Integer.toString(integer) + "|" + integerIterator.next());
                }
                return list.iterator();
            }
        }, false);
        System.out.println(res.collect());
    }

    /**
     * def zip[U](other: RDD[U])(implicit arg0: ClassTag[U]): RDD[(T, U)]
     * zip函数用于将两个RDD组合成Key/Value形式的RDD,这里默认两个RDD的partition数量以及元素数量都相同，否则会抛出异常。
     */
    public void testZip(){
        List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> javaRDD1 = sparkContext.parallelize(list1);

        List<String> list2 = Arrays.asList("a", "b", "c", "d", "e");
        JavaRDD<String> javaRDD2 = sparkContext.parallelize(list2);

        System.out.println(javaRDD1.zip(javaRDD2).collect());
    }

    /**
     * zipPartitions函数将多个RDD按照partition组合成为新的RDD，该函数需要组合的RDD具有相同的分区数，但对于每个分区内的元素数量没有要求。
     * 该函数有好几种实现，可分为三类：
     * 参数是一个RDD
     * def zipPartitions[B, V](rdd2: RDD[B])(f: (Iterator[T], Iterator[B]) => Iterator[V])(implicit arg0: ClassTag[B], arg1: ClassTag[V]): RDD[V]
     * def zipPartitions[B, V](rdd2: RDD[B], preservesPartitioning: Boolean)(f: (Iterator[T], Iterator[B]) => Iterator[V])(implicit arg0: ClassTag[B], arg1: ClassTag[V]): RDD[V]
     * 这两个区别就是参数preservesPartitioning，是否保留父RDD的partitioner分区信息
     */
    public void testZipPartitions(){
        final List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> javaRDD1 = sparkContext.parallelize(list1);

        List<String> list2 = Arrays.asList("a", "b", "c", "d", "e");
        JavaRDD<String> javaRDD2 = sparkContext.parallelize(list2);

        JavaRDD<String> res1 = javaRDD1.mapPartitionsWithIndex(new Function2<Integer, Iterator<Integer>, Iterator<String>>() {
            @Override
            public Iterator<String> call(Integer integer, Iterator<Integer> integerIterator) throws Exception {
                LinkedList<String> list = new LinkedList<String>();
                while (integerIterator.hasNext()) {
                    list.add(Integer.toString(integer) + "|" + integerIterator.next());
                }
                return list.iterator();
            }
        }, false);
        System.out.println(res1.collect());

        JavaRDD<String> res2 = javaRDD2.mapPartitionsWithIndex(new Function2<Integer, Iterator<String>, Iterator<String>>() {
            @Override
            public Iterator<String> call(Integer integer, Iterator<String> iterator) throws Exception {
                LinkedList<String> list = new LinkedList<String>();
                while (iterator.hasNext()) {
                    list.add(Integer.toString(integer) + "|" + iterator.next());
                }
                return list.iterator();
            }
        }, false);
        System.out.println(res2.collect());

        JavaRDD<String> rdd = javaRDD1.zipPartitions(javaRDD2, new FlatMapFunction2<Iterator<Integer>, Iterator<String>, String>() {
            @Override
            public Iterator<String> call(Iterator<Integer> iterator, Iterator<String> iterator2) throws Exception {
                LinkedList<String> lists = new LinkedList<String>();
                while (iterator.hasNext() && iterator2.hasNext()) {
                    lists.add(iterator.next() + "_" + iterator2.next());
                }
                return lists.iterator();
            }
        });
        System.out.println(rdd.collect());
    }

    public static void main(String[] args) {
        SparkRDD sparkRDD = new SparkRDD();
        sparkRDD.testZipPartitions();
    }

}
