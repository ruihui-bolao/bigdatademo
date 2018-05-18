package com.hui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.metrics.instrument.MeterRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/5/18 11:08
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
@RequestMapping(value = "test")
@Controller

public class MyController {

    @Autowired
//    MeterRegistry meterRegistry;


    @RequestMapping(value = "hello")
    public String test(){
        System.out.println("hello word!");
        return "index";
    }



}
