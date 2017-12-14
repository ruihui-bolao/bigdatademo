package com.hui.springaop.runcode;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/13 16:56
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  测试 Spring Aop
 */

@Aspect
public class AspectJAdvice {

    /**
     * 指定 spring aop 切点，与函数 aspectJMethod 对应
     * 定义Pointcut，Pointcut名称为aspectjMethod,必须无参，无返回值
     * 只是一个标识，并不进行调用
     */
    @Pointcut("execution(* com.hui.springaop.runcode.TextManager.showText(..))")
    private void aspectJMethod() {
    }

    /**
     * 执行切点前做的一些工作
     * @param joinPoint
     */
    @Before("aspectJMethod()")
    public void doBefore(JoinPoint joinPoint) {
        System.out.println("----dobefore()开始----");
        System.out.println("执行业务逻辑前做一些工作");
        System.out.println("通过jointPoint获得所需内容");
        System.out.println("----dobefore()结束----");
    }

    /**
     * @around 增强处理近似等于Before 和 AfterReturning 的总和。@around 可以改变执行目标方法的参数值，也可以执行目标方法之后的返回值。
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("aspectJMethod()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable{

        System.out.println("----doAround()开始----");
        System.out.println("此处可做一些类似before的工作");
        //核心逻辑
        Object retval=pjp.proceed();
        System.out.println("此处可做一些类似after的工作");
        System.out.println("----doAround()结束----");
        return retval;
    }

    /**
     * 制定切点后做的一些工作
     * @param joinPoint
     */
    @After(value = "aspectJMethod()")
    public void doAfter(JoinPoint joinPoint) {
        System.out.println("----doAfter()开始----");
        System.out.println("执行核心逻辑之后，所做工作");
        System.out.println("通过jointPoint获得所需内容");
        System.out.println("----doAfter()结束----");
    }

    /**
     * 对返回值进行处理。
     * @param joinPoint
     * @param retval
     */
    @AfterReturning(value = "aspectJMethod()", returning = "retval")
    public void doReturn(JoinPoint joinPoint, String retval) {
        System.out.println("AfterReturning()开始");
        System.out.println("Return value= " + retval);
        System.out.println("此处可对返回结果做一些处理");
        System.out.println("----AfterReturning()结束----");

    }

    /**
     * 对抛出的异常进行处理
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(value = "aspectJMethod()", throwing = "e")
    public void doThrowing(JoinPoint joinPoint, Exception e) {
        System.out.println("-----doThrowing()开始-----");
        System.out.println(" 错误信息：" + e.getMessage());
        System.out.println(" 此处意在执行核心业务逻辑出错时，捕获异常，并可做一些日志记录操作等等");
        System.out.println(" 可通过joinPoint来获取所需要的内容");
        System.out.println("-----End of doThrowing()------");
    }
}