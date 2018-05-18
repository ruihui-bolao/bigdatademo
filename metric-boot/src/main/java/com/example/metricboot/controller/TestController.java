package com.example.metricboot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/5/18 16:38
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
@RequestMapping("/demo")
@RestController
public class TestController {
    @ResponseBody
    @RequestMapping("/test")
    public String test(){
        return "hello world";
    }
}
