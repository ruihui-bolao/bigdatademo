package test;

/**
 * <pre>
 * Created with IntelliJ IDEA.
 * User: zhengzhi
 * Date: 2018/1/31
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 * </pre>
 *
 * @author Goldstein
 */
public class TestCase {


    public static void main(String[] args) {

        for (int i = 0; i < 100; i++) {
            Integer x = i;
            String s = x.toHexString(x).toUpperCase();

            int fixLen = s.length();
            if(fixLen < 8){
                for (int j = 0; j < (8 - fixLen); j++) {
                    s = "0" + s;
                }
                System.out.println(s);

            }
        }




    }
}
