package com.hui.aopredis.demo.aop;

import com.hui.aopredis.demo.bean.RedisKeyBean;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/14 10:54
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   流式数据切面监控，将监控的信息存放在redis中，
 */
public class BaseDataFlowHandler {

    /**
     * RedisTemplate
     */
    private RedisTemplate redisTemplate;

    /**
     * Redis key java bean
     */
    private RedisKeyBean redisKeyBean;

    /**
     * 获取当前时间
     *
     * @return 时间的string类型
     */
    public String getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String currentTime = df.format(new Date());
        return currentTime;
    }

    /**
     * 替换key中时间，
     *
     * @param key
     * @return
     */
    public String getKey(String key) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String today = df.format(new Date());
        // 将key中需要替换位置的数字进行替换。这样可以在key中添加时间等灵活信息。
        return key.replaceAll("%d", today);
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
