package com.hui.redis.aop; /**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/14 9:49
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */

/**
 * redis key 的javabean
 */
public class RedisKeyBean {

    private String redisTopic;   //redis topic
    private String orgId;       // redis 机构

    private String lastTime;  //redis的技术时间
    private String startTime;  //redis的开始时间

    public String getRedisTopic() {
        return redisTopic;
    }

    public void setRedisTopic(String redisTopic) {
        this.redisTopic = redisTopic;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
