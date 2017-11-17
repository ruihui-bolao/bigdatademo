package hive.jdbc.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.hive.HiveContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.hadoop.hive.HiveTemplate;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/9 13:57
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  通过HiveTemplate 对hive进行操作
 */
public class HiveDemo {

    public static void main(String[] args) {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("hive-context.xml");
        HiveTemplate hiveTemplate = (HiveTemplate) context.getBean("hiveTemplate");
        List<String> result = hiveTemplate.query("show tables");
        System.out.println(result);
        List<String> dataRes = hiveTemplate.query("show databases");
        System.out.println(dataRes);
        List<String> results = hiveTemplate.query("select * from etl.hq2aomenwb");
        for (String s : results) {
            System.out.println(s);
        }

    }

}
