package multhread;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/12/6 13:54
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class Runnable4 implements Runnable {

    public String path = "C:\\Users\\sssd\\Desktop\\destest\\t4.txt";
    public String savaPath = "C:\\Users\\sssd\\Desktop\\destest";

    @Override
    public void run() {
        FileHandle.fileHandle(path, savaPath);
    }

    public static void main(String[] args) {
        Runnable4 runnable1 = new Runnable4();
        runnable1.run();
    }

}
