package com.dxy.library.util.common;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 时间工具类
 * @author duanxinyuan
 * 2017/9/6 17:55
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public static final String yyyy_MM_dd_HHmmssSSS_VALUE = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final DateTimeFormatter yyyy_MM_dd_HHmmssSSS = DateTimeFormatter.ofPattern(yyyy_MM_dd_HHmmssSSS_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String yyyy_MM_dd_HHmmss_VALUE = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter yyyy_MM_dd_HHmmss = DateTimeFormatter.ofPattern(yyyy_MM_dd_HHmmss_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String yyyy_MM_dd_HHmm_VALUE = "yyyy-MM-dd HH:mm";
    public static final DateTimeFormatter yyyy_MM_dd_HHmm = DateTimeFormatter.ofPattern(yyyy_MM_dd_HHmm_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String yyyy_MM_dd_HH_VALUE = "yyyy-MM-dd HH";
    public static final DateTimeFormatter yyyy_MM_dd_HH = DateTimeFormatter.ofPattern(yyyy_MM_dd_HH_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String yyyy_MM_dd_VALUE = "yyyy-MM-dd";
    public static final DateTimeFormatter yyyy_MM_dd = DateTimeFormatter.ofPattern(yyyy_MM_dd_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String yyyyMMdd_VALUE = "yyyyMMdd";
    public static final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern(yyyyMMdd_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String yyyy_MM_VALUE = "yyyy-MM";
    public static final DateTimeFormatter yyyy_MM = DateTimeFormatter.ofPattern(yyyy_MM_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String yyyyMM_VALUE = "yyyyMM";
    public static final DateTimeFormatter yyyyMM = DateTimeFormatter.ofPattern(yyyyMM_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String MM_dd_HHmmss_VALUE = "MM-dd HH:mm:ss";
    public static final DateTimeFormatter MM_dd_HHmmss = DateTimeFormatter.ofPattern(MM_dd_HHmmss_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String MM_dd_HHmm_VALUE = "MM-dd HH:mm";
    public static final DateTimeFormatter MM_dd_HHmm = DateTimeFormatter.ofPattern(MM_dd_HHmm_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String MM_dd_VALUE = "MM-dd";
    public static final DateTimeFormatter MM_dd = DateTimeFormatter.ofPattern(MM_dd_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String HHmmss_VALUE = "HH:mm:ss";
    public static final DateTimeFormatter HHmmss = DateTimeFormatter.ofPattern(HHmmss_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static final String HHmm_VALUE = "HH:mm";
    public static final DateTimeFormatter HHmm = DateTimeFormatter.ofPattern(HHmm_VALUE).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

    public static String format(LocalDateTime localDateTime, String pattern) {
        if (null == localDateTime) {
            throw new IllegalArgumentException("datetime is null");
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(Date date, String pattern) {
        if (null == date) {
            throw new IllegalArgumentException("date is null");
        }
        return DateFormatUtils.format(date, pattern);
    }

    public static String format(Date date, DateTimeFormatter dateTimeFormatter) {
        if (null == date) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).format(dateTimeFormatter);
    }

    public static String format(Long timestamp, String pattern) {
        if (null == timestamp) {
            throw new IllegalArgumentException("timestamp is null");
        }
        return format(timestamp, DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(Long timestamp, DateTimeFormatter dateTimeFormatter) {
        if (null == timestamp) {
            throw new IllegalArgumentException("timestamp is null");
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).format(dateTimeFormatter);
    }

    public static Date parse(String str, String pattern) {
        if (StringUtils.isEmpty(str)) {
            throw new IllegalArgumentException("date is null");
        }
        try {
            return parseDate(str, Locale.getDefault(), pattern);
        } catch (ParseException e) {
            throw new IllegalArgumentException();
        }
    }

    public static Date from(Long timestamp) {
        if (null == timestamp) {
            throw new IllegalArgumentException("timestamp is null");
        }
        return Date.from(Instant.ofEpochMilli(timestamp));
    }

    public static LocalDateTime ofInstant(Long timestamp) {
        if (null == timestamp) {
            throw new IllegalArgumentException("timestamp is null");
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    public static long toEpochMilli(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 计算两个时间相差的小时数目(精确到小数位)
     * @param beginDateStr 开始时间
     * @param endDateStr 结束时间
     * @return 小时数
     */
    public static double getDiffHour(String beginDateStr, String endDateStr, String pattern) {
        Date beginDate = parse(beginDateStr, pattern);
        Date endDate = parse(endDateStr, pattern);
        return getDiffHour(beginDate, endDate);
    }

    /**
     * 计算两个时间相差的小时数目(精确到小数位)
     * @param beginDate 开始时间
     * @param endDate 结束时间
     * @return 小时数
     */
    public static double getDiffHour(Date beginDate, Date endDate) {
        if (null == beginDate || null == endDate) {
            throw new IllegalArgumentException("date is null");
        }
        long nh = 1000 * 60 * 60;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - beginDate.getTime();
        // 计算差多少小时
        return (double) diff / (double) nh;
    }

    /**
     * 计算两个时间相差的天数(向下取整)
     * @param beginDateStr 开始时间
     * @param endDateStr 结束时间
     * @return 天数
     */
    public static int getDiffDay(String beginDateStr, String endDateStr, String pattern) {
        Date beginDate = parse(beginDateStr, pattern);
        Date endDate = parse(endDateStr, pattern);
        return getDiffDay(beginDate, endDate);
    }

    /**
     * 计算两个时间相差的天数目(向下取整)
     * @param beginDate 开始时间
     * @param endDate 结束时间
     * @return 天数
     */
    public static int getDiffDay(Date beginDate, Date endDate) {
        long nh = 1000 * 60 * 60 * 24;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - beginDate.getTime();
        // 计算差多少小时
        return (int) (diff / nh);
    }

    /**
     * 获取时间差
     * @param beginDate 开始时间
     * @param endDate 结束时间
     */
    public static Duration getDiff(Date beginDate, Date endDate) {
        GregorianCalendar beginCalendar = new GregorianCalendar();
        beginCalendar.setTime(beginDate);
        if (null == endDate) {
            throw new IllegalArgumentException("enddate is null");
        }
        GregorianCalendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);
        return Duration.between(beginCalendar.toZonedDateTime(), endCalendar.toZonedDateTime());
    }

    /**
     * 给时间做加法
     */
    public static Date plus(String dateStr, String pattern, long amountToAdd, ChronoUnit chronoUnit) {
        Date date = parse(dateStr, pattern);
        return plus(date, amountToAdd, chronoUnit);
    }

    /**
     * 给时间做加法
     */
    public static Date plus(Date date, long amountToAdd, ChronoUnit chronoUnit) {
        if (null == date) {
            throw new IllegalArgumentException("date is null");
        }
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).plus(amountToAdd, chronoUnit);
        Instant instant = zonedDateTime.toInstant();
        return Date.from(instant);
    }

    /**
     * 比较两个时间是否为同一天
     */
    public static boolean isSameDay(String dateStr1, String dateStr2) {
        return isSameDay(parse(dateStr1, yyyyMMdd_VALUE), parse(dateStr2, yyyyMMdd_VALUE));
    }

    /**
     * 判断是否是当月最后一天
     * @param date 日期
     */
    public static boolean isLastDayOfMonth(Date date) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        int dayOfMonth = zonedDateTime.getDayOfMonth();
        int lastDayOfMonth = zonedDateTime.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
        return dayOfMonth == lastDayOfMonth;
    }

}
