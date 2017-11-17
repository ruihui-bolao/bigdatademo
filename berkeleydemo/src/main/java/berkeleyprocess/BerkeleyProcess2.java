package berkeleyprocess;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/10/20 9:36
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class BerkeleyProcess2 {
    public static void main(String[] args) {
        try {
            ProcessBuilder builder = new ProcessBuilder("java", "berkeleyprocess.BerkeleyProcess");
            Process start = builder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
