package com.hui.springbootdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/5/3 11:34
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:     自定义 spring-boot controller
 */
@RestController
public class MyController {

//    // 指定一个用来注解
//    @Value("${com.hello}")
//    public String str;
//    @Value("${com.name}")
//    public String name;

    //     通过java bean 的方式进行注解
    @Autowired
    private ConfigBean configBean;


    @RequestMapping("/")
    public String hexo() {
        String helloStr = configBean.getHelloStr();
        String name = configBean.getName();
        return helloStr + " " + name;
    }
}
