package com.crh.dubbo.demo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/9 10:55
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class App {
    public static void main(String[] args) throws Exception{
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("dubboPrivider.xml");
        context.start();
        while(true){
            System.out.println("run....");
            Thread.sleep(3000);
        }
    }
}
