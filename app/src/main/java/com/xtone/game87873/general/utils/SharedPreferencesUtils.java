package com.xtone.game87873.general.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.xtone.game87873.MyApplication;

/**
 * SharedPreferences工具类
 */
public class SharedPreferencesUtils {

	private static final String SP_NAME = "app_storage";

	public static SharedPreferences getSP() {
		return MyApplication.getInstance().getSharedPreferences(SP_NAME,
				Context.MODE_PRIVATE);
	}

	public static void saveBoolean(String key, boolean value) {
		SharedPreferences sp = getSP();
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static boolean getBoolean(String key, boolean defValue) {
		SharedPreferences sp = getSP();
		return sp.getBoolean(key, defValue);
	}

	public static void saveString(String key, String value) {
		SharedPreferences sp = getSP();
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getString(String key, String defValue) {
		SharedPreferences sp = getSP();
		return sp.getString(key, defValue);
	}

	public static void saveLong(String key, long value) {
		SharedPreferences sp = getSP();
		Editor editor = sp.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static long getLong(String key, long defValue) {
		SharedPreferences sp = getSP();
		return sp.getLong(key, defValue);
	}

	public static void saveInt(String key, int value) {
		SharedPreferences sp = getSP();
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static int getInt(String key, int defValue) {
		SharedPreferences sp = getSP();
		return sp.getInt(key, defValue);
	}
}
