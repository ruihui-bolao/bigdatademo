package com.hui.aopredis.demo.bean;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/14 10:55
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   将Redis中的key转换为Java bean
 */
public class RedisKeyBean {

    private String ndmpFlowLastTime;   //ndmpFlow的结束时间
    private String ndmpFlowStartTime;  //ndmpFlow的开始时间
    private String ndmpFlowError;      //ndmpFlow的错误异常

    public String getNdmpFlowLastTime() {
        return ndmpFlowLastTime;
    }

    public void setNdmpFlowLastTime(String ndmpFlowLastTime) {
        this.ndmpFlowLastTime = ndmpFlowLastTime;
    }

    public String getNdmpFlowStartTime() {
        return ndmpFlowStartTime;
    }

    public void setNdmpFlowStartTime(String ndmpFlowStartTime) {
        this.ndmpFlowStartTime = ndmpFlowStartTime;
    }

    public String getNdmpFlowError() {
        return ndmpFlowError;
    }

    public void setNdmpFlowError(String ndmpFlowError) {
        this.ndmpFlowError = ndmpFlowError;
    }
}
