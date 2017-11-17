package com.hrg.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 时间辅助类
 * 
 * 注意：请正常调用，不要放非常多不正确的时间格式
 * 
 */
public final class TimeTool {

	/**
	 * 各种时间格式字符串
	 */
	public final static String										yyyyMMddHHmmssSSS	= "yyyyMMddHHmmssSSS";

	public final static String										yyyyMMddHHmmss		= "yyyyMMddHHmmss";

	public final static String										yyyyMMddHHmm		= "yyyyMMddHHmm";

	public final static String										yyyyMMddHH			= "yyyyMMddHH";
	public final static String										yyyyMMddHHm			= "yyyyMMddHHm";

	public final static String										yyyyMMdd			= "yyyyMMdd";

	public final static String										_yyyyMMddHHmmssSSS	= "yyyy-MM-dd HH:mm:ss:SSS";

	public final static String										_yyyyMMddHHmmss		= "yyyy-MM-dd HH:mm:ss";

	public final static String										_yyyyMMddHHmm		= "yyyy-MM-dd HH:mm";

	public final static String										_yyyyMMddHH			= "yyyy-MM-dd HH";

	public final static String										_yyyyMMdd			= "yyyy-MM-dd";

	public final static String										yyyyMMddHHmmssSSS_	= "yyyy/MM/dd HH:mm:ss:SSS";

	public final static String										yyyyMMddHHmmss_		= "yyyy/MM/dd HH:mm:ss";

	public final static String										yyyyMMddHHmm_		= "yyyy/MM/dd HH:mm";

	public final static String										yyyyMMddHH_			= "yyyy/MM/dd HH";

	public final static String										yyyyMMdd_			= "yyyy/MM/dd";

	public final static String										yyMMddHHmmssSSS		= "yyMMddHHmmssSSS";

	public final static String										yyMMddHHmmss		= "yyMMddHHmmss";

	public final static String										yyMMddHHmm			= "yyMMddHHmmS";

	public final static String										yyMMddHH			= "yyMMddHHS";

	public final static String										yyMMdd				= "yyMMdd";

	public final static String										_yyMMddHHmmssSSS	= "yy-MM-dd HH:mm:ss:SSS";

	public final static String										_yyMMddHHmmss		= "yy-MM-dd HH:mm:ss";

	public final static String										_yyMMddHHmm			= "yy-MM-dd HH:mm";

	public final static String										_yyMMddHH			= "yy-MM-dd HH";

	public final static String										_yyMMdd				= "yy-MM-dd";

	/**
	 * 存放各种<时间格式字符串, ThreadLocal<SimpleDateFormat>(时间格式化辅助类的线程本地变量)>
	 */
	private final static Map<String, ThreadLocal<SimpleDateFormat>>	TIME_FORMAT_MAP		= new ConcurrentHashMap<String, ThreadLocal<SimpleDateFormat>>();

	/**
	 * 将时间字符串转化为时间类Date
	 * 
	 * @param formatStr
	 *            格式字符串
	 * @param strDate
	 *            需解析的时间字符串
	 * @return
	 * @throws ParseException
	 */
	public final static Date parse(final String formatStr, String strDate) {
		SimpleDateFormat sdf = getTlFormat(formatStr).get();
		try {
			return sdf.parse(strDate);
		} catch (ParseException e) {
			return new Date();
		}
	}

	/**
	 * 将时间字符串转化为时间类Date,格式默认用"yyyy-MM-dd HH:mm:ss"
	 * 
	 * @param formatStr
	 *            格式字符串
	 * @param strDate
	 *            需解析的时间字符串
	 * @return
	 * @throws ParseException
	 */
	public final static Date parse(String strDate) throws ParseException {
		SimpleDateFormat sdf = getTlFormat(_yyyyMMddHHmmss).get();
		return sdf.parse(strDate);
	}

	/**
	 * 将时间类Date转化为时间字符串
	 * 
	 * @param formatStr
	 *            格式字符串
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public final static String format(String formatStr, Date date) {

		SimpleDateFormat sdf = getTlFormat(formatStr).get();
		return sdf.format(date);
	}

	/**
	 * 按照formatStr格式 获取当前时间字符串
	 * 
	 * @param formatStr
	 *            格式字符串
	 * @return
	 * @throws ParseException
	 */
	public final static String format(String formatStr) {

		SimpleDateFormat sdf = getTlFormat(formatStr).get();
		return sdf.format(new Date());
	}

	/**
	 * 获取"yyyy-MM-dd HH:mm:ss" 格式的当前时间的字符串
	 * 
	 * @return
	 * @throws ParseException
	 */
	public final static String format() {
		SimpleDateFormat sdf = getTlFormat(_yyyyMMddHHmmss).get();
		return sdf.format(new Date());
	}

	/**
	 * 获取"yyyy-MM-dd HH:mm:ss" 格式的当前时间的字符串
	 * 
	 * @return
	 * @throws ParseException
	 */
	public final static String formatCurDate() {
		return format();
	}

	/**
	 * 获取"yyyyMMddHH:mm:ssSSS" 格式的当前时间的字符串
	 * 
	 * @return
	 * @throws ParseException
	 */
	public final static String formatwaichang() {
		SimpleDateFormat sdf = getTlFormat(yyMMddHHmmssSSS).get();
		return sdf.format(new Date());
	}

