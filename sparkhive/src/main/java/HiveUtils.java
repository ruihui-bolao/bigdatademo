import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.hadoop.hive.HiveTemplate;

import java.util.HashSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/2/11 12:01
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   实现将 hdfs 上的文件加载到 hive 中。
 */
public class HiveUtils {

    /**
     *  hive use database shell 语句
     */
    public String useHiveDB = "";

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
     * @param dataBaseName   hive 中执行操作的数据库
     */
    public HiveUtils(String dataBaseName) {
        useHiveDB = "use " + dataBaseName + ";";
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

            // 如果不存在先创建分区
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

    /**
     *  从hive中查询数据
     */
    public void queryData(){

    }

    public static void main(String[] args) {

        String dataBase = "test";
        HiveUtils hiveUtils = new HiveUtils(dataBase);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", "testProject");
        jsonObject.put("batchId", "testBatch");
        String data = jsonObject.toJSONString();
        String tableName = "hivetest";
        String filePath = "/data/etl2/origindata/103/881a4456455544568985a13a467acb8a";
        boolean res = hiveUtils.loadData2Hive(data, tableName, filePath);
        System.out.println(res);
    }

}
