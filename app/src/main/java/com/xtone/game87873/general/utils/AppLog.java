package com.xtone.game87873.general.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * 打印日志调用
 */
public class AppLog {
	public static boolean logable = true;

	public static void redLog(String aTag, String aLogInfo) {
		if (logable) {

			if (!TextUtils.isEmpty(aLogInfo)) {
				int index = 0;
				int maxLength = 4000;
				String sub;
				while (index < aLogInfo.length()) {
					// java的字符不允许指定超过总的长度end
					if (aLogInfo.length() <= index + maxLength) {
						sub = aLogInfo.substring(index);
					} else {
						sub = aLogInfo.substring(index, index + maxLength);
					}

					index += maxLength;
					Log.e(aTag, sub);
				}
			}
		}
	}

	public static void greenLog(String aTag, String aLogInfo) {
		if (logable) {
			Log.i(aTag, aLogInfo);
			aLogInfo = null;
			aTag = null;
		}
	}

	public static void yellowLog(String aTag, String aLogInfo) {
		if (logable) {
			Log.w(aTag, aLogInfo);
			aLogInfo = null;
			aTag = null;
		}
	}

	public static void blackLog(String aTag, String aLogInfo) {
		if (logable) {
			Log.d(aTag, aLogInfo);
			aLogInfo = null;
			aTag = null;
		}
	}

	public static void debug(String aTag, String aLogInfo) {
		Log.e(aTag, aLogInfo);
		aLogInfo = null;
		aTag = null;
	}

}
