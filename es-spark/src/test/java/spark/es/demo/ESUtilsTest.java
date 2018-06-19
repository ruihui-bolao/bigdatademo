package spark.es.demo;

import com.alibaba.fastjson.JSONObject;
import com.sdyc.ndmp.schedule.spark.SparkContextManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  EsUtils的测试类
 */
public class ESUtilsTest {
    /**
     *  设置es的index/type(es的索引index相当于数据库和类型type相当于数据表)
     */
    public static String esIndexs;
    /**
     * es 集群的服务器节点
     */
    public static String esNodes;

    /**
     * 任务类型
     */
    public static Integer taskType;

    /**
     * 数据类型
     */
    public static String dataType;

    static {
        esIndexs = "wzx_caricature/caricature";
        esNodes = "10.20.57.208,10.20.57.209,10.20.57.210,10.20.57.211";
        taskType = 4;
        dataType = "WQ_CARICATURE";
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        Map param = context.getBean("commonJobParam", Map.class);
        Configuration conf = (Configuration) param.get("conf");
        JavaSparkContext sparkContext = SparkContextManager.getSparkContext();
        JavaRDD<String> javaRDD = sparkContext.textFile("file:///C:/Users/sssd/Desktop/huitemp/a.json");
        // 向配置文件中追加es 的相关配置
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
        keyWords.put(ESUtils.OR_KEYS, "妙手天医在都市");
        keyWords.put(ESUtils.NOT_KEYS, "");
        final List<JSONObject> collect = ESUtils.queryDataFromEs(conf, sparkContext, keyWords, null, null, esIndexs, esNodes, taskType, false, null).collect();
        System.out.println("查询到的数据大小为：" + collect.size());
        for (JSONObject jsonObject : collect) {
            System.out.println(jsonObject);
        }
    }

}