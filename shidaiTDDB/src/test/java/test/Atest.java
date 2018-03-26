package test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <pre>
 * Created with IntelliJ IDEA.
 * User: zhengzhi
 * Date: 2018/1/31
 * Time: 11:23
 * To change this template use File | Settings | File Templates.
 * </pre>
 *
 * @author Goldstein
 */
public class Atest {

    public static String TimeStamp2Date(String timestampString,String format){

        if(!format.isEmpty() && format != null){

        }


        return "";
    }


    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        long timestamp = Long.parseLong("1517294696")*1000;
        String d = sdf.format(timestamp);

        Date date = sdf.parse(d);

        System.out.println(d);
        System.out.println(date);
    }
}
