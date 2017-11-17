package com.crh.dubbo.demo.impl;

import com.crh.dubbo.demo.service.DubboService;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/9 10:45
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */

@Service("dubboService")
public class DubboServiceImpl implements DubboService{
    @Override
    public String demoService(String username) {
        System.out.println("welcome" + username);
        return "welcome"+username;
    }
}