	/**
	 * 获取"yyyy-MM-dd HH:mm:ss" 格式的给定时间的字符串
	 * 
	 * @return
	 * @throws ParseException
	 */
	public final static String format(Date date) {
		SimpleDateFormat sdf = getTlFormat(_yyyyMMddHHmmss).get();
		return sdf.format(date);
	}

	/**
	 * 根据格式字符串取ThreadLocal<SimpleDateFormat>
	 * 
	 * @param formatStr
	 * @return
	 */
	private final static ThreadLocal<SimpleDateFormat> getTlFormat(final String formatStr) {
		ThreadLocal<SimpleDateFormat> tl = TIME_FORMAT_MAP.get(formatStr);
		if (tl == null) {
			tl = new ThreadLocal<SimpleDateFormat>() {
				@Override
				protected SimpleDateFormat initialValue() {
					return new SimpleDateFormat(formatStr);
				}
			};
			TIME_FORMAT_MAP.put(formatStr, tl);
		}
		return tl;
	}

	/**
	 * 根据各时间字段生成时间毫秒值
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 * @param millisecond
	 * @return
	 */
	public final static long getTime(int year, int month, int day, int hour, int minute, int second, int millisecond) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		c.set(Calendar.MILLISECOND, millisecond);
		return c.getTimeInMillis();
	}

	/**
	 * 获取date时间的d段值
	 * 
	 * @param c
	 * @param mills
	 * @param t
	 * @return
	 */
	public final static int getT(Calendar c, long mills, int t) {
		if (c == null) {
			c = Calendar.getInstance();
		}
		if (mills > 0) {
			c.setTimeInMillis(mills);
		}
		int ret = c.get(t);
		if (t == Calendar.MONTH) {
			ret++;
		}
		return ret;
	}

	/**
	 * 适配成jdbc DATE
	 * 
	 * @param date
	 * @return
	 */
	public final static java.sql.Date sqlDate(Date date) {
		return new java.sql.Date(date.getTime());
	}

	/**
	 * 适配成jdbc Timestamp
	 * 
	 * @param date
	 * @return
	 */
	public final static Timestamp timestampDate(Date date) {
		return new Timestamp(date.getTime());
	}

	/**
	 * 获取时间的年
	 * 
	 * @param date
	 * @return
	 */
	public final static int getYear(Date date) {
		return getT(null, date == null ? 0 : date.getTime(), Calendar.YEAR);
	}

	/**
	 * 获取时间的年
	 * 
	 * @param date
	 * @return
	 */
	public final static int getYear() {
		return getT(null, 0, Calendar.YEAR);
	}

	/**
	 * 获取时间的年
	 * 
	 * @param date
	 * @return
	 */
	public final static int getYear(long millis) {
		return getT(null, millis, Calendar.YEAR);
	}

	/**
	 * 获取时间的月
	 * 
	 * @param date
	 * @return
	 */
	public final static int getMonth(Date date) {
		return getT(null, date == null ? 0 : date.getTime(), Calendar.MONTH);
	}

	/**
	 * 获取时间的月
	 * 
	 * @param date
	 * @return
	 */
	public final static int getMonth() {
		return getT(null, 0, Calendar.MONTH);
	}

	/**
	 * 获取时间的月
	 * 
	 * @param date
	 * @return
	 */
	public final static int getMonth(long millis) {
		return getT(null, millis, Calendar.MONTH);
	}

	/**
	 * 获取时间的日
	 * 
	 * @param date
	 * @return
	 */
	public final static int getDay(Date date) {
		return getT(null, date == null ? 0 : date.getTime(), Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取时间的日
	 * 
	 * @param date
	 * @return
	 */
	public final static int getDay() {
		return getT(null, 0, Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取时间的日
	 * 
	 * @param date
	 * @return
	 */
	public final static int getDay(long millis) {
		return getT(null, millis, Calendar.DAY_OF_MONTH);
	}

	/**
	 * 根据各时间字段生成时间毫秒值
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public final static long getTime(int year, int month, int day, int hour, int minute, int second) {
		return getTime(year, month, day, hour, minute, second, 0);
	}

	/**
	 * 根据各时间字段生成时间毫秒值
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @return
	 */
	public final static long getTime(int year, int month, int day, int hour, int minute) {
		return getTime(year, month, day, hour, minute, 0, 0);
	}

	/**
	 * 根据各时间字段生成时间毫秒值
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @return
	 */
	public final static long getTime(int year, int month, int day, int hour) {
		return getTime(year, month, day, hour, 0, 0, 0);
	}

	/**
	 * 根据各时间字段生成时间毫秒值
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public final static long getTime(int year, int month, int day) {
		return getTime(year, month, day, 0, 0, 0, 0);
	}

	public static void main(String[] args) {
		// long l = getTime(2014, 1, 1);
		// long l2 = getTime(2014, 12, 31);
		// System.out.println(format(_yyyyMMddHHmmssSSS, new Date(l)));
		// System.out.println(format(_yyyyMMddHHmmssSSS, new Date(l2)));
		// // String a = "皖AA0666";
		// // if(a.length() > 2){
		// // System.out.println(a.substring(0, 2));
		// // }
		// Timestamp ts = new Timestamp(System.currentTimeMillis());
		// String tsStr = "2011-05-09 11:49:45";
		// try {
		// ts = Timestamp.valueOf(tsStr);
		// System.out.println(ts);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		System.out.println(format("yyyy_MM", new Date()));

	}
}
