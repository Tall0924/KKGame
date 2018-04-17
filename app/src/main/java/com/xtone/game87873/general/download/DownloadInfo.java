package com.xtone.game87873.general.download;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.lidroid.xutils.http.HttpHandler;

/**
 * 下载相关信息的实体类
 * 
 * @author yangpb
 * @Date:2014-8-26下午5:03:05
 */
@DatabaseTable(tableName = "DownloadInfo")
public class DownloadInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String GROUP_ID = "groupId";
	public static final int STATE_HAS_INSTALLED = 1;
	public static final int STATE_HAS_DOWNLOADED = 2;
	public static final int STATE_DOWNLOADING = 3;
	public static final int STATE_PAUSE = 4;
	public static final int STATE_NO_DOWNLOAD = 5;

	// @DatabaseField(id = true)
	// private String id;
	@DatabaseField(generatedId = true)
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@DatabaseField
	private long appId;

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	@DatabaseField
	private String appName;
	@DatabaseField
	private String appDes;
	@DatabaseField
	private String appSize;
	@DatabaseField
	private String typeName;

	private long downloadTotal;
	private int gameIconSuperscript;// 角标 0：无 1：活动 2：首发 3：新服

	public int getGameIconSuperscript() {
		return gameIconSuperscript;
	}

	public void setGameIconSuperscript(int gameIconSuperscript) {
		this.gameIconSuperscript = gameIconSuperscript;
	}

	public long getDownloadTotal() {
		return downloadTotal;
	}

	public void setDownloadTotal(long downloadTotal) {
		this.downloadTotal = downloadTotal;
	}

	public String getAppSize() {
		return appSize;
	}

	public void setAppSize(String appSize) {
		this.appSize = appSize;
	}

	public String getAppDes() {
		return appDes;
	}

	public void setAppDes(String appDes) {
		this.appDes = appDes;
	}

	@DatabaseField
	private int versionCode;

	@DatabaseField
	private String versionName;

	@DatabaseField
	private String appIconUrl;

	@DatabaseField
	private String apkDownloadUrl;

	@DatabaseField
	private String apkSavePath;

	@DatabaseField
	private String apkPackageName;
	@DatabaseField
	private int apkMark;

	private String gameContent;

	private List<String> picUrls;

	public List<String> getPicUrls() {
		return picUrls;
	}

	public void setPicUrls(List<String> picUrls) {
		this.picUrls = picUrls;
	}

	public String getGameContent() {
		return gameContent;
	}

	public void setGameContent(String gameContent) {
		this.gameContent = gameContent;
	}

	public int getApkMark() {
		return apkMark;
	}

	public void setApkMark(int apkMark) {
		this.apkMark = apkMark;
	}

	private HttpHandler<File> downloadHandler;

	private Drawable appIcon;
	@DatabaseField
	private long contentLength;

	@DatabaseField
	private long currLength;

	@DatabaseField
	private String downloadTime;

	@DatabaseField
	private String appType;

	@DatabaseField
	private HttpHandler.State state;

	private int appState;

	// public String getId() {
	// return id;
	// }
	//
	// public void setId(String id) {
	// this.id = id;
	// }

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getAppIconUrl() {
		return appIconUrl;
	}

	public void setAppIconUrl(String appIconUrl) {
		this.appIconUrl = appIconUrl;
	}

	public String getApkDownloadUrl() {
		return apkDownloadUrl;
	}

	public void setApkDownloadUrl(String apkDownloadUrl) {
		this.apkDownloadUrl = apkDownloadUrl;
	}

	public String getApkSavePath() {
		return apkSavePath;
	}

	public void setApkSavePath(String apkSavePath) {
		this.apkSavePath = apkSavePath;
	}

	public String getApkPackageName() {
		return apkPackageName;
	}

	public void setApkPackageName(String apkPackageName) {
		this.apkPackageName = apkPackageName;
	}

	public HttpHandler<File> getDownloadHandler() {
		return downloadHandler;
	}

	public void setDownloadHandler(HttpHandler<File> downloadHandler) {
		this.downloadHandler = downloadHandler;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	public long getCurrLength() {
		return currLength;
	}

	public void setCurrLength(long currLength) {
		this.currLength = currLength;
	}

	public String getDownloadTime() {
		return downloadTime;
	}

	public void setDownloadTime(String downloadTime) {
		this.downloadTime = downloadTime;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public HttpHandler.State getState() {
		return state;
	}

	public void setState(HttpHandler.State state) {
		this.state = state;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setAppState(int appState) {
		this.appState = appState;
	}

	public int getAppState() {
		return appState;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o != null && o instanceof DownloadInfo) {
			DownloadInfo info = (DownloadInfo) o;
			if (!TextUtils.isEmpty(apkPackageName)
					&& apkPackageName.equals(info.apkPackageName)
					&& versionCode != 0 && versionCode == info.versionCode) {
				return true;
			}
		}
		return false;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

}
