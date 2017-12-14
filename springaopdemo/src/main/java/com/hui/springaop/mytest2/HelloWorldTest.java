package com.hui.springaop.mytest2;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by ninggd on 2017/11/29.
 */
public class HelloWorldTest {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-aop.xml");
        HelloWorld hw1 = (HelloWorld)context.getBean("helloWorldImpl1");
        HelloWorld hw2 = (HelloWorld)context.getBean("helloWorldImpl2");
        hw1.printHelloWorld();

        hw2.printHelloWorld();

    }
}
