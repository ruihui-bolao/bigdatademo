package com.sdyc.springjms.demo;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/23 11:01
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */

@Service
public class ProducerServiceImpl implements ProducerService {

    @Resource
    private JmsTemplate jTemplate;

    @Override
    public void sendMessage(Destination destination, final String message) {
        System.out.println("================生产者创建了一条消息==============");
        jTemplate.send(destination, new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage("hello acticeMQ:"+message);
            }
        });
    }
}
