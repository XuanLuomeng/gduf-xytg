package cn.gduf.xytg.common.utils;

import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 日期操作工具类
 * @date 2025/10/29 23:24
 */
public class DateUtil {
    /**
     * 日期格式化模式：年-月-日
     */
    private static final String dateFormat = "yyyy-MM-dd";

    /**
     * 时间格式化模式：时:分:秒
     */
    private static final String timeFormat = "HH:mm:ss";

    /**
     * 将日期格式化为"yyyy-MM-dd"格式的字符串
     *
     * @param date 待格式化的日期对象
     * @return 格式化后的日期字符串
     */
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(date);

    }

    /**
     * 将日期格式化为"HH:mm:ss"格式的字符串
     *
     * @param date 待格式化的日期对象
     * @return 格式化后的时间字符串
     */
    public static String formatTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        return sdf.format(date);

    }

    /**
     * 将时间字符串解析为Date对象
     *
     * @param date 待解析的时间字符串，格式应为"HH:mm:ss"
     * @return 解析后的Date对象，如果解析失败则返回null
     */
    public static Date parseTime(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对两个日期进行截断比较
     *
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @param field 截断字段（如Calendar.YEAR、Calendar.MONTH等）
     * @return 比较结果：-1表示date1小于date2，0表示相等，1表示date1大于date2
     */
    public static int truncatedCompareTo(final Date date1, final Date date2, final int field) {
        return DateUtils.truncatedCompareTo(date1, date2, field);
    }

    /**
     * 比较两个日期的大小关系（精确到秒）
     *
     * @param beginDate 开始日期
     * @param endDate   结束日期
     * @return 如果endDate >= beginDate返回true，否则返回false
     */
    public static boolean dateCompare(Date beginDate, Date endDate) {
        // endDate > beginDate
        if (DateUtil.truncatedCompareTo(beginDate, endDate, Calendar.SECOND) == 1) {
            return false;
        }
        return true;
    }

    /**
     * 比较两个时间的大小关系（忽略年月日，仅比较时分秒）
     *
     * @param beginDate 开始时间
     * @param endDate   结束时间
     * @return 如果endDate >= beginDate返回true，否则返回false
     */
    public static boolean timeCompare(Date beginDate, Date endDate) {
        // 清除年月日信息，只保留时分秒进行比较
        Calendar instance1 = Calendar.getInstance();
        instance1.setTime(beginDate); //设置时间为当前时间
        instance1.set(Calendar.YEAR, 0);
        instance1.set(Calendar.MONTH, 0);
        instance1.set(Calendar.DAY_OF_MONTH, 0);

        Calendar instance2 = Calendar.getInstance();
        instance2.setTime(endDate); //设置时间为当前时间
        instance2.set(Calendar.YEAR, 0);
        instance2.set(Calendar.MONTH, 0);
        instance2.set(Calendar.DAY_OF_MONTH, 0);
        // endDate > beginDate
        if (DateUtil.truncatedCompareTo(instance1.getTime(), instance2.getTime(), Calendar.SECOND) == 1) {
            return false;
        }
        return true;
    }

    /**
     * 获取当前过期时间（以秒为单位）
     *
     * @return 过期时间，单位为秒
     */
    public static Long getCurrentExpireTimes() {
        //过期截止时间
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date()); //设置时间为当前时间
        instance.set(Calendar.HOUR_OF_DAY, 23);
        instance.set(Calendar.MINUTE, 59);
        instance.set(Calendar.SECOND, 59);
        Date endTime = instance.getTime();
        //当前时间与截止时间间隔，单位：秒
        long interval = (endTime.getTime() - new Date().getTime()) / 1000;
        return 100 * 60 * 60 * 24 * 365L;
    }

}
