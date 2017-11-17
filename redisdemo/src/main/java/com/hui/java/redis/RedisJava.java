package com.hui.java.redis;

import redis.clients.jedis.Jedis;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/15 17:43
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   java 连接 Redis服务
 */
public class RedisJava {

    public static void main(String[] args) {
        // redid 的链接地址
        Jedis jedis = new Jedis("192.168.1.235", 9999);
        System.out.println("连接成功");
        //查看服务是否运行
        System.out.println("服务正在运行: " + jedis.ping()); //
        //设置 redis 字符串数据
        jedis.set("runoobkey", "www.runoob.com");
        // 获取存储的数据并输出
        System.out.println("redis 存储的字符串为: " + jedis.get("runoobkey"));
    }

}
