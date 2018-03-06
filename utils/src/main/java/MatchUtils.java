import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/6 11:54
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   关于正则匹配的一些常用工具类
 */
public class MatchUtils {

    /**
     * 正则匹配时间的格式, yyyy-MM-hh 年-月-日 或者 yyyy/MM/hh 年/月/日
     *
     * @param dataValue
     */
    public static void matchDate(String dataValue) {
        String dataFormat = "\\d{4}[-/]\\d{2}[-/]\\d{2}";
        Pattern compile = Pattern.compile(dataFormat);
        Matcher matcher = compile.matcher(dataValue);
        String res = null;
        if (matcher.find()) {
            res = matcher.group();
        }
        System.out.println(res);
    }

    /**
     * 用来匹配软件大小及单位的正则匹配,例如：该软件大小为 9M，匹配出9M 。
     *
     * @param text
     */
    public static void matchSize(String text) {
        String softwareSize = "([0-9]+(.[0-9]{1,3})?)+(b|B|kb|KB|MB|mb|GB|gb|G|g|M|m|K|k)";
        Pattern compile = Pattern.compile(softwareSize);
        Matcher matcher = compile.matcher(text);
        String res = null;
        if (matcher.find()) {
            res = matcher.group();
        }
        System.out.println(res);
    }

    /**
     * 用来匹配 url 的主域名
     *
     * @param url
     */
    public static void mathUrl(String url) {
//        String url = "//cps.youku.com/redirect.html?id=00000163&url=http%3A%2F%2Fv.youku.com%2Fv_show%2Fid_XODA3MjIzMjcy.html%3Ffrom%3Ds1.8-3-1.1";
        String domain = url.replaceAll("((http[s]{0,1}:)?//.+?)/.*", "$1").replaceAll("(http[s]{0,1}:)?//", "");
        System.out.println("匹配到的主域名为：" + domain);
    }


    public static void main(String[] args) {
        String text = "现在的时间为2018-03-06";
        MatchUtils.matchDate(text);
    }
}
