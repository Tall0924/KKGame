package com.xtone.game87873.general.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.xtone.game87873.section.entity.IndexAdEntity;

/**
 * 储存数据及获取数据的类
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-6-23 下午4:51:42
 */
public class AppStorage {

	private static final String VERSION_CODE = "version_code";
	private static final String NOTIFICATION_SETTING = "notification_setting";
	private static final String AUTO_DELETE_PACKAGE = "auto_delete_package";
	private static final String DOWNLOAD_ONLY_WIFI = "download_only_wifi";
	private static final String INDEX_AD_URL = "index_ad_url";
	private static final String INDEX_AD_START_TIME = "index_ad_start_time";
	private static final String INDEX_AD_END_TIME = "index_ad_end_time";
	private static final String INDEX_AD_TYPE = "index_ad_type";
	private static final String INDEX_AD_TARGET_ID = "index_ad_targe_id";
	private static final String INDEX_AD_ID = "index_ad_id";
	private static final String INDEX_AD_DIR = "index_ad_dir";
	private static final String INDEX_AD_TITLE = "index_ad_title";

	/**
	 * 获取上一次的版本号
	 * 
	 * @return
	 */
	public static int getLastVersionCode() {
		return SharedPreferencesUtils.getInt(VERSION_CODE, 0);
	}

	/**
	 * 保存版本号
	 * 
	 * @param versionCode
	 */
	public static void saveVersionCode(int versionCode) {
		SharedPreferencesUtils.saveInt(VERSION_CODE, versionCode);
	}
	/**
	 * 获取是否推送通知
	 * 
	 * @return
	 */
	public static boolean isReceiveNotification() {
		return SharedPreferencesUtils.getBoolean(NOTIFICATION_SETTING, true);
	}
	
	/**
	 * 设置是否推送通知
	 * 
	 */
	public static void setIsReceiveNotification(boolean b) {
		SharedPreferencesUtils.saveBoolean(NOTIFICATION_SETTING, b);
	}
	/**
	 * 获取是否自动删除安装包
	 * 
	 * @return
	 */
	public static boolean isAutoDeletePackage() {
		return SharedPreferencesUtils.getBoolean(AUTO_DELETE_PACKAGE, true);
	}
	
	/**
	 * 设置是否自动删除安装包
	 * 
	 */
	public static void setIsAutoDeletePackage(boolean b) {
		SharedPreferencesUtils.saveBoolean(AUTO_DELETE_PACKAGE, b);
	}
	/**
	 * 获取是否仅在wifi下下载
	 * 
	 * @return
	 */
	public static boolean isDownloadOnlyWifi() {
		return SharedPreferencesUtils.getBoolean(DOWNLOAD_ONLY_WIFI, true);
	}
	
	/**
	 * 设置是否仅在wifi下下载
	 * 
	 */
	public static void setIsDownloadOnlyWifi(boolean b) {
		SharedPreferencesUtils.saveBoolean(DOWNLOAD_ONLY_WIFI, b);
	}
	/**
	 * 保存加载页广告信息
	 * 
	 */
	public static void saveIndexAdInfo(long id,String url,long startTime,long endTime,String type,long targetId,String dir,String title) {
		SharedPreferences sp = SharedPreferencesUtils.getSP();
		Editor editor = sp.edit();
		editor.putLong(INDEX_AD_ID, id);
		editor.putString(INDEX_AD_URL, url);
		editor.putLong(INDEX_AD_START_TIME, startTime);
		editor.putLong(INDEX_AD_END_TIME, endTime);
		editor.putString(INDEX_AD_TYPE, type);
		editor.putLong(INDEX_AD_TARGET_ID, targetId);
		editor.putString(INDEX_AD_DIR, dir);
		editor.putString(INDEX_AD_TITLE, title);
		editor.commit();
	}
	
	public static IndexAdEntity getIndexAdInfo(){
		IndexAdEntity info= new IndexAdEntity();
		info.setAdId(SharedPreferencesUtils.getLong(INDEX_AD_ID, 0));
		info.setImgUrl(SharedPreferencesUtils.getString(INDEX_AD_URL, ""));
		info.setType(SharedPreferencesUtils.getString(INDEX_AD_TYPE, ""));
		info.setTargetId(SharedPreferencesUtils.getLong(INDEX_AD_TARGET_ID, 0));
		info.setStartTime(SharedPreferencesUtils.getLong(INDEX_AD_START_TIME, 0));
		info.setEndTime(SharedPreferencesUtils.getLong(INDEX_AD_END_TIME, 0));
		info.setDir(SharedPreferencesUtils.getString(INDEX_AD_DIR, ""));
		info.setTitle(SharedPreferencesUtils.getString(INDEX_AD_TITLE, ""));
		return info;
	}
}
