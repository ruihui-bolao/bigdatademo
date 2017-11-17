package com.hui.redis.aop;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/14 9:13
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class TestRedis extends BaseDataFlowHandler {

    public void testRedis(RedisTemplate redisTemplate, RedisKeyBean redisKeyBean) {
        ValueOperations valueOps = redisTemplate.opsForValue();
        setRedisKeyBean(redisKeyBean);
        valueOps.set(getKey(getRedisKeyBean().getOrgId()), "hui");
        valueOps.set(getKey(getRedisKeyBean().getRedisTopic()), "TestRedis");
        valueOps.set(getKey(getRedisKeyBean().getStartTime()), getCurrentTime().toString());
        valueOps.set(getKey(getRedisKeyBean().getLastTime()), getCurrentTime().toString());
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        RedisTemplate redisTemplate = (RedisTemplate) context.getBean("redisTemplate");
        RedisKeyBean redisKeyBean = (RedisKeyBean) context.getBean("redisKeyBean");
        TestRedis testRedis = new TestRedis();
        testRedis.testRedis(redisTemplate, redisKeyBean);
    }
}
