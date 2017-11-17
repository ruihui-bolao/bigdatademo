package com.sdyc.springjms.demo;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.jms.*;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/23 11:05
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */

@Service
public class ConsumerServiceImpl implements ConsumerService {

    @Resource
    private JmsTemplate jTemplate;

    @Override
    public String receiveMessage(Destination destination, Destination replyDestination) {

        Message message = jTemplate.receive(destination);

        try {
            /**
             * 此处为了更好的容错性，可以使用instanceof来判断下消息类型
             */
            if (message instanceof TextMessage) {
                String receiveMessage = ((TextMessage) message).getText();
                System.out.println("收到生产者的消息:" + receiveMessage);
                /**
                 * 收到消息之后，将回复报文放到回复队列里面去
                 */
                jTemplate.send(replyDestination, new MessageCreator() {

                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage("消费者已经收到生产者的消息了，这是一条确认报文!");
                    }
                });
                return receiveMessage;
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return "";
    }
}
