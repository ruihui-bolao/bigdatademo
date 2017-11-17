package com.hui.springaop;

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

    public String showText(String path)throws Exception;
    public List<String> readText(String path) throws Exception ;

}
