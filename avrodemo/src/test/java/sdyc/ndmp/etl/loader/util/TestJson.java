package sdyc.ndmp.etl.loader.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/7/7 17:33
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class TestJson {


    public JSONObject getJsonByClientId(String json, String clientId) {
        JSONObject json1 = (JSONObject) JSON.parse(json);
        JSONArray data = json1.getJSONArray("data");
        for (Object datum : data) {
            JSONObject tempJson = (JSONObject) datum;
            String tempClientId = tempJson.getString("clientId");
            if (StringUtils.equalsIgnoreCase(tempClientId, clientId)) {
                return  tempJson;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        TestJson testJson = new TestJson();
        String str = "{\n" +
                "\t\"data\": [{\n" +
                "\t\t\"clientHostname\": \"DESKTOP-VDOFBJB:5796\",\n" +
                "\t\t\"clientId\": \"0a01cd58-3221-4254-bce3-18e29bec1b80\",\n" +
                "\t\t\"clientStatus\": \"RUNNING\",\n" +
                "\t\t\"gcType-PS MarkSweep-count\": \"0\",\n" +
                "\t\t\"gcType-PS MarkSweep-time\": \"0\",\n" +
                "\t\t\"gcType-PS Scavenge-count\": \"10\",\n" +
                "\t\t\"gcType-PS Scavenge-time\": \"110\",\n" +
                "\t\t\"heapUsage-committed\": \"122683392\",\n" +
                "\t\t\"heapUsage-init\": \"130023424\",\n" +
                "\t\t\"heapUsage-max\": \"1840250880\",\n" +
                "\t\t\"heapUsage-used\": \"17018120\",\n" +
                "\t\t\"lastConnecTime\": 1530949258743,\n" +
                "\t\t\"nonHeapMemoryUsage-committed\": \"23265280\",\n" +
                "\t\t\"nonHeapMemoryUsage-init\": \"2555904\",\n" +
                "\t\t\"nonHeapMemoryUsage-max\": \"22423120\",\n" +
                "\t\t\"nonHeapMemoryUsage-used\": \"-1\",\n" +
                "\t\t\"registTime\": 1530949258743,\n" +
                "\t\t\"thread-count\": \"19\",\n" +
                "\t\t\"thread-cpu-time\": \"703125000\",\n" +
                "\t\t\"thread-daemon-count\": \"8\",\n" +
                "\t\t\"thread-peak-count\": \"19\",\n" +
                "\t\t\"thread-user-time\": \"312500000\"\n" +
                "\t}, {\n" +
                "\t\t\"clientHostname\": \"DESKTOP-VDOFBJB:10596\",\n" +
                "\t\t\"clientId\": \"90c93869-a5bc-4085-884d-083cb38e2a7c\",\n" +
                "\t\t\"clientStatus\": \"RUNNING\",\n" +
                "\t\t\"gcType-PS MarkSweep-count\": \"0\",\n" +
                "\t\t\"gcType-PS MarkSweep-time\": \"0\",\n" +
                "\t\t\"gcType-PS Scavenge-count\": \"10\",\n" +
                "\t\t\"gcType-PS Scavenge-time\": \"63\",\n" +
                "\t\t\"heapUsage-committed\": \"122683392\",\n" +
                "\t\t\"heapUsage-init\": \"130023424\",\n" +
                "\t\t\"heapUsage-max\": \"1840250880\",\n" +
                "\t\t\"heapUsage-used\": \"17040328\",\n" +
                "\t\t\"lastConnecTime\": 1530949258743,\n" +
                "\t\t\"nonHeapMemoryUsage-committed\": \"23068672\",\n" +
                "\t\t\"nonHeapMemoryUsage-init\": \"2555904\",\n" +
                "\t\t\"nonHeapMemoryUsage-max\": \"22341640\",\n" +
                "\t\t\"nonHeapMemoryUsage-used\": \"-1\",\n" +
                "\t\t\"registTime\": 1530949258743,\n" +
                "\t\t\"thread-count\": \"19\",\n" +
                "\t\t\"thread-cpu-time\": \"531250000\",\n" +
                "\t\t\"thread-daemon-count\": \"8\",\n" +
                "\t\t\"thread-peak-count\": \"19\",\n" +
                "\t\t\"thread-user-time\": \"281250000\"\n" +
                "\t}, {\n" +
                "\t\t\"clientHostname\": \"DESKTOP-VDOFBJB:10568\",\n" +
                "\t\t\"clientId\": \"99d2a62b-a9e8-4a74-868b-6e810353b259\",\n" +
                "\t\t\"clientStatus\": \"RUNNING\",\n" +
                "\t\t\"gcType-PS MarkSweep-count\": \"0\",\n" +
                "\t\t\"gcType-PS MarkSweep-time\": \"0\",\n" +
                "\t\t\"gcType-PS Scavenge-count\": \"10\",\n" +
                "\t\t\"gcType-PS Scavenge-time\": \"84\",\n" +
                "\t\t\"heapUsage-committed\": \"122683392\",\n" +
                "\t\t\"heapUsage-init\": \"130023424\",\n" +
                "\t\t\"heapUsage-max\": \"1840250880\",\n" +
                "\t\t\"heapUsage-used\": \"17912760\",\n" +
                "\t\t\"lastConnecTime\": 1530949258743,\n" +
                "\t\t\"nonHeapMemoryUsage-committed\": \"22937600\",\n" +
                "\t\t\"nonHeapMemoryUsage-init\": \"2555904\",\n" +
                "\t\t\"nonHeapMemoryUsage-max\": \"22207424\",\n" +
                "\t\t\"nonHeapMemoryUsage-used\": \"-1\",\n" +
                "\t\t\"registTime\": 1530949258743,\n" +
                "\t\t\"thread-count\": \"19\",\n" +
                "\t\t\"thread-cpu-time\": \"546875000\",\n" +
                "\t\t\"thread-daemon-count\": \"8\",\n" +
                "\t\t\"thread-peak-count\": \"19\",\n" +
                "\t\t\"thread-user-time\": \"328125000\"\n" +
                "\t}, {\n" +
                "\t\t\"clientHostname\": \"DESKTOP-VDOFBJB:8592\",\n" +
                "\t\t\"clientId\": \"03c2fbf6-94db-4ae9-9c72-879b1c3288ac\",\n" +
                "\t\t\"clientStatus\": \"RUNNING\",\n" +
                "\t\t\"gcType-PS MarkSweep-count\": \"0\",\n" +
                "\t\t\"gcType-PS MarkSweep-time\": \"0\",\n" +
                "\t\t\"gcType-PS Scavenge-count\": \"10\",\n" +
                "\t\t\"gcType-PS Scavenge-time\": \"96\",\n" +
                "\t\t\"heapUsage-committed\": \"122683392\",\n" +
                "\t\t\"heapUsage-init\": \"130023424\",\n" +
                "\t\t\"heapUsage-max\": \"1840250880\",\n" +
                "\t\t\"heapUsage-used\": \"23449672\",\n" +
                "\t\t\"lastConnecTime\": 1530949258743,\n" +
                "\t\t\"nonHeapMemoryUsage-committed\": \"23068672\",\n" +
                "\t\t\"nonHeapMemoryUsage-init\": \"2555904\",\n" +
                "\t\t\"nonHeapMemoryUsage-max\": \"22416704\",\n" +
                "\t\t\"nonHeapMemoryUsage-used\": \"-1\",\n" +
                "\t\t\"registTime\": 1530949258743,\n" +
                "\t\t\"thread-count\": \"19\",\n" +
                "\t\t\"thread-cpu-time\": \"671875000\",\n" +
                "\t\t\"thread-daemon-count\": \"8\",\n" +
                "\t\t\"thread-peak-count\": \"19\",\n" +
                "\t\t\"thread-user-time\": \"375000000\"\n" +
                "\t}],\n" +
                "\t\"total\": 4\n" +
                "}";
        String clientId = "90c93869-a5bc-4085-884d-083cb38e2a7c";
        JSONObject jsonByClientId = testJson.getJsonByClientId(str, clientId);
        System.out.println(jsonByClientId.toJSONString());
    }

}
