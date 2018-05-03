import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/15 11:32
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class javaTest {

    @Test
    public void test() {
        String str = "hui,test";
        String str1 = "hui";
        str1 = str1 + "," + str1;
        System.out.println(str1);
    }

    @Test
    public void test2() {
        String str = "1514736000000";
        long l = Long.valueOf(str) / 1000;
        System.out.println(l);
    }

    @Test
    public void test3() throws Exception {
        String path = "C:\\Users\\sssd\\Desktop\\data\\data720.YQ2017HM00000083.json";
        String savePath = "C:\\Users\\sssd\\Desktop\\data\\zhongxin.json";
        List<String> lines = FileUtils.readLines(new File(path));
        FileWriter out = new FileWriter(savePath);
        for (String line : lines) {
            try {
                JSONObject jsonObject = (JSONObject) JSON.parse(line);
                JSONObject object = new JSONObject();
                object.put("title", jsonObject.getString("title"));
                object.put("content", jsonObject.getString("content"));
                out.write(object.toString() + "\n");
                out.flush();
            } catch (IOException e) {
                continue;
            }
        }
        out.close();
    }

    @Test
    public void test4() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("hui");
        list.add("jia");
        String remove = list.remove(0);
        System.out.println(remove);
        System.out.println(list.size());
        System.out.println(list.get(0));
    }

    @Test
    public void test5() {
        HashMap<String, String> map1 = new HashMap<String, String>();
        map1.put("name", "hui");
        HashMap<String, String> map3 = new HashMap<String, String>();
        map3.put("age", "1");
        HashMap<String, String> map2 = new HashMap<String, String>();
        map2.putAll(map1);
//        map2.putAll(map3);
        for (Map.Entry<String, String> entry : map2.entrySet()) {
            System.out.println(entry.getKey() + "-----" + entry.getValue());
        }
    }

    @Test
    public void test6() {
        HashMap<String, Boolean> map = new HashMap<String, Boolean>();
        map.put("hui", true);
        System.out.println(map.get("hui"));
        System.out.println(map.containsKey("test"));
    }

    /**
     * 主要是实现大文件的读写操作。
     * @throws Exception
     */
    @Test
    public void test9() throws Exception {
        String siteName = "青龙寺";
        String saveTempPath = "C:\\Users\\sssd\\Desktop\\qinglongsi2\\uniqueTemp.txt";
        File file = new File("C:\\Users\\sssd\\Desktop\\qinglongsi2\\yuanshuju.csv");
        LineIterator it = FileUtils.lineIterator(new File(saveTempPath), "UTF-8");
        int i = 0;
        while (it.hasNext()) {
            System.out.printf("开始处理第%d行数据...\n", ++i);
            String line = it.nextLine();
            String[] split = line.split(",");
            String tempstr = DigestUtils.md5Hex(split[0]) + "," + siteName + "," + DigestUtils.md5Hex(split[2]) + "," + split[3] + "," + split[4];
            FileUtils.writeStringToFile(file, tempstr + "\n", "gbk", true);
        }
    }

}
