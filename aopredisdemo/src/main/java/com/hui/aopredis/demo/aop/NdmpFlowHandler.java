package com.hui.aopredis.demo.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.springframework.data.redis.core.ValueOperations;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/14 11:10
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   对aop中的切点进行处理
 */

public class NdmpFlowHandler extends BaseDataFlowHandler {

    protected static Log LOG = LogFactory.getLog(NdmpFlowHandler.class);

    /**
     * 切点之前处理
     *
     * @param joinPoint
     */
    public void doBefore(JoinPoint joinPoint) {
        LOG.info("开始执行切点之前的处理");
        System.out.println("开始执行切点之前的处理");
        ValueOperations valueOps = getRedisTemplate().opsForValue();
        valueOps.set(getKey(getRedisKeyBean().getNdmpFlowStartTime()), getCurrentTime().toString());
    }

    /**
     * 切点之后处理
     *
     * @param joinPoint
     */
    public void doAfter(JoinPoint joinPoint) {
        System.out.println("_______________________________");
        ValueOperations valueOps = getRedisTemplate().opsForValue();
        valueOps.set(getKey(getRedisKeyBean().getNdmpFlowLastTime()), getCurrentTime().toString());
    }

    /**
     * 抛异常处理
     *
     * @param joinPoint
     * @param e
     */
    public void doThrowing(JoinPoint joinPoint, Exception e) {
        ValueOperations valueOps = getRedisTemplate().opsForValue();
        valueOps.set(getKey(getRedisKeyBean().getNdmpFlowError()), e.getMessage());
    }

}
