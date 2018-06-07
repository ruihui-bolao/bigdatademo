package demo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.hive.HiveContext;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/10 17:26
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  通过spark conf 读取 hive ，并返回JavaRdd.
 */
public class SparkHive {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf();
        conf.setMaster("local").setAppName("SparkHive");
        JavaSparkContext javaSparkContext = new JavaSparkContext(conf);
        HiveContext hiveContext = new HiveContext(javaSparkContext.sc());
        hiveContext.sql("use copyright");
        JavaRDD<Row> show_databases = hiveContext.sql("select * from wq_app_data").toJavaRDD();
        List<Row> rows = show_databases.collect();
        System.out.println("数据库的大小为：" + rows.size());
        for (Row row : rows) {
            System.out.println(row.toString());
        }
    }
}
