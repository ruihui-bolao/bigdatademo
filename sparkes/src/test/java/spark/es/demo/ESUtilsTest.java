package spark.es.demo;

import com.alibaba.fastjson.JSONObject;
import com.sdyc.ndmp.schedule.spark.SparkContextManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.util.HashMap;
import java.util.Map;


public class ESUtilsTest {

    public static String esIndexs;
    public static String esNodes;
    public static Integer taskType;
    public static String dataType;

    static {
        esIndexs = "wzx_app/app";
        esNodes = "192.168.1.235,192.168.1.238,192.168.1.237";
        taskType = 6;
        dataType = "WQ_APP";
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        Map param = context.getBean("commonJobParam", Map.class);
        Configuration conf = (Configuration) param.get("conf");
        JavaSparkContext sparkContext = SparkContextManager.getSparkContext();
        JavaRDD<String> javaRDD = sparkContext.textFile("file:///C:/Users/sssd/Desktop/huitemp/a.json");
        conf = ESUtils.buildWriteESConf(conf, "sectionUrn", esIndexs, esNodes);
        ESUtils.sparkWriteToEs(javaRDD, conf);
        System.out.println("保存数据完成");
    }

    @Test
    public void testEsQuery() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        Map param = context.getBean("commonJobParam", Map.class);
        Configuration conf = (Configuration) param.get("conf");
        JavaSparkContext sparkContext = SparkContextManager.getSparkContext();
        final HashMap<String, String> keyWords = new HashMap<String, String>(3);
        keyWords.put(ESUtils.AND_KEYS, "");
        // 加入抓取的关键字
        keyWords.put(ESUtils.OR_KEYS, "前夫大人请滚开");
        keyWords.put(ESUtils.NOT_KEYS, "");
        JavaRDD<JSONObject> javaRDD = ESUtils.queryDataFromEs(conf, sparkContext, keyWords, null, null, esIndexs, esNodes, taskType, false, null);
        long count = javaRDD.count();
        System.out.println(count);
    }

}