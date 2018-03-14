package sdyc.bailuyuan.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 功能：日期工具类
 * 作者： create by yangxun
 * 时间： 2015/8/20.
 */
public class DateUtils {
    public final static String FORMAT_MM = "mm";
    public final static String FORMAT_TIME = "HH:mm:ss";
    public final static String FORMAT_8 = "yyyyMMdd";
    public final static String FORMAT_10 = "yyyy-MM-dd";
    public final static String FORMAT_19_yMdHms = "yyyyMMddHHmmss";
    public final static String FORMAT_19 = "yyyy-MM-dd HH:mm:ss";
    public final static String FORMAT_19_0 = "yyyy-MM-dd 00:00:01";
    public final static String FORMAT_19_H = "yyyy-MM-dd HH:00:00";
    public final static String FORMAT_19_M_00 = "yyyy-MM-dd HH:mm:00";
    public final static String FORMAT_19_M_30 = "yyyy-MM-dd HH:mm:30";
    public final static String FORMAT_19_M_59 = "yyyy-MM-dd HH:59:59";
    public final static String FORMAT_13_H = "yyyy-MM-dd HH";

    private static final String SHORT_DATE_NO_SPLIT_FORMAT_PATTERN = "yyyyMMdd";

    private static final String SHORT_DATE_FORMAT_PATTERN = "yyyy-MM-dd";

    private static final String BASE_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final String BASE_FULL_DATE_FORMAT_PATTERN = "yyyyMMddHHmmss";

    private static final String ES_BEGIN_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'00:00:00";

    private static final String ES_END_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'23:59:59";

    private static final String ES_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * 字符串类型的时间按照指定格式转换为Date类型
     *
     * @param time
     * @param format
     * @return
     */
    public static Date parseStrToDate(String time, String format) {
        Date date = null;
        SimpleDateFormat ymd = new SimpleDateFormat(format);
        try {
            date = ymd.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 将 string 时间类型转换为 sql  date
     *
     * @param time
     * @return
     */
    public static java.sql.Date parseStrToSqlDate(String time) {
        java.sql.Date date = null;
        try {
            date = java.sql.Date.valueOf(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 把Date类型日期按照指定格式转换成字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatDateToStr(Date date, String format) {
        String dateTime = null;
        SimpleDateFormat ymd = new SimpleDateFormat(format);
        try {
            dateTime = ymd.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTime;
    }

    /**
     * 把Date类型日期按照指定格式转换成字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatTimestampToStr(Timestamp date, String format) {
        String dateTime = null;
        SimpleDateFormat ymd = new SimpleDateFormat(format);
        try {
            dateTime = ymd.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTime;
    }

    /**
     * 按照指定格式获取当前时间
     *
     * @param format
     * @return
     */
    public static String getCurrentTime(String format) {
        String dateStr = null;
        SimpleDateFormat ymd = new SimpleDateFormat(format);
        dateStr = ymd.format(new Date());
        return dateStr;
    }

    /**
     * 获取当前前一小时的时间
     *
     * @param date
     * @return
     */
    public static Date getBeforeHour(Date date, int hour) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        //使用roll方法进行向前回滚
        //cl.roll(Calendar.DATE, -1);
        //使用set方法直接进行设置
        int day = cl.get(Calendar.HOUR);
        cl.set(Calendar.HOUR, day + hour);
        return cl.getTime();
    }

    /**
     * 获取当前前一小时的时间
     *
     * @param date
     * @return
     */
    public static String getBeforeMinute(String date, String format, int hour) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(parseStrToDate(date, format));
        //使用roll方法进行向前回滚
        //cl.roll(Calendar.DATE, -1);
        //使用set方法直接进行设置
        int day = cl.get(Calendar.MINUTE);
        cl.set(Calendar.MINUTE, day + hour);
        return formatDateToStr(cl.getTime(), format);
    }

    /**
     * 日期加减天数
     *
     * @param smdate 原始日期
     * @param day    加减天数
     * @return
     */
    public static Date dateArithmetic(Date smdate, int day) {
        GregorianCalendar gc = new GregorianCalendar();
        try {
            gc.setTime(smdate);
            gc.add(5, day);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gc.getTime();
    }

    /**
     * 日期加减天数
     *
     * @param smdate 原始日期
     * @param day    加减天数
     * @return
     */
    public static Timestamp dateArithmeticTimestamp(Timestamp smdate, int day) {
        GregorianCalendar gc = new GregorianCalendar();
        try {
            gc.setTime(smdate);
            gc.add(5, day);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Timestamp(gc.getTimeInMillis());
    }

    /**
     * 求两个日期相差天数
     *
     * @param smdate 开始日期
     * @param bdate  结束日期
     * @return
     */
    public static int daysBetween(String smdate, String bdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        Long between_days = null;
        try {
            cal.setTime(sdf.parse(smdate));
            long time1 = cal.getTimeInMillis();
            cal.setTime(sdf.parse(bdate));
            long time2 = cal.getTimeInMillis();
            between_days = (time2 - time1) / (1000 * 3600 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return between_days.intValue();
    }

    /**
     * 求两个日期相差天数
     *
     * @param smdate 开始日期
     * @param bdate  结束日期
     * @return
     */
    public static int daysBetween(Date smdate, Date bdate) {
        Long between_days = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            smdate = sdf.parse(sdf.format(smdate));
            bdate = sdf.parse(sdf.format(bdate));
            Calendar cal = Calendar.getInstance();
            cal.setTime(smdate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(bdate);
            long time2 = cal.getTimeInMillis();
            between_days = (time2 - time1) / (1000 * 3600 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return between_days.intValue();
    }

    /**
     *  时间戳转换为固定的时间字符串
     * @param seconds
     * @param format
     * @return
     */
    public static String timeStamp2Date(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = BASE_DATE_FORMAT_PATTERN;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds)));
    }

    /**
     * 将时间戳转换为固定格式的时间字符串
     */
    public static String stampToDateStr(String s) {
        String res;

        long lt = new Long(s) * 1000;
        Date date = new Date(lt);
        res = DATE_FORMAT.get().format(date);

        return res;
    }

    public static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(BASE_DATE_FORMAT_PATTERN);
        }
    };

    public static void main(String[] a) {
        System.out.println(DateUtils.formatDateToStr(DateUtils.getBeforeHour(new Date(), -1), DateUtils.FORMAT_19_M_59));
        System.out.println(DateUtils.formatDateToStr(DateUtils.getBeforeHour(new Date(), -1), DateUtils.FORMAT_19_H));
        System.out.println(DateUtils.formatDateToStr(new Date(System.currentTimeMillis() - 30 * 1000), DateUtils.FORMAT_19));

        String mm = DateUtils.getCurrentTime(DateUtils.FORMAT_MM);
        int m = (Integer.parseInt(mm) / 3) * 3;
        String endTime = null;
        if (m > 10) {
            endTime = DateUtils.getCurrentTime(DateUtils.FORMAT_13_H) + ":" + m + ":00";
        } else {
            endTime = DateUtils.getCurrentTime(DateUtils.FORMAT_13_H) + ":0" + m + ":00";
        }
        System.out.println(endTime);
        System.out.println(getBeforeMinute(endTime, DateUtils.FORMAT_19_M_00, -3));
    }
}
