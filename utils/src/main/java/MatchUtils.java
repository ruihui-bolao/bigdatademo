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
     *  用来匹配 url 的主域名
     *
     * @param url
     */
    public static void mathUrl(String url){
        String domain = url.replaceAll("((http[s]{0,1}:)?//.+?)/.*", "$1").replaceAll("(http[s]{0,1}:)?//", "");
        System.out.println("匹配到的主域名为：" + domain);
    }


    public static void main(String[] args) {
        String url = "//cps.youku.com/redirect.html?id=00000163&url=http%3A%2F%2Fv.youku.com%2Fv_show%2Fid_XODA3MjIzMjcy.html%3Ffrom%3Ds1.8-3-1.1";
        MatchUtils.mathUrl(url);
    }
}
