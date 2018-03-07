import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/7 13:40
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:
 */
public class JavaTest {

    @Test
    public void test(){
        String str = "words";
        char[] chars = str.toCharArray();
        System.out.println(chars.length);
        for (char aChar : chars) {
            System.out.println(aChar);
        }
    }

    @Test
    public void test2(){
        List<String> strings = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f");
/*        for (String s : strings) {
            System.out.println(s);
        }*/
        String s = strings.get(3);
        System.out.println(s);
    }

    @Test
    public void test3(){
        String dataStr = "4e2d56fd";
        String regex = "(.{4})";
        dataStr = dataStr.replaceAll(regex,"\\\\u$1");
        System.out.println(dataStr.trim());
    }

}
