package com.hui.tire;

/**
 * Created with IntelliJ IDEA.
 * User: sssd
 * Date: 2018/3/7 15:29
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:    编码转换工具类
 */
public class CodeUtils {


    /**
     *  unicode 转中文
     * @param dataStr   unicode 字符串
     * @return
     */
    public static String decodeUnicode( String dataStr) {

        String regex = "(.{4})";
        dataStr = dataStr.replaceAll(regex,"\\\\u$1");
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }

    /**
     * 中文转Unicode
     *
     * @param gbString
     * @return
     */
    public static String gbEncoding(final String gbString) {   //gbString = "测试"
        char[] utfBytes = gbString.toCharArray();   //utfBytes = [测, 试]
        String unicodeBytes = "";
        for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
            String hexB = Integer.toHexString(utfBytes[byteIndex]);   //转换为16进制整型字符串
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + hexB;
        }
        return unicodeBytes;
    }

    public static void main(String[] args) {
/*        String str = "中国";
        String s = CodeUtils.gbEncoding(str);
        System.out.println(s);*/
        String str2 = "4e2d56fd";
        String s1 = CodeUtils.decodeUnicode(str2);
        System.out.println(s1);
    }

}
