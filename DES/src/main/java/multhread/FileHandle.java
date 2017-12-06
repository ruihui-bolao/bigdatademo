package multhread;

import util.DESCBC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/12/6 14:57
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:    多线程的文件处理
 */
public class FileHandle {

    public static void fileHandle(String path, String savaPath) {
        FileInputStream fileInputStream = null;
        FileOutputStream outputStream = null;
        try {
            File file = new File(path);
            fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);
            byte[] encrypt = DESCBC.encrypt(bytes);
            outputStream = new FileOutputStream(savaPath + "\\" + "jiami" + "\\" + file.getName());
            outputStream.write(encrypt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
