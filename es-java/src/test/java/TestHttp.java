import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/7/23 9:50
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   用来测试抓取
 */
public class TestHttp {

    public void getVale(){
        try {
            String http = "https://weibo.com/jxfjdh?refer_flag=1001030201_&is_hot=1";
            Document doc = Jsoup.connect(http)
                    .data("query", "Java")
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .post();
            doc.select("img[class=W_face_radius]").get(0);
            Elements face = doc.getElementsByClass("face");
            Elements rows = doc.select("table[class=table]").get(0).select("td");
            System.out.println(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getUrl(){
        try {
            String str = "https://weibo.com/jxfjdh?refer_flag=1001030201_&is_hot=1";
            URL url = new URL(str);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            InputStreamReader input = new InputStreamReader(httpConn.getInputStream(), "utf-8");
            BufferedReader bufReader = new BufferedReader(input);
            String line = "";
            StringBuilder contentBuf = new StringBuilder();
            while ((line = bufReader.readLine()) != null) {
                contentBuf.append(line);
            }
            String buf = contentBuf.toString();
            System.out.println(buf);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        TestHttp testHttp = new TestHttp();
        testHttp.getUrl();
    }
}
