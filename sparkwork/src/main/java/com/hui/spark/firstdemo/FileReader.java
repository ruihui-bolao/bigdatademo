package com.hui.spark.firstdemo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 2018/11/14 15:06
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   主要是用来实现文件的读取
 */
public class FileReader {

    String inputPath = "C:\\Users\\sssd\\Desktop\\华侨数据\\wifidata1.csv";
    String prePath = "C:\\Users\\sssd\\Desktop\\华侨数据\\wifidata-";


    /**
     * 首先将时间划分
     *
     * @throws Exception
     */
    public void readerFile() throws Exception {
        List<String> lines = FileUtils.readLines(new File(inputPath), "GBK");
        String tempStr = ""; // 主要是用來按天分割数据
        for (String line : lines) {
            String[] split = line.split(",");
            String phoneMac = split[0].replaceAll("-", "");
            String timeStr = split[2];
            String wifiMac = split[5].substring(9, 21);
            String timeStr1 = changTimeStr(timeStr);
            String date = timeStr1.split(" ")[0];
            String res = phoneMac + "," + wifiMac + "," + timeStr1;
            if (StringUtils.isBlank(tempStr) || !StringUtils.equalsIgnoreCase(tempStr, date)) {
                tempStr = date.toString();
            }
            String outPath = prePath + tempStr + ".txt";
            FileUtils.writeStringToFile(new File(outPath), res + "\n", "UTF-8", true);
        }
    }

    /**
     * 时间戳转换为lang
     *
     * @param timeStr
     * @return
     */
    public String changTimeStr(String timeStr) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(timeStr) * 1000;
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;

    }


    public static void main(String[] args) throws Exception {
        FileReader fileReader = new FileReader();
        fileReader.readerFile();
    }
}
