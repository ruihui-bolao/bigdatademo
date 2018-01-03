package javahbase;

import com.alibaba.fastjson.JSONObject;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/12/21 14:48
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   java + hbaseTemplete + 操作hbase
 */
public class JavaHbase {

    public static String findDetailById(HbaseTemplate hbaseTemplate, String url) {
        byte[] rowKey = url.getBytes();
        // 使用get方法根据rowkey查询详细信息
        Map<String, String> result = hbaseTemplate.get("testhui", Bytes.toString(rowKey), new RowMapper<Map<String, String>>() {
            @Override
            public Map<String, String> mapRow(Result result, int i) throws Exception {

                // 存储结果集
                Map<String, String> map = new HashMap<String, String>();
                // 查询info列族的信息
                NavigableMap<byte[], byte[]> familyMap = result.getFamilyMap("info".getBytes());

                if (familyMap != null && !familyMap.isEmpty()) {
                    // 遍历info集合
                    for (Map.Entry<byte[], byte[]> entry : familyMap.entrySet()) {
                        // 将数据存储在map集合中
                        map.put(Bytes.toString(entry.getKey()), Bytes.toString(entry.getValue()));
                    }
                }
                return map;
            }
        });
        // 将map集合转换成json字符串返回
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(result);
        return jsonObject.toString();
    }
}
