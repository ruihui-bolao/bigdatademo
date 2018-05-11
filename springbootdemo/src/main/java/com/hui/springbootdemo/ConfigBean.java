package com.hui.springbootdemo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/5/3 11:53
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:    用来将配置文件中的属性转换为Java bean
 */
@Component
@ConfigurationProperties(prefix = "com.hui")
public class ConfigBean {

    private String helloStr;
    private String name;

    public String getHelloStr() {
        return helloStr;
    }

    public void setHelloStr(String helloStr) {
        this.helloStr = helloStr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
