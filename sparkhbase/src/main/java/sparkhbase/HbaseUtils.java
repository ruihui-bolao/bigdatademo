package sparkhbase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/12/21 8:56
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:     Spark hbase 的工具类 （ 主要为 spark rdd 写入数据到 hbase ）
 */
public class HbaseUtils {

    /**
     * hbase conf 配置文件 写入到 spark conf
     *
     * @param conf      未初始化 hbase conf 的配置文件
     * @param tableName hbase 表名
     * @return
     */
    public static Configuration buildWriteHbaseConf(Configuration conf, String tableName) {
        // 设置 hbase 的数据表名
        conf.set(TableOutputFormat.OUTPUT_TABLE, tableName);
        return conf;
    }

    /**
     * spark rdd 写入数据到 hbase（通过 saveAsNewAPIHadoopDataset 将数据保存到 hbase 中）
     *
     * @param javaRDD  源数据 javardd
     * @param conf     spark conf 配置文件
     * @param datatype 数据类型
     * @throws Exception
     */
    public static void sparkWriteToHbase(JavaRDD<String> javaRDD, Configuration conf, final String datatype) throws Exception {


        Job job = new Job(conf);
        job.setOutputKeyClass(NullWritable.class);        // 设置输出时的 key 值类型
        job.setOutputValueClass(Put.class);               // 设置输出时的 value 值类,hbase中数据的插入和取出都使用的是 Put 类对象
        job.setOutputFormatClass(TableOutputFormat.class);   // mapreduce 的输出导入到hbase 有多种方式可以实现，TableOutputFormat 就是其中的一种，但是其效率并不好，
        // 大量数据转载到hbase 的话最好是生成Hfile 后再导入到hbase ,hfile 是 hbase 内部存储表示形式，所以效率速度很快。
        javaRDD.mapPartitionsToPair(new PairFlatMapFunction<Iterator<String>, NullWritable, Put>() {
            @Override
            public Iterable<Tuple2<NullWritable, Put>> call(Iterator<String> iterator) throws Exception {
                LinkedList<Tuple2<NullWritable, Put>> list = new LinkedList<Tuple2<NullWritable, Put>>();
                while (iterator.hasNext()) {
                    try {
                        final String next = iterator.next();
                        JSONObject jsonObject = (JSONObject) JSON.parse(next.trim());
                        Put put = new Put(jsonObject.getString("sectionUrn").getBytes());
                        put.add("info".getBytes(), "datatype".getBytes(), datatype.getBytes());
                        Set<Map.Entry<String, Object>> entrySet = jsonObject.entrySet();
                        // 对原始数据的 json 进行遍历
                        for (Map.Entry<String, Object> entry : entrySet) {
                            final Object entryValue = entry.getValue();
                            // 根据 value 的类型转换byte
                            if (entryValue != null) {
                                if (entryValue instanceof String) {
                                    put.add("info".getBytes(), entry.getKey().getBytes(), ((String) entryValue).getBytes());
                                }
                            }
                        }
                        list.add(new Tuple2<NullWritable, Put>(NullWritable.get(), put));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                System.out.println("list 的大小 " + list.size());
                return list;
            }
        }).saveAsNewAPIHadoopDataset(job.getConfiguration());
    }


}
