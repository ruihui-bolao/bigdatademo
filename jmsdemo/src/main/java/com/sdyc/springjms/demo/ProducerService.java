package com.sdyc.springjms.demo;

import javax.jms.Destination;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/23 11:00
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public interface ProducerService {
    void sendMessage(Destination destination, final String message);
}
