package com.hui.mulThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/12 11:35
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:  kafka 生产者的 多线程模式
 */
public class HandlerProducer implements Runnable{

    /**
     * kafka 的发送消息
     */
    private String message;

    /**
     * 构造器初始化
     * @param message
     */
    public HandlerProducer(String message){
        this.message = message;
    }

    /**
     *  实现 runable 接口
     */
    public void run() {
        KafkaProducerSingleton kafkaProducerSingleton = KafkaProducerSingleton.getInstance();
        kafkaProducerSingleton.init("accesslog",3);
        System.out.println("当前线程：" + Thread.currentThread().getName() + ",获取的kafka实例" + kafkaProducerSingleton);
        kafkaProducerSingleton.sendKafkaMessage("发送消息：" + message);
    }

    public static void main(String[] args) throws Exception {
        ExecutorService service  = Executors.newFixedThreadPool(3);
        for (int i = 1; i <= 10; i++) {
            Thread.sleep(1000);
            service.execute(new HandlerProducer(":" + i));
        }
    }

}
