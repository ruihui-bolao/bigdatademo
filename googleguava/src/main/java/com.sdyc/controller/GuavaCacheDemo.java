package com.sdyc.controller;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/16 10:32
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
@Controller
@RequestMapping("/location")
public class GuavaCacheDemo {

    protected static Log LOG = LogFactory.getLog(GuavaCacheDemo.class);
    private Set<String> parmSet = new LinkedHashSet<String>();

    // spring guava cache
    //缓存接口这里是LoadingCache，LoadingCache在缓存项不存在时可以自动加载缓存
    //CacheBuilder的构造函数是私有的，只能通过其静态方法newBuilder()来获得CacheBuilder的实例
    private Cache<String, Object> parmsCache = CacheBuilder.newBuilder()
            //设置并发级别为8，并发级别是指可以同时写缓存的线程数
            .concurrencyLevel(20)
            //设置写缓存后8秒钟过期
            .expireAfterWrite(10, TimeUnit.SECONDS)
            //设置缓存容器的初始容量为10
            .initialCapacity(10)
            //设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
            .maximumSize(100)
            //设置要统计缓存的命中率
            .recordStats()
            //设置缓存的移除通知
            .removalListener(new RemovalListener<Object, Object>() {
                @Override
                public void onRemoval(RemovalNotification<Object, Object> notification) {
                    LOG.info(notification.getKey() + " was removed, cause is " + notification.getCause());
                    System.out.println(notification.getKey() + " was removed, cause is " + notification.getCause());
                }
            })
            //build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
            .build();

    @ResponseBody
    @RequestMapping("testGuavaCache.do")
    public String GuavaCache(String parms) throws ExecutionException, InterruptedException {
        ConcurrentMap<String, Object> concurrentMap = parmsCache.asMap();
        System.out.println("当前: " + concurrentMap.size());
        for (String s : concurrentMap.keySet()) {
            System.out.println("key = " + s);
        }
        parmsCache.put(parms, "jjjjjjjjj");
        return concurrentMap.toString();
    }

}

