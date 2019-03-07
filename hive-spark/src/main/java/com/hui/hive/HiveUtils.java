package com.hui.hive;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.hive.HiveContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.hadoop.hive.HiveTemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/2/11 12:01
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   hiveUtils工具类，实现hive 数据读取和写入
 */
public class HiveUtils implements Serializable {

    /**
     * hive use database shell 语句
     */
    public String useHiveDB = "";

    /**
     * spark 的上下文
     */
    public transient JavaSparkContext sparkContext;

    public static transient HiveContext hiveContext = null;

    /**
     * hiveTemplate
     */
    protected static transient HiveTemplate hiveTemplate;

    /**
     * 静态代码块，初始化 hiveTemplate
     */
    static {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
        hiveTemplate = (HiveTemplate) context.getBean("hiveTemplate");
    }

    /**
     * 初始化使用数据库的语句
     *
     * @param dataBaseName hive 中执行操作的数据库
     * @param sparkContext  spark 上下文机制
     */
    public HiveUtils(String dataBaseName, JavaSparkContext sparkContext) {
        this.useHiveDB = "use " + dataBaseName + ";";
        this.sparkContext = sparkContext;
    }

    /**
     * 从hive中查询数据,
     */
    public JavaRDD<String> getRddData(String hiveDbName , String sqlText) {

        if (hiveContext == null){
            hiveContext = new HiveContext(sparkContext.sc());
        }else if (hiveContext.sparkContext() != sparkContext.sc()){
            hiveContext = null;
            hiveContext = new HiveContext(sparkContext.sc());
        }

        hiveContext.sql("use " + hiveDbName);
        DataFrame dataFrame = hiveContext.sql(sqlText);
        JavaRDD<String> javaRDD = dataFrame.toJavaRDD().flatMap(new FlatMapFunction<Row, String>() {
            @Override
            public Iterable<String> call(Row row) throws Exception {
                int length = row.length();
                ArrayList<String> list = new ArrayList<String>(length);
                for (int i = 0; i < length; ++i) {
                    String rowTemp = row.getString(i);
                    JSONObject jsonObject = null;
                    if (rowTemp != null) {
                        try {
                            jsonObject = JSON.parseObject(rowTemp);
                        } catch (Exception var8) {
                            continue;
                        }

                        if (jsonObject != null) {
                            list.add(jsonObject.toJSONString());
                        }
                    }
                }
                return list;
            }
        });
        return javaRDD;
    }


    /**
     * 将hdfs上的数据加载到hive中 ( 注意将 hdfs 上的数据加载到 hive 数据表时， hdfs 上的数据也即将消失。)
     *
     * @param data      原始数据
     * @param tableName hive中存放的表名
     * @param filePath  原始数据存放的路径
     * @return
     */
    public boolean loadData2Hive(String data, String tableName, String filePath) {

        boolean res = false;
        try {
            List<String> query = hiveTemplate.query(useHiveDB + "show partitions " + tableName);
            HashSet<String> onlinePartitions = new HashSet<String>();

            // 将从 hive 中查询到的结果放在set中,用来判断是否存在
            if (null != query && query.size() > 0) {
                for (String eachQuery : query) {
                    String[] temp = eachQuery.split("/");
                    onlinePartitions.add(temp[0].split("=")[1] + temp[1].split("=")[1]);
                }
            }

            // 获取数据中对应的字段 projectId 和 batchId
            JSONObject jsonObject = (JSONObject) JSON.parse(data);
            String projectId = jsonObject.getString("projectId");
            String batchId = jsonObject.getString("batchId");

            // 判断该数据的分区是否存在，如果不存在先创建分区
            if (!onlinePartitions.contains(projectId + batchId)) {
                String addPartition = useHiveDB + "alter table " + tableName + " add partition(projectId='" + projectId + "',batchId='" + batchId + "')";
                System.out.println("创建的分区为：" + addPartition);
                hiveTemplate.query(addPartition);
            }

            System.out.println("********************************开始加载数据到分区***********************************");
            String loadData2Hive = useHiveDB + "load data inpath '" + filePath + "' overwrite into table " + tableName + " partition(projectId='" + projectId + "',batchId='" + batchId + "')";
            hiveTemplate.query(loadData2Hive);
            res = true;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
        return res;
    }


    public static void main(String[] args) {

        //初始化配置文件
        String dataBase = "etl";
        SparkConf conf = new SparkConf();
        conf.setMaster("local").setAppName("sparkHive");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        HiveUtils hiveUtils = new HiveUtils(dataBase , sparkContext);

/*        // 1, 测试将 hdfs 上的文件导入到 hive 数据表中。
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", "testProject");
        jsonObject.put("batchId", "testBatch");
        String data = jsonObject.toJSONString();
        String tableName = "hivetest";
        String filePath = "/data/etl2/origindata/103/881a4456455544568985a13a467acb8a";
        boolean res = hiveUtils.loadData2Hive(data, tableName, filePath);
        System.out.println(res);*/

        //2，测试利用 spark 读取 hive 数据表。
//        String hiveTableName = "travelscenicreview";
//        String projectId = "hengqin";
//        String batchId = "67115b30d5fd47dc9c89095eb6c9177f";
//        String sqlString = "select * from  " + hiveTableName + "  where  projectId=  '" + projectId + "'  and batchId= '" + batchId + "'";
//        JavaRDD<String> rddData = hiveUtils.getRddData(dataBase, sqlString);
//        List<String> collect = rddData.collect();
//        for (String s : collect) {
//            System.out.println(s);
//        }

        String hiveTableName = "travelscenicreview";
        String projectId = "hengqin";
        String batchId = "testBatch";
        String sqlString = "select * from  " + hiveTableName + "  where  projectId=  '" + projectId + "'  and batchId= '" + batchId + "'";
        JavaRDD<String> rddData = hiveUtils.getRddData(dataBase, sqlString);
        List<String> collect = rddData.collect();
        System.out.println("****** 查出的结果 " + collect.size() );
        for (String s : collect) {
            System.out.println("******" + s);
        }

    }
}
