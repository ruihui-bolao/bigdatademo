package com.hui.redis.aop;

import org.springframework.data.redis.core.RedisTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/14 9:48
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:    数据流切面监控
 */
public class BaseDataFlowHandler {

    private RedisTemplate redisTemplate;
    private RedisKeyBean redisKeyBean;

    public String getCurrentTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String currentTime = df.format(new Date());
        return currentTime;
    }

    public String getKey(String key){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String today = df.format(new Date());
        return key.replaceAll("%d", today).replaceAll("%t",this.redisKeyBean.getRedisTopic()).replaceAll("%o", this.redisKeyBean.getOrgId());
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public RedisKeyBean getRedisKeyBean() {
        return redisKeyBean;
    }

    public void setRedisKeyBean(RedisKeyBean redisKeyBean) {
        this.redisKeyBean = redisKeyBean;
    }
}
