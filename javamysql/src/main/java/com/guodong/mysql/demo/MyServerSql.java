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
public final class MyServerSql {

    /**
     * Log
     */
    private final static Logger LOG = LoggerFactory.getLogger(MyServerSql.class);



    protected final static Properties myServerSQL = new Properties();


    static {
        URL resource = PropertyUtil.getClasspathResource("jise-server-sql.xml");

        FileInputStream in = null;
        try {
            in = new FileInputStream(new File(resource.toURI()));
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
