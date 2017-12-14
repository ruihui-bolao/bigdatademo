package com.hui.springaop.mytest;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.stereotype.Component;

/**
 * Created by ninggd on 2017/11/28.
 */
@Component
public class LoveAdvice extends DelegatingIntroductionInterceptor implements Love {
    @Override
    public void display(String name) {
        System.out.println("you are my heart:"+name);
    }


    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        return super.invoke(mi);
    }
}
