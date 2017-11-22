package utils;

import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2017/11/22 9:25
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:    关于IP 的一些工具类
 */
public class IpUtils {

    /**
     *  对给出的String Ip 进行分割
     * @param ip
     * @return
     */
    private static String[] splitIps(String ip) {
        //我们先把ip分为四段,$ip1,$ip2,$ip3,$ip4、
        return StringUtils.split(ip, ".", -1);
    }

    /**
     *  将IP 转换成 long 类型
     * @param ip
     * @return
     */
    public static long ipv4ToLong(String ip) {
        String[] ipSplits = splitIps(ip);
        if (ipSplits.length != 4) {
            return 0L;
        }
        return (Long.valueOf(ipSplits[0]) << 24) | (Long.valueOf(ipSplits[1]) << 16) | (Long.valueOf(ipSplits[2]) << 8) | (Long.valueOf(ipSplits[3]));
    }

    /**
     * 将 long类型的IP 转换成 String 格式的ip
     * @param ip
     * @return
     */
    public static String long2Ipv4(long ip) {
        return ((ip >> 24) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + (ip & 0xFF);
    }

    public static void main(String[] args) {
        long value2 = IpUtils.ipv4ToLong("115.182.90.100");
        System.out.println(value2);
        System.err.println(long2Ipv4(value2));
    }
}
