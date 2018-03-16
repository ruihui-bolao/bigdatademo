import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/15 11:32
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class javaTest {

    @Test
    public void test(){
        String str = "hui,test";
        String str1 = "hui";
        str1 = str1 + "," + str1;
        System.out.println(str1);
    }

    @Test
    public void test2(){
        String str = "1514736000000";
        long l = Long.valueOf(str) / 1000;
        System.out.println(l);
    }

}
