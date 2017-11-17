import com.google.common.io.Files;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/16 14:25
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class Test {


    public void readText(String path){
        try {
            List<String> lines = Files.readLines(new File(path), Charset.forName("UTF-8"));
            System.out.println(lines.size());
            if (lines.size() < 10 ){
                throw new RuntimeException("获取的文件的个数有问题！");
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public void readText2(String path) throws Exception {
        List<String> lines = Files.readLines(new File(path), Charset.forName("UTF-8"));
    }

    public static void main(String[] args) {

        Test test = new Test();
        String path = "C:\\Users\\sssd\\Desktop\\spark.txt";
        try {
            test.readText(path);
//            test.readText2(path);
        } catch (Exception e) {
            System.out.println("抓到的异常为：" + e.toString());
        }

    }

}
