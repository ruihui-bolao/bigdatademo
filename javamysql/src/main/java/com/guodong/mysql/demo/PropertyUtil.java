package com.guodong.mysql.demo;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Created by ninggd on 2017/11/17.
 *
 *  java  properties  工具类
 */
public class PropertyUtil {
    public static Properties loadProperties(String path) {
        ClassLoader classLoader = getClassLoader();
        URL resource =  classLoader.getResource(path);
        if(resource == null) {
            try {
                resource = new File(new File("./conf"), path).toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        final Properties properties = new Properties();
        try {
            InputStream inputStream = resource.openStream();
            properties.load(inputStream);
            IOUtils.closeQuietly(inputStream);
            return properties;
        } catch (final Exception e) {
            throw new RuntimeException("unable to load kata.properties", e);
        }
    }



    public static void printListFile(File dir, int floor) {
        File[] files = dir.listFiles();
        if(files == null) {
            return;
        }
        for (File file : files) {
            if(file.isFile()) {
                System.out.println(getTree(floor) + file.getAbsolutePath());
            } else {
                printListFile(file, ++floor);
            }
        }
    }


    public static String getTree(int f) {
        StringBuilder str = new StringBuilder("|");
        for (int i = 0; i < f; i++) {
            str.append("--");
        }
        return str.toString();
    }


    public static String getPropertiesFilePath(final String path) {
        ClassLoader classLoader = getClassLoader();

        URL resource =  classLoader.getResource(path);
        if(resource == null) {
            try {
                resource = new File(new File("./conf"), path).toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        if (resource == null) {
            throw new RuntimeException(path + " not in classpath");
        }
        return resource.toString();
    }




    public static Properties loadProperties(final File file) {
        final Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            properties.load(inputStream);
            return properties;
        } catch (final Exception e) {
            throw new RuntimeException("unable to load kata.properties", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }


    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if(classLoader == null) {
            classLoader = PropertyUtil.class.getClassLoader();
        }
        return classLoader;
    }



    public static String getClasspath() {
        return getClassLoader().getResource("").getPath();
    }



    public static URL getClasspathResource(String path) {
        return getClassLoader().getResource(path);
    }


    public static void main(String[] args) {
        System.out.println();
    }

}
