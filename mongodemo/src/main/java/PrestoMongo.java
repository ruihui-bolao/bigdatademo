
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.sql.*;
import java.util.Iterator;

/**
 * Created by sssd on 2017/8/30.
 *
 * 通过 presto 连接 mongo
 */
public class PrestoMongo {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        // presto 连接mongodb
        Class.forName("com.facebook.presto.jdbc.PrestoDriver");
        // 192.168.1.238:9098：IP地址以及端口， mongodb：连接对象，data720：库名，"hui"用户名，不需验证，但是必须存在。null: 密码为空
        Connection connection = DriverManager.getConnection("jdbc:presto://192.168.1.238:9098/mongodb/data720", "hui", null);
        Statement statement = connection.createStatement();
        // 查询命令（单条查询）
        String query = "show tables";
        String query1 = "select * from test_ax limit 10";
        // 按时间段查询
//        DateTime nowTime = new DateTime();
//        DateTime todayTime = nowTime.withTimeAtStartOfDay();
        String query2 = "select * from test_ax where dwCreatedAt BETWEEN timestamp '2017-08-30 01:00 UTC'AND  TIMESTAMP '2017-08-31 01:00 UTC'";
        System.out.println(query2);
        // 获取返回结果的列数
        long startTime = System.currentTimeMillis();
        ResultSet resultSet = statement.executeQuery(query2);
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        //将结果保存到json数组中。
        JSONArray jsonArray = new JSONArray();
        while( resultSet.next()){
            JSONObject object = new JSONObject();
            for (int i=1; i<=columnCount; i++ ){
                String columnName = metaData.getColumnLabel(i);
                String value = resultSet.getString(columnName);
                object.put(columnName, value);
            }
            jsonArray.add(object);
        }
        long endTime = System.currentTimeMillis();
        //显示json数组中的内容。
        Iterator<Object> iterator = jsonArray.iterator();
        while (iterator.hasNext()){
            JSONObject lineValue = (JSONObject) iterator.next();
            System.out.println(lineValue);
        }
        System.out.println("查询所消耗的时间为" + (endTime-startTime)/1000f + "秒");
        resultSet.close();
        connection.close();
    }
}
