package com.xtone.game87873.section.info;

import java.util.HashMap;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.sohu.cyan.android.sdk.api.CyanSdk;
import com.sohu.cyan.android.sdk.exception.CyanException;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.base.SwipeBackActivity;
import com.xtone.game87873.general.db.UserAccountDao;
import com.xtone.game87873.general.download.DownloadManager;
import com.xtone.game87873.general.manager.PreferenceManager;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.AppStorage;
import com.xtone.game87873.general.utils.AppUtil;
import com.xtone.game87873.general.utils.AppUtils;
import com.xtone.game87873.general.utils.JsonUtils;
import com.xtone.game87873.general.utils.NetworkUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.section.StartUtils;
import com.xtone.game87873.section.dialog.UpdateDialog;
import com.xtone.game87873.section.dialog.UserProgressDialog;
import com.xtone.game87873.section.gift.GiftPrefectureFragment;

@EActivity(R.layout.activity_setting)
public class SettingActivity extends SwipeBackActivity {

	@ViewById
	TextView tv_headTitle, tv_version, tvDownloadAddress;
	@ViewById
	ImageView iv_headLeft, ivIsNoteOn,iv_autoDelete,iv_onlyWifiDownload;
	@ViewById
	LinearLayout ll_aboutUs, ll_recommed_to_friends, ll_suggestion_feedback,
			ll_check_for_updates;

	@ViewById
	Button btn_logout;
	private PreferenceManager pm;
	private Handler handler = new Handler();
	private PushAgent mPushAgent;

