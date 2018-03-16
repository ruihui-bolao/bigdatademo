package bailuyuan.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/16 11:07
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   txt 文本转换为 excel
 */
public class Fileutils {

    public static void main(String[] args) throws Exception {
        String savaPath = "C:\\Users\\sssd\\Desktop\\bailuyuan\\一月份\\temp.csv";
        String inputPath = "C:\\Users\\sssd\\Desktop\\bailuyuan\\一月份\\temp.txt";
        List<String> lines = FileUtils.readLines(new File(inputPath));
        for (String line : lines) {
            String savaLine = line + "\n";
            FileUtils.writeStringToFile(new File(savaPath), savaLine, "gbk", true);
        }
    }

}
