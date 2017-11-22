import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.UDFRegistration;
import org.apache.spark.sql.types.DataTypes;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/22 9:33
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  测试 spark 的用户自定义函数
 */
public class App {
    public static void main(String[] args) {
        JavaSparkContext context = new JavaSparkContext("local", "TestUDF");
        SQLContext sqlContext = new SQLContext(context);
        UDFRegistration udfRegistration = sqlContext.udf();
        // 读取文件的 json 格式
        DataFrame jsonDataFrame = sqlContext.read().json("sparksqludf/src/main/resources/testjson.json");
        udfRegistration.register("ip2long", new IP2LongUDF(), DataTypes.LongType);
        // 从数据库中查询ip并展示
        jsonDataFrame.select("ip").show();
        // 创建临时表并展示表结构
        jsonDataFrame.registerTempTable("TestSparkUdf");
        jsonDataFrame.show();
        // 利用用户自定义函数进行查询
        DataFrame filter1 = jsonDataFrame.sqlContext().sql("select ip2long(ip) as long ,name" +
                " from TestSparkUdf ");
        filter1.show();
    }
}
