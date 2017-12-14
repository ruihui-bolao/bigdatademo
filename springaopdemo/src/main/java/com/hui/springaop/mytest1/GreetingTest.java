package com.hui.springaop.mytest1;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by ninggd on 2017/11/28.
 */
public class GreetingTest {

//    @Test
//    public void test() {
//        //创建代理工程
//        ProxyFactory proxyFactory = new ProxyFactory();
//        //射入代理对象
//        proxyFactory.setTarget(new GreetingImpl());
//        //添加前置增强，后置增强
//        // proxyFactory.addAdvice(new GreetingBeforeAndAfterAdvice());
//        proxyFactory.addAdvice(new GreetingAroundAdvice());
//        //从代理工厂中获取代理，调用代理方法。
//        Greeting greeting = (Greeting) proxyFactory.getProxy();
//        greeting.sayHello("word");
//    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        Greeting greeting = (Greeting) context.getBean("greetingProxy");
        greeting.sayHello("Word !!!");
    }
}
