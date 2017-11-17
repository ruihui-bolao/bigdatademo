package berkeleyprocess;

import java.io.FileOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/19 16:23
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class BerkeleyProcess {
    public static void main(String[] args) throws Exception{
        FileOutputStream outputStream = new FileOutputStream("G:\\Test1.txt");
        outputStream.close();
        System.out.println("被调用成功！");
    }
}
