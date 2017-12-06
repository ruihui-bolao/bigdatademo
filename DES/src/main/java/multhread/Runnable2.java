package multhread;

import util.DESCBC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/12/6 13:54
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class Runnable2 implements Runnable {

    public String path = "DES\\src\\main\\resources\\destest\\init\\t2.txt";
    public String savaPath = "DES\\src\\main\\resources\\destest";

    @Override
    public void run() {
        FileHandle.fileHandle(path, savaPath);
    }

    public static void main(String[] args) {
        Runnable2 runnable1 = new Runnable2();
        runnable1.run();
    }

}
