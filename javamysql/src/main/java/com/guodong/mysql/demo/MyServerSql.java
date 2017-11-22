package com.guodong.mysql.demo;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Created by ninggd on 2017/11/17.
 */

/**
 * mysql 的 server 服务类
 */
public final class MyServerSql {

    /**
     * Log
     */
    private final static Logger LOG = LoggerFactory.getLogger(MyServerSql.class);

    /**
     * 初始化 Java properties 的配置文件
     */
    protected final static Properties myServerSQL = new Properties();

    /**
     * 静态代码块：初始化代码时，只执行一次
     */
    static {
        //加载配置文件
        URL resource = PropertyUtil.getClasspathResource("jise-server-sql.xml");
        // 获取输入流
        FileInputStream in = null;
        try {
            in = new FileInputStream(new File(resource.toURI()));
            // properties 读取输入流
            myServerSQL.loadFromXML(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static Properties getInstance() {
        return myServerSQL;
    }



    public static void main(String[] args) {
        MyServerSql conf = new MyServerSql();
        System.out.println(MyServerSql.getInstance().getProperty("get.task.param"));
    }


}
