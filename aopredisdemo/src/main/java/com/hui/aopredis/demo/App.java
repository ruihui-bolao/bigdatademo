package com.hui.aopredis.demo;

import com.hui.aopredis.demo.service.TextManager;
import com.hui.aopredis.demo.service.TextManagerImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/14 15:40
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class App {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("AopContext.xml");
        TextManager textManager = (TextManager)context.getBean("textManagerImpl");
        textManager.showText("C:\\Users\\sssd\\Desktop\\spark.txt");
    }
}
