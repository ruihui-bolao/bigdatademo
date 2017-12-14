package com.hui.springaop.mytest1;

import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * Created by ninggd on 2017/11/28.
 */

/**
 * 实现接口MethodBeforeAdvice该拦截器会在调用方法前执行
 * 实现接口AfterReturningAdvice该拦截器会在调用方法后执行
 * 实现接口MethodInterceptor该拦截器会在调用方法前后都执行，实现环绕结果。
 */
public class GreetingBeforeAndAfterAdvice implements MethodBeforeAdvice, AfterReturningAdvice {

    @Override
    public void before(Method method, Object[] objects, Object o) throws Throwable {
        System.out.println("调用BeforeAdvice成功");
    }

    @Override
    public void afterReturning(Object o, Method method, Object[] objects, Object o1) throws Throwable {
        System.out.println("调用AfterAdvice成功");
    }
}
