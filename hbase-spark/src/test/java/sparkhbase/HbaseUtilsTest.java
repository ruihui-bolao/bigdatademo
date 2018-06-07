package sparkhbase;

import com.sdyc.ndmp.schedule.spark.SparkContextManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

public class HbaseUtilsTest {

    // 测试 hbaseutil 的 spark 相关操作
    @Test
    public void test() throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("legoContext.xml");
        Map param = context.getBean("commonJobParam", Map.class);
        Configuration conf = (Configuration) param.get("conf");
        JavaSparkContext sparkContext = SparkContextManager.getSparkContext();
        JavaRDD<String> javaRDD = sparkContext.textFile("file:///E:/Work/bigdatademo/sparkhbase/src/main/resources/files/test1.json");
        Configuration hbaseConf = HbaseUtils.buildWriteHbaseConf(conf, "testhui");
        HbaseUtils.sparkWriteToHbase(javaRDD, hbaseConf, "movie");
        System.out.println("保存数据完成");
    }

}