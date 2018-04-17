package com.xtone.game87873.general.download;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.android.volley.VolleyError;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.AppUtils;
import com.xtone.game87873.general.utils.JsonUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;

/**
 * UpadteAndInstalledUtils.java
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-24 下午3:53:52
 */
public class UpadteAndInstalledUtils {

	private Context mContext;
	private MyCallBack mCallBack;

	public UpadteAndInstalledUtils(Context context, MyCallBack callBack) {
		mContext = context;
		mCallBack = callBack;
	}

	private Handler mHanler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				setInstalledData((List<DownloadInfo>) msg.obj);
				break;
			case 2:
				setUpdateData((List<DownloadInfo>) msg.obj);
				break;

			default:
				break;
			}

		}
	};

	public void getUpdateAndInstalled() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<DownloadInfo> appInfos = AppUtils
						.getInstalledAppPackageList(mContext);
				if (appInfos != null && appInfos.size() > 0) {
					Message msg = new Message();
					msg.obj = appInfos;
					msg.what = 1;
					mHanler.sendMessage(msg);
					Message msg2 = new Message();
					msg2.obj = appInfos;
					msg2.what = 2;
					mHanler.sendMessage(msg2);
				}
			}
		}).start();
	}

	public void getUpdateList() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<DownloadInfo> appInfos = AppUtils
						.getInstalledAppPackageList(mContext);
				if (appInfos != null && appInfos.size() > 0) {
					Message msg2 = new Message();
					msg2.obj = appInfos;
					msg2.what = 2;
					mHanler.sendMessage(msg2);
				}
			}
		}).start();
	}

	public void getInstalledList() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<DownloadInfo> appInfos = AppUtils
						.getInstalledAppPackageList(mContext);
				if (appInfos != null && appInfos.size() > 0) {
					Message msg = new Message();
					msg.obj = appInfos;
					msg.what = 1;
					mHanler.sendMessage(msg);
				}
			}
		}).start();
	}

	private void setInstalledData(List<DownloadInfo> appInfos) {

		try {
			JSONArray appArr = new JSONArray();
			for (int i = 0; i < appInfos.size(); i++) {
				JSONObject appJson = new JSONObject();
				DownloadInfo app = appInfos.get(i);
				appJson.put("apk_package_name", app.getApkPackageName());
				appJson.put("apk_version_code", app.getVersionCode());
				appArr.put(appJson);
			}
			if (mContext != null) {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("checklist", appArr.toString());
				VolleyUtils.requestString(mContext, ApiUrl.INSTALL_LIST,
						params, this, new VolleyCallback<String>() {

							@Override
							public void onResponse(String response) {
								try {
									AppLog.redLog("jjjjj___install___",response);
									JSONObject responseJson = new JSONObject(response);

									int status = responseJson.getInt("status");
									String msg = responseJson.getString("message");
									DownloadService.getDownloadManager(mContext).getInstalledAppList().clear();
									if (status == 200) {
										List<DownloadInfo> appInfos = new ArrayList<DownloadInfo>();
										JSONArray bannerArray = responseJson.getJSONArray("data");
										if (bannerArray != null&& bannerArray.length() > 0) {
											int len = bannerArray.length();
											for (int i = 0; i < len; i++) {
												JSONObject infoObj = bannerArray.getJSONObject(i);
												DownloadInfo info = new DownloadInfo();
												info.setAppId(JsonUtils.getJSONLong(infoObj,"id"));
												info.setApkPackageName(JsonUtils.getJSONString(infoObj,"apk_package_name"));
												info.setVersionCode(JsonUtils.getJSONInt(infoObj,"apk_version_code"));
												info.setVersionName(JsonUtils.getJSONString(infoObj,"apk_version_name"));
												info.setAppName(JsonUtils.getJSONString(infoObj,"name_zh"));
												info.setAppSize(JsonUtils.getJSONString(infoObj,"apk_size"));
												info.setAppIconUrl(JsonUtils.getJSONString(infoObj,"icon"));
												appInfos.add(info);
											}

											DownloadService.getDownloadManager(mContext).getInstalledAppList().addAll(appInfos);
										}
									} else {
										ToastUtils.toastShow(mContext, msg);
									}
									if (mCallBack != null) {
										mCallBack.doInstalledCallBack();
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

							@Override
							public void onErrorResponse(VolleyError error) {
								// TODO Auto-generated method stub
								// ToastUtils.toastShow(IntroActivity.this,
								// error.getLocalizedMessage());
							}

						});
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setUpdateData(List<DownloadInfo> appInfos) {

		try {
			JSONArray appArr = new JSONArray();
			for (int i = 0; i < appInfos.size(); i++) {
				JSONObject appJson = new JSONObject();
				DownloadInfo app = appInfos.get(i);
				appJson.put("apk_package_name", app.getApkPackageName());
				appJson.put("apk_version_code", app.getVersionCode());
				appArr.put(appJson);
			}
			if (mContext != null) {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("checklist", appArr.toString());
				VolleyUtils.requestString(mContext, ApiUrl.UPDATE_LIST, params,
						this, new VolleyCallback<String>() {

							@Override
							public void onResponse(String response) {
								try {
									AppLog.redLog("jjjjj_000000000__update___",response);
									JSONObject responseJson = new JSONObject(response);

									int status = responseJson.getInt("status");
									String msg = responseJson.getString("message");
									DownloadService.getDownloadManager(mContext).getUpdateAppList().clear();
									if (status == 200) {
										List<DownloadInfo> appInfos = new ArrayList<DownloadInfo>();
										JSONArray bannerArray = responseJson.getJSONArray("data");
										if (bannerArray != null&& bannerArray.length() > 0) {
											int len = bannerArray.length();
											for (int i = 0; i < len; i++) {
												JSONObject infoObj = bannerArray.getJSONObject(i);
												DownloadInfo info = new DownloadInfo();
												info.setAppId(JsonUtils.getJSONLong(infoObj,"id"));
												info.setApkPackageName(JsonUtils.getJSONString(infoObj,"apk_package_name"));
												info.setVersionCode(JsonUtils.getJSONInt(infoObj,"apk_version_code"));
												info.setVersionName(JsonUtils.getJSONString(infoObj,"apk_version_name"));
												info.setAppName(JsonUtils.getJSONString(infoObj,"name_zh"));
												// info.setApkMark(infoObj
												// .getInt("game_rank"));
												// info.setAppDes(infoObj
												// .getString("synopsis"));
												info.setAppSize(JsonUtils.getJSONString(infoObj,"apk_size"));
												info.setApkDownloadUrl(JsonUtils.getJSONString(infoObj,"apk_url"));
												info.setAppIconUrl(JsonUtils.getJSONString(infoObj,"icon"));
												appInfos.add(info);
											}

											DownloadService.getDownloadManager(mContext).getUpdateAppList().addAll(appInfos);
										}
									} else {
										ToastUtils.toastShow(mContext, msg);
									}
									if (mCallBack != null) {
										mCallBack.doUpdateCallBack();
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

							@Override
							public void onErrorResponse(VolleyError error) {
								// TODO Auto-generated method stub
								// ToastUtils.toastShow(IntroActivity.this,
								// error.getLocalizedMessage());
							}

						});
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public interface MyCallBack {
		public void doUpdateCallBack();

		public void doInstalledCallBack();
	}
}