	@Click(R.id.ll_aboutUs)
	void aboutUsClick() {
		Intent intent = new Intent(this, AboutUsActivity_.class);
		startActivity(intent);
	}
	@Click(R.id.iv_autoDelete)
	void setIsAutoDelete() {
		if (AppStorage.isAutoDeletePackage()) {
			AppStorage.setIsAutoDeletePackage(false);
			iv_autoDelete.setImageResource(R.drawable.btn_set_off);
		} else {
			AppStorage.setIsAutoDeletePackage(true);
			iv_autoDelete.setImageResource(R.drawable.btn_set_on);
		}
	}
	@Click(R.id.iv_onlyWifiDownload)
	void setIsDownloadOnlyWifi() {
		if (AppStorage.isDownloadOnlyWifi()) {
			AppStorage.setIsDownloadOnlyWifi(false);
			iv_onlyWifiDownload.setImageResource(R.drawable.btn_set_off);
		} else {
			AppStorage.setIsDownloadOnlyWifi(true);
			iv_onlyWifiDownload.setImageResource(R.drawable.btn_set_on);
		}
	}
	@Click(R.id.ivIsNoteOn)
	void setReceiveNote() {
		ivIsNoteOn.setClickable(false);
		if (mPushAgent.isEnabled()) {
			mPushAgent.disable(new IUmengUnregisterCallback() {

				@Override
				public void onUnregistered(String arg0) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							ivIsNoteOn.setImageResource(R.drawable.btn_set_off);
							ivIsNoteOn.setClickable(true);
							AppStorage.setIsReceiveNotification(false);
						}
					});

				}
			});
		} else {
			mPushAgent.enable(new IUmengRegisterCallback() {

				@Override
				public void onRegistered(String arg0) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							ivIsNoteOn.setImageResource(R.drawable.btn_set_on);
							ivIsNoteOn.setClickable(true);
							AppStorage.setIsReceiveNotification(true);
						}
					});

				}
			});
		}
	}

	@AfterViews
	public void afterViews() {
		initView();
	}

	private void initView() {
		pm = new PreferenceManager(this);
		tv_headTitle.setText(R.string.setting);
		tvDownloadAddress.setText(getString(R.string.package_position_content)
				+ DownloadManager.APK_SAVE_PATH);
		try {
			tv_version.setText(AppUtil.getAppVersionName(this));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (pm.getUserId() == -1) {
			btn_logout.setVisibility(View.GONE);
		}
		// 友盟消息推送
		mPushAgent = PushAgent.getInstance(this);
		if (mPushAgent.isEnabled()) {
			ivIsNoteOn.setImageResource(R.drawable.btn_set_on);
		} else {
			ivIsNoteOn.setImageResource(R.drawable.btn_set_off);
		}

		if (AppStorage.isAutoDeletePackage()) {
			iv_autoDelete.setImageResource(R.drawable.btn_set_on);
		} else {
			iv_autoDelete.setImageResource(R.drawable.btn_set_off);
		}
		if (AppStorage.isDownloadOnlyWifi()) {
			iv_onlyWifiDownload.setImageResource(R.drawable.btn_set_on);
		} else {
			iv_onlyWifiDownload.setImageResource(R.drawable.btn_set_off);
		}
	}

	@Click(R.id.iv_headLeft)
	void backClick() {
		finish();
	}

	@Click(R.id.ll_suggestion_feedback)
	void toFeedbackClick() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			ToastUtils.toastShow(this, "网络不可用！");
			return;
		}
		Intent intent = new Intent(this, FeedbackActivity_.class);
		startActivity(intent);
	}

	@Click(R.id.ll_check_for_updates)
	void checkForUpdateClick() {
//		uMengUpdate();
		checkUpdate();
	}

	@Click(R.id.btn_logout)
	void logoutClick() {
		if (pm.getUserId() == -1) {
			ToastUtils.toastShow(this, "您还未登录！");
			return;
		}
		pm.setUserId(-1);
		pm.setUserNick("");
		pm.setUserFigureUrl("");
		pm.setUserMobile("");
		setResult(RESULT_OK);
		new UserAccountDao(this).deleteAll();
		sendLogoutBroadcast();
		
		try {
			CyanSdk.getInstance(this).logOut();
		} catch (CyanException e) {
			e.printStackTrace();
		}
		finish();
	}

	private void sendLogoutBroadcast() {
		Intent intent = new Intent(GiftPrefectureFragment.ACTION_LOGOUT_OR_LOGIN);
		sendBroadcast(intent);
	}

	private void checkUpdate() {
		HashMap<String, String> params = new HashMap<String, String>();
		final int versionCode = AppUtils.getVersionCode(this);
		params.put("version_code", versionCode + "");
		VolleyUtils.requestString(this, ApiUrl.CHECK_UPDATE, params, this,
				new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						try {
							AppLog.redLog("CHECK_UPDATE__", response);
							JSONObject responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200 && responseJson.has("data")) {
								JSONArray jsonArray = responseJson
										.getJSONArray("data");
								if (jsonArray != null && jsonArray.length() > 0) {
									JSONObject jsonObj = jsonArray
											.getJSONObject(0);
									int code = JsonUtils.getJSONInt(jsonObj,
											"version_code");
									String log = JsonUtils.getJSONString(
											jsonObj, "update_remark");
									String downloadUrl = JsonUtils
											.getJSONString(jsonObj,
													"update_url");
									if (versionCode < code) {
										new UpdateDialog(SettingActivity.this,
												downloadUrl, log).show();
										UserProgressDialog.getInstane()
												.dismiss();
									}

								}
							} else {
								ToastUtils.toastShow(SettingActivity.this, msg);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
	}
	
	private void uMengUpdate() {
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case UpdateStatus.Yes: // has update
					UmengUpdateAgent.showUpdateDialog(SettingActivity.this,
							updateInfo);
					break;
				case UpdateStatus.No: // has no update
					Toast.makeText(SettingActivity.this, "当前版本为最新版本",
							Toast.LENGTH_SHORT).show();
					break;
				// case UpdateStatus.NoneWifi: // none wifi
				// Toast.makeText(SettingActivity.this, "没有wifi连接， 只在wifi下更新",
				// Toast.LENGTH_SHORT).show();
				// break;
				case UpdateStatus.Timeout: // time out
					Toast.makeText(SettingActivity.this, "超时",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});
		UmengUpdateAgent.forceUpdate(this);
	}

	@Click(R.id.ll_recommed_to_friends)
	void shareClick() {
		StartUtils.startCustomShareDialogActivity(this);
	}

}
