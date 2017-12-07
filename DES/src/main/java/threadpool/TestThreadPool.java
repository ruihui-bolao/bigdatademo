package threadpool;

import multhread.Runnable1;
import multhread.Runnable2;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/12/7 10:20
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class TestThreadPool {

    public static void main(String[] args) {

        for (int i = 0;i<100; i++){
            Runnable1 runnable1 = new Runnable1();
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(100);
            scheduledExecutorService.scheduleAtFixedRate(runnable1,0,3, TimeUnit.SECONDS);
        }
    }

}
