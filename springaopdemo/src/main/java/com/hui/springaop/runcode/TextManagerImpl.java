package com.hui.springaop.runcode;

import com.google.common.io.Files;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/13 16:56
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  测试 Spring Aop
 */
public class TextManagerImpl implements TextManager{

    @Override
    public String showText(String path) throws Exception {
        path = "E:\\Work\\bigdatademo\\sparkrdd\\src\\main\\resources\\spark.txt";
        List<String> lines = readText(path);
        for (String line : lines) {
            System.out.println(line);
        }
        return lines.toString();
    }

    @Override
    public List<String> readText(String path) throws Exception {
        List<String> lines = Files.readLines(new File(path), Charset.forName("UTF-8"));
        return lines;
    }
}
