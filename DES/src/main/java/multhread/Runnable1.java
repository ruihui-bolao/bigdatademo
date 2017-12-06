package multhread;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/12/6 13:54
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:     其中的一个线程示例
 */
public class Runnable1 implements Runnable {

    public String path = "DES\\src\\main\\resources\\destest\\init\\t1.txt";
    public String savaPath = "DES\\src\\main\\resources\\destest";

    @Override
    public void run() {
        FileHandle.fileHandle(path, savaPath);
    }

    public static void main(String[] args) {
        Runnable1 runnable1 = new Runnable1();
        runnable1.run();
    }

}
