package com.hui.springaop.mytest1;

import org.springframework.stereotype.Component;

/**
 * Created by ninggd on 2017/11/28.
 */
@Component
public class GreetingImpl implements Greeting {
    @Override
    public void sayHello(String name) {
        System.out.println("Hello:"+name);
    }
}
