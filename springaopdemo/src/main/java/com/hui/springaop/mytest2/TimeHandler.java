package com.hui.springaop.mytest2;

/**
 * Created by ninggd on 2017/11/29.
 */

/**
 * 切面类
 */
public class TimeHandler {
    public void printTime(){
        System.out.println("CurrentTime " + System.currentTimeMillis());
    }
}
