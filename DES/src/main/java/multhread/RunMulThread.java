package multhread;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/12/6 15:02
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   直接运行多线程
 */
public class RunMulThread {

    public static void main(String[] args) {
        Runnable1 runnable1 = new Runnable1();
        Runnable2 runnable2 = new Runnable2();
        Runnable3 runnable3 = new Runnable3();
        Runnable4 runnable4 = new Runnable4();
        Runnable5 runnable5 = new Runnable5();
        Runnable6 runnable6 = new Runnable6();
        Runnable7 runnable7 = new Runnable7();
        Runnable8 runnable8 = new Runnable8();
        Runnable9 runnable9 = new Runnable9();

        Thread thread1 = new Thread(runnable1);
        Thread thread2 = new Thread(runnable2);
        Thread thread3 = new Thread(runnable3);
        Thread thread4 = new Thread(runnable4);
        Thread thread5 = new Thread(runnable5);
        Thread thread6 = new Thread(runnable6);
        Thread thread7 = new Thread(runnable7);
        Thread thread8 = new Thread(runnable8);
        Thread thread9 = new Thread(runnable9);

        thread1.run();
        thread2.run();
        thread3.run();
        thread4.run();
        thread5.run();
        thread6.run();
        thread7.run();
        thread8.run();
        thread9.run();

    }
}
