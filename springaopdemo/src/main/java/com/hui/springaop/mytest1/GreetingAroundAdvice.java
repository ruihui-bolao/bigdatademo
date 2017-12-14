package com.hui.springaop.mytest1;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;


/**
 * Created by ninggd on 2017/11/28.
 */

/**
 * 实现接口MethodInterceptor该拦截器会在调用方法前后都执行，实现环绕结果。
 */
@Component
public class GreetingAroundAdvice implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        before();
        Object proceed = methodInvocation.proceed();
        after();
        return proceed;
    }
    private void before(){
        System.out.println("Before");
    }
    private void after(){
        System.out.println("After");
    }
}
