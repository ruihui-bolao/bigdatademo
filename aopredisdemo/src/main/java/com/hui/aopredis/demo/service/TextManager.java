package com.hui.aopredis.demo.service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/13 16:56
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description: 测试 Spring Aop
 */


public interface TextManager {

    /**
     * 将文本的内容输出到控制台上
     *
     * @param path
     * @return
     * @throws Exception
     */
    public String showText(String path) throws Exception;

    /**
     * 读取文本中的内容
     *
     * @param path
     * @return
     * @throws Exception
     */
    public List<String> readText(String path) throws Exception;

}
