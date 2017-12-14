package com.hui.springaop.mytest;

import com.hui.springaop.mytest1.GreetingImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by ninggd on 2017/11/28.
 */
public class LoveTest {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        GreetingImpl greeting = (GreetingImpl)context.getBean("greetingProxy");
        greeting.sayHello("Word!");
        Love love = (Love) greeting;
        love.display("Word!");
    }
}
