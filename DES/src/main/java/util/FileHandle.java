package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/12/6 15:22
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class FileHandle {

    public static void main(String[] args) throws Exception {
        String path = "DES\\src\\main\\resources\\destest\\jiami";
        String savePath = "DES\\src\\main\\resources\\destest\\jiemi";
        File file = new File(path);
        File[] files = file.listFiles();
        for (File file1 : files) {
            InputStream inputStream = new FileInputStream(file1);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            byte[] decrypt = DESCBC.decrypt(bytes);
            FileOutputStream outputStream = new FileOutputStream(savePath + "\\" + file1.getName());
            outputStream.write(decrypt);
            outputStream.flush();
            inputStream.close();
            outputStream.close();
        }
    }

}
