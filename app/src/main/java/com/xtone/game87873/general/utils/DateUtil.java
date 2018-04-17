package com.xtone.game87873.general.utils;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间格式转换工具类
 * 
 * @author: chenyh
 * @date: 2014-7-30 下午1:46:45
 * 
 */
public class DateUtil {

	public static SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd  HH:mm:ss");
	public static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");

	public static SimpleDateFormat feedDateFormat = new SimpleDateFormat(
			"yyyy.MM.dd HH:mm");

	// Sting to date 转换 Date date = formatter.parse(str);

	// dateUtil 获取当前的时间
	public static String getNowDate() {
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}

	public static String formatDate(Long date) {
		Date tempDates = new Date(date);
		String str = dateFormat.format(tempDates);
		return str;
	}

	public static String formatFeedDate(Long date) {
		Date tempDates = new Date(date);
		String str = feedDateFormat.format(tempDates);
		return str;
	}

	// 将字符串转为时间戳 
	@SuppressLint("SimpleDateFormat") 
	public static long strToTime(String strTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d = sdf.parse(strTime);
			long l = d.getTime();
			return l;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	//时间戳转化为易读时间
	public static String getRestidueTime(long time)
	{
		int day = (int) (time/1000/60/60/24);
		int hour = (int) (time/1000/60/60%24);
		int minute = (int) (time/1000/60%60);
		StringBuffer strb = new StringBuffer();
		if (day > 0) {
			strb.append(day+"天");
		}
		if (strb.length() > 0 || hour > 0) {
			strb.append(hour+"小时");
		}
		if (minute > 0) {
			strb.append(minute+"分");
		}
		return strb.toString();
	}

}
