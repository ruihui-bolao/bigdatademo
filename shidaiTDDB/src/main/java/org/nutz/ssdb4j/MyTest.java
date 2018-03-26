package org.nutz.ssdb4j;

import org.nutz.ssdb4j.spi.Response;
import org.nutz.ssdb4j.spi.SSDB;
import org.nutz.ssdb4j.util.DateUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 * Created with IntelliJ IDEA.
 * User: zhengzhi
 * Date: 2018/1/30
 * Time: 17:10
 * To change this template use File | Settings | File Templates.
 * </pre>
 *
 * @author Goldstein
 */
public class MyTest {


    private static SSDB ssdb = SSDBs.pool("192.168.1.235", 8888, 2000, null);

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String sTime = "2018-02-01 00:00:00";
    public static String eTime = "2018-03-04 23:59:59";



    /**
     * 比较时间在不在规定时间范围内
     *
     * @param startTime
     * @return
     */
    public static boolean compare(Long startTime) {
        try {
            long stamp = DateUtils.dateToStamp2(sTime);
            long etamp = DateUtils.dateToStamp2(eTime);
            if (startTime >= (stamp / 1000) && (etamp / 1000) >= startTime) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    //提取——RelationData-terminal-xxxxxxxxxx 数据
    public static void getTerminalData() throws IOException {

        FileWriter writer = new FileWriter("C:\\Users\\sssd\\Desktop\\bailuyuan\\bailuyuan1.csv");
        writer.write("sn," + "ssid," + "time," + "authMode," + "bssid," + "channel," + "count," + "logOut," + "mac," + "pad," + "rssi");
        writer.write("\n");

        String key = "";
        for (int i = 1; i < 1000000000; i++) {
            System.out.printf("正在导出%d条数据%n",i);
            Integer x = i;
            String s = x.toHexString(x).toUpperCase();

            int fixLen = s.length();
            if (fixLen < 8) {
                for (int j = 0; j < (8 - fixLen); j++) {
                    s = "0" + s;
                }
                key = s;
            }


            //取值
            Response time = ssdb.hgetall("__RelationData-terminal-" + key);

            if(time.mapString().size() == 0){
                System.out.println("breake key为：" + key);
                break;
            }


            Map<String, String> map = time.mapString();
            Set<String> keys = map.keySet();

            boolean compareRes = false;
            //根据sn判断数据是否是白鹿原数据，如果是则进行处理
            if (map.get("sn") != null && map.get("sn").equalsIgnoreCase("WF441706287A4F0B10")) {

                String[] strs = new String[11];
                StringBuffer buffer = new StringBuffer();
                for (String mapkey : keys) {
                    String mapVlaue = map.get(mapkey);
                    if (mapVlaue == null) {
                        mapVlaue = "";
                    }

                    if (mapkey.equals("ssid")) {
                        strs[1] = mapVlaue;
                    } else if (mapkey.equals("time")) {
                        long tempLong = Long.parseLong(mapVlaue);
                        compareRes = compare(tempLong);
/*                        long timestamp = tempLong * 1000;
                        String d = sdf.format(timestamp);*/
                        strs[2] = Long.toString(tempLong);
                    } else if (mapkey.equals("authMode")) {
                        strs[3] = mapVlaue;
                    } else if (mapkey.equals("bssid")) {
                        strs[4] = mapVlaue;
                    } else if (mapkey.equals("channel")) {
                        strs[5] = mapVlaue;
                    } else if (mapkey.equals("count")) {
                        strs[6] = mapVlaue;
                    } else if (mapkey.equals("logOut")) {
                        long l = Long.parseLong(mapVlaue);
/*                        long timestamp = l * 1000;
                        String d = sdf.format(timestamp);*/
                        strs[7] = Long.toString(l);
                    } else if (mapkey.equals("mac")) {
                        strs[8] = mapVlaue;
                    } else if (mapkey.equals("pad")) {
                        strs[9] = mapVlaue;
                    } else if (mapkey.equals("rssi")) {
                        strs[10] = mapVlaue;
                    } else {
                        strs[0] = mapVlaue;
                    }
                }

                // TODO: 2018/3/16  叫上时间限制

                if (compareRes){
                    for (int j = 0; j < 11; j++) {
                        buffer.append(strs[j]);
                        buffer.append(",");
                    }

                    System.out.println("写入一条数据：" + buffer.toString());
                    writer.write(buffer.toString());
                    writer.write("\n");
                }
            } else {
                continue;
            }
        }

        System.out.println("##########数据处理完毕#############");
        writer.flush();
        writer.close();

    }


    //提取——__RelationData-device-XXXX 数据
    public static void getDeviceData() throws Exception{

        FileWriter writer = new FileWriter("F:/deviceInfo.csv");
        writer.write("address," + "floor," + "ip," + "latitude," + "longitude," + "name," + "pad," + "saleType," + "serviceType," + "sn" );
        writer.write("\n");


        String key = "";
        for(int i = 1;i < 1000 ; i++){

            Integer x = i;
            String s = x.toHexString(x).toUpperCase();

            int fixLen = s.length();
            if (fixLen < 8) {
                for (int j = 0; j < (8 - fixLen); j++) {
                    s = "0" + s;
                }
                key = s;
            }


            //取值
            Response response = ssdb.hgetall("__RelationData-device-" + key);


            //如果找不到则跳出，因为这个号是自增的
            if(response.mapString().size() == 0){
                break;
            }


            Map<String, String> map = response.mapString();

            String[] strs = new String[10];
            StringBuffer buffer = new StringBuffer();

            Set<String> keys = map.keySet();
            for(String mapKey : keys){

                String mapValue = map.get(mapKey);
                if(mapValue == null){
                    mapValue = "";
                }

                if (mapKey.equals("address")) {
                    strs[0] = mapValue;
                } else if (mapKey.equals("floor")) {
                    strs[1] = mapValue;
                } else if (mapKey.equals("ip")) {
                    strs[2] = mapValue;
                } else if (mapKey.equals("latitude")) {
                    strs[3] = mapValue;
                } else if (mapKey.equals("longitude")) {
                    strs[4] = mapValue;
                } else if (mapKey.equals("name")) {
                    strs[5] = mapValue;
                } else if (mapKey.equals("pad")) {
                    strs[6] = mapValue;
                } else if (mapKey.equals("saleType")) {
                    strs[7] = mapValue;
                } else if (mapKey.equals("serviceType")) {
                    strs[8] = mapValue;
                } else {
                    strs[9] = mapValue;
                }

            }

            for (int j = 0; j < 10; j++) {
                buffer.append(strs[j]);
                buffer.append(",");
            }

            System.out.println("写入一条数据：" + buffer.toString());
            writer.write(buffer.toString());
            writer.write("\n");

        }

        System.out.println("##########Device数据处理完毕#############");
        writer.flush();
        writer.close();


    }


    public static void main(String[] args) throws Exception {

        getTerminalData();


//        getDeviceData();

    }
}
