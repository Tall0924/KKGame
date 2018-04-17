package com.xtone.game87873.section.game;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.contants.UmengEvent;
import com.xtone.game87873.contants.UserActions;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.base.SwipeBackFragmentActivity;
import com.xtone.game87873.general.download.DownloadInfo;
import com.xtone.game87873.general.download.DownloadManager;
import com.xtone.game87873.general.download.DownloadService;
import com.xtone.game87873.general.manager.PreferenceManager;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.AppStorage;
import com.xtone.game87873.general.utils.AppUtils;
import com.xtone.game87873.general.utils.ImageLoaderUtils;
import com.xtone.game87873.general.utils.JsonUtils;
import com.xtone.game87873.general.utils.NetworkUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.BadgeView;
import com.xtone.game87873.section.StartUtils;
import com.xtone.game87873.section.dialog.CommonDialog;
import com.xtone.game87873.section.entity.Information;

/**
 * 游戏详情页
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-7 下午3:08:35
 */
@EActivity(R.layout.activity_appdetail)
public class AppDetailActivity extends SwipeBackFragmentActivity {
	public static final String GAME_NAME = "game_name";
	public static final String GAME_ID = "game_id";
	private long gameId;
	private BaseFragment[] mFragments = new BaseFragment[2];
	private LayoutInflater mLayoutInflater;
	private DownloadInfo downloadInfo;
	private int giftCount;
	private String url;// 分享的地址

	private boolean isCollect = false;
	private List<DownloadInfo> downloadedList, downloadingList, downloadList;
	private DownloadInfo infoDb;
	private ButtonChangeHolder holder;
	private DownloadManager downloadManager;
	@ViewById
	TextView tvTitle;
	@ViewById
	RelativeLayout rlContent, gameItem;

	@ViewById
	LinearLayout il_loadFailure, llContent, llTitle;
	@ViewById
	Button btn_refresh;// 刷新按钮
	@ViewById
	ImageView ivMsg, ivReturn, ivShare, ivDownload;
	@ViewById
	TextView tvMsg, tv_Msg2;

	@ViewById
	ProgressBar pb_download_btn;// 底部进度条
	@ViewById
	Button btn_download;// 底部下载按钮

	@ViewById
	ImageView ivIcon, ivCollect;// 游戏、收藏icon
	@ViewById
	TextView tvGameName, tvGameDes, tvState;

	@ViewById
	RadioGroup rgNav;
	@ViewById
	RadioButton rbDetail, rbGift;
	@ViewById
	ImageView iv1, iv2;
	@ViewById
	ViewPager vpContent;

	private BadgeView badge;// 角标
	private BroadcastReceiver receiveBroadCast, tagReceiver;
	private List<DownloadInfo> mRecommangGames;
	private List<Information> mInformations;
	private String prompt, updateLog, state, language;
	private List<ImageView> lines;
	private int[] colors = new int[] { R.drawable.bg_gamedetail_top1,
			R.drawable.bg_gamedetail_top2, R.drawable.bg_gamedetail_top3,
			R.drawable.bg_gamedetail_top4, R.drawable.bg_gamedetail_top5,
			R.drawable.bg_gamedetail_top6, R.drawable.bg_gamedetail_top7 };
	private int colorRes;
	private Drawable whiteDrawable, colorDrawable;

	@Click(R.id.ivCollect)
	void collect() {

		if (downloadInfo == null) {
			return;
		}
		long userId = new PreferenceManager(this).getUserId();
		if (userId == -1) {
			showLoginDialog();
			return;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type", "1");// 1游戏，2专题
		params.put("uid", userId + "");
		params.put("targetid", downloadInfo.getAppId() + "");// 1游戏，2专题
		final String url;
		if (isCollect) {
			url = ApiUrl.DEL_COLLECTION;
		} else {
			url = ApiUrl.ADD_COLLECTION;
		}
		VolleyUtils.requestString(this, url, params, this,
				new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						try {
							AppLog.redLog("jjjjj___rank___", response);
							JSONObject responseJson = new JSONObject(response);

							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {

								if (url == ApiUrl.DEL_COLLECTION) {
									isCollect = false;
									ivCollect
											.setImageResource(R.drawable.ic_praise);
									ToastUtils.toastShow(
											AppDetailActivity.this,
											R.string.collect_cancel);

									// setResult(MyCollectionGameFragment.RESULT_CODE_NO_COLLECT);
								} else {
									isCollect = true;
									ivCollect
											.setImageResource(R.drawable.ic_praised);
									ToastUtils.toastShow(
											AppDetailActivity.this,
											R.string.collect_success);
									// setResult(MyCollectionGameFragment.RESULT_CODE_COLLECT);
								}
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

	// 提示登录
	private void showLoginDialog() {
		CommonDialog dialog = new CommonDialog(this,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						if (arg1 == CommonDialog.CLICK_OK) {
							StartUtils.startLogin(AppDetailActivity.this);
						}
						dialog.cancel();
					}
				});
		dialog.setContent(getResources().getString(R.string.login_to_collect));
		dialog.show();
	}

	// 没有网络
	private void noNetwork() {
		rlContent.setVisibility(View.GONE);
		il_loadFailure.setVisibility(View.VISIBLE);
		ivMsg.setImageResource(R.drawable.icon_signal);
		tvMsg.setText("加载失败");
		tvMsg.setVisibility(View.VISIBLE);
		tv_Msg2.setVisibility(View.VISIBLE);
		btn_refresh.setText("重新加载");
	}

	private void reLoadData() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			return;
		}
		il_loadFailure.setVisibility(View.GONE);
		getNetData();
	}

	@Click(R.id.btn_refresh)
	void refreshClick() {
		reLoadData();
	}

	@Click(R.id.ivReturn)
	void toReturn() {
		finish();
	}

	@Click(R.id.ivDownload)
	void toDownloadPage() {
		StartUtils.startMyGame(this);
	}

	@Click(R.id.ivShare)
	void shareClick() {
		if (downloadInfo != null) {
			Bundle bundle = new Bundle();
			bundle.putString("share_content",Html.fromHtml(downloadInfo.getGameContent()).toString());
			bundle.putString("share_url", url);
			bundle.putString("share_title", downloadInfo.getAppName());
			bundle.putString("icon", downloadInfo.getAppIconUrl());
			StartUtils.startCustomShareDialogActivity(AppDetailActivity.this,bundle);
		}
	}

	@AfterViews
	void afterViews() {

		// 角标显示当前下载中的游戏有几个
		badge = new BadgeView(this, ivDownload);
		downloadManager = DownloadService.getDownloadManager(this);
		downloadedList = downloadManager.getDownloadedInfoList();
		downloadingList = downloadManager.getDownloadingInfoList();
		downloadList = downloadManager.getHasDownList();
		if (downloadingList.size() > 0) {
			badge.setText(downloadingList.size() + "");
			badge.show();
		} else {
			badge.hide();
		}
		// 注册广播接收
		receiveBroadCast = new BadgeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(UserActions.ACTION_DOWNLOAD_CHANGE); // 只有持有相同的action的接受者才能接收此广播
		registerReceiver(receiveBroadCast, filter);

		// 广播接收器处理头部的显示和隐藏
		tagReceiver = new TagsReceiver();

		int i = new Random().nextInt(7);
		colorRes = colors[i];// 获取随机颜色
		whiteDrawable = getResources().getDrawable(R.color.bg_title);
		colorDrawable = getResources().getDrawable(colorRes);

		// btnDownload.setVisibility(View.GONE);
		Intent intent = getIntent();
		// tvTitle.setText(intent.getStringExtra(GAME_NAME));
		tvTitle.setText(R.string.game_zone);
		mInformations = new ArrayList<Information>();
		mRecommangGames = new ArrayList<DownloadInfo>();
		gameId = intent.getLongExtra(GAME_ID, 0);

		mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		GameStatistication.AddGameStatistics(this, gameId,
				GameStatistication.TYPE_CLICK);// 游戏点击统计

		getNetData();
	}

	private void getNetData() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			noNetwork();
			return;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("gid", gameId + "");
		// params.put("client", ApiUrl.SOURCE_APP + "");// 2为app，1pc
		long userId = new PreferenceManager(this).getUserId();
		if (userId != -1) {
			params.put("userid", userId + "");
		}
		VolleyUtils.requestStringWithLoading(this, ApiUrl.GAME_INFO, params,
				this, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							AppLog.redLog("appdetail______", response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								JSONObject infoObj = responseJson.getJSONObject("data");
								downloadInfo = new DownloadInfo();
								downloadInfo.setAppId(JsonUtils.getJSONLong(infoObj,"id"));
								downloadInfo.setApkPackageName(JsonUtils.getJSONString(infoObj,"apk_package_name"));
								downloadInfo.setVersionCode(JsonUtils.getJSONInt(infoObj,"apk_version_code"));
								downloadInfo.setVersionName(JsonUtils.getJSONString(infoObj,"apk_version_name"));
								downloadInfo.setAppName(JsonUtils.getJSONString(infoObj,"name_zh"));
								downloadInfo.setApkMark(JsonUtils.getJSONInt(infoObj,"game_rank"));
								downloadInfo.setAppDes(JsonUtils.getJSONString(infoObj,"synopsis"));
								downloadInfo.setTypeName(JsonUtils.getJSONString(infoObj,"typename"));
								downloadInfo.setAppSize(JsonUtils.getJSONString(infoObj,"apk_size"));
								downloadInfo.setApkDownloadUrl(JsonUtils.getJSONString(infoObj,"apk_url"));
								downloadInfo.setAppIconUrl(JsonUtils.getJSONString(infoObj,"icon"));
								downloadInfo.setGameContent(JsonUtils.getJSONString(infoObj,"game_content"));
								url = JsonUtils.getJSONString(infoObj, "url");
								prompt = JsonUtils.getJSONString(infoObj,
										"game_prompt");
								updateLog = JsonUtils.getJSONString(infoObj,
										"game_update_log");
								List<String> picUrls = new ArrayList<String>();
								String picStrs = JsonUtils.getJSONString(infoObj,"screenshot");
								String[] picArrs = picStrs.split(",");
								if (picArrs != null && picArrs.length > 0) {
									int picLen = picArrs.length;
									for (int j = 0; j < picLen; j++) {
										picUrls.add(picArrs[j]);
									}
								}
								downloadInfo.setPicUrls(picUrls);
								if (infoObj.has("collection")) {
									int col = JsonUtils.getJSONInt(infoObj,"collection");
									if (col == 1) {
										isCollect = true;
									} else {
										isCollect = false;
									}
								}
								// giftCount = JsonUtils.getJSONInt(infoObj,
								// "libaocount");
								JSONArray gameArray = infoObj
										.getJSONArray("related_recommend");
								mRecommangGames.clear();
								if (gameArray != null && gameArray.length() > 0) {
									int len = gameArray.length();
									for (int i = 0; i < len; i++) {
										JSONObject gameObj = gameArray
												.getJSONObject(i);
										DownloadInfo info = new DownloadInfo();
										info.setAppId(JsonUtils.getJSONLong(
												gameObj, "id"));
										info.setAppName(JsonUtils
												.getJSONString(gameObj,
														"name_zh"));
										info.setAppIconUrl(JsonUtils
												.getJSONString(gameObj, "icon"));
										mRecommangGames.add(info);
									}
								}
								JSONArray infoArray = infoObj
										.getJSONArray("information");
								mInformations.clear();
								if (infoArray != null && infoArray.length() > 0) {
									int len = infoArray.length();
									for (int i = 0; i < len; i++) {
										JSONObject informationObj = infoArray
												.getJSONObject(i);
										Information info = new Information();
										info.setId(JsonUtils.getJSONLong(
												informationObj, "id"));
										info.setTitle(JsonUtils.getJSONString(
												informationObj, "title"));
										info.setType(JsonUtils.getJSONInt(
												informationObj, "type"));
										mInformations.add(info);
									}
								}
								int isFree = JsonUtils.getJSONInt(infoObj,
										"price");
								int online = JsonUtils.getJSONInt(infoObj,
										"online");
								language = JsonUtils.getJSONString(infoObj,
										"game_language");
								state = "官方   安全";
								if (online == 1) {
									// state += "   " + "联网";
								} else {
									state += "   " + "无需联网";
								}
								if (isFree == 1) {
									// state += "   " + "道具付费";
								} else {
									state += "   " + "免费";
								}

								setView();
							} else {
								ToastUtils.toastShow(AppDetailActivity.this,
										msg);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub

					}
				});
	}

	private void setView() {
		// 友盟计数统计
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("game_id", gameId + "");
		map.put("game_name", downloadInfo.getAppName());
		MobclickAgent.onEvent(this, UmengEvent.CLICK_GAME, map);

		rlContent.setVisibility(View.VISIBLE);

		ivReturn.setImageResource(R.drawable.btn_head_back_2);
		tvTitle.setText(R.string.game_zone);
		tvTitle.setTextColor(getResources().getColor(android.R.color.white));
		ivDownload.setImageResource(R.drawable.btn_index_title_download);
		ivShare.setImageResource(R.drawable.btn_head_share_white);
		llTitle.setBackgroundResource(colorRes);
		gameItem.setBackgroundResource(colorRes);

		if (isCollect) {
			ivCollect.setImageResource(R.drawable.ic_praised);
		} else {
			ivCollect.setImageResource(R.drawable.ic_praise);
		}
		tvGameName.setText(downloadInfo.getAppName());
		tvGameDes.setText(language + "    " + downloadInfo.getAppSize()
				+ "    " + "V" + downloadInfo.getVersionName());
		tvState.setText(state);
		// VolleyUtils.setURLImage(mQueue, ivIcon, downloadInfo.getAppIconUrl(),
		// R.drawable.icon_game_loading, R.drawable.icon_game_loading);
		ImageLoaderUtils.loadImgWithConner(ivIcon,
				downloadInfo.getAppIconUrl(), R.drawable.icon_game_loading,
				R.drawable.icon_game_loading);
		holder = new ButtonChangeHolder();

		// 1.2.3版本修改-改数据库表结构
		// downloadInfo.setId(downloadInfo.getAppId() + "");
		downloadInfo.setApkSavePath(DownloadManager.APK_SAVE_PATH+ downloadInfo.getApkDownloadUrl().substring(
				downloadInfo.getApkDownloadUrl().lastIndexOf("/") + 1));

		setButton();

		lines = new ArrayList<ImageView>();
		lines.add(iv1);
		lines.add(iv2);
		for (int i = 0; i < lines.size(); i++) {
			lines.get(i).setVisibility(View.INVISIBLE);
		}
		lines.get(0).setVisibility(View.VISIBLE);

		vpContent.setAdapter(new UserPagerAdapter(getSupportFragmentManager()));
		rgNav.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
				case R.id.rbDetail:
					vpContent.setCurrentItem(0);
					break;
				case R.id.rbGift:
					vpContent.setCurrentItem(1);
					break;

				default:
					break;
				}
			}
		});
		vpContent.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					rbDetail.setChecked(true);
					break;
				case 1:
					rbGift.setChecked(true);
					break;

				default:
					break;
				}
				for (int i = 0; i < lines.size(); i++) {
					lines.get(i).setVisibility(View.INVISIBLE);
				}
				lines.get(arg0).setVisibility(View.VISIBLE);
				if (mFragments[arg0] != null) {
					mFragments[arg0].initView();
				}
			}
		});
		rbDetail.setChecked(true);

	}

	private class UserPagerAdapter extends FragmentPagerAdapter {

		public UserPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int arg0) {
			switch (arg0) {
			case 0:
				if (mFragments[0] == null) {
					AppDetailGameFragment fragment = new AppDetailGameFragment();
					fragment.setDatas(downloadInfo, mInformations,mRecommangGames, prompt, updateLog, url);
					mFragments[0] = fragment;
				}
				break;
			case 1:
				if (mFragments[1] == null) {
					AppDetailGiftFragment fragment = new AppDetailGiftFragment_();
					fragment.setGameId(gameId);
					mFragments[1] = fragment;
				}
				break;
			}
			return mFragments[arg0];
		}

		@Override
		public int getCount() {
			return mFragments.length;
		}
	}

	private void setButton() {
		if (downloadInfo != null) {
			int state = AppUtils.getApkState(this,
					downloadInfo.getApkPackageName(),
					downloadInfo.getVersionCode());
			if (state == AppUtils.APP_INSTALLED) {
				btn_download.setText(R.string.open);
				// pb_download_btn.setProgress(100);
				downloadInfo.setAppState(DownloadInfo.STATE_HAS_INSTALLED);
				btn_download.setBackgroundResource(R.drawable.btn_download_info_detail);
			} else {
				infoDb = null;
				for (DownloadInfo info : downloadList) {
					if (info.equals(downloadInfo)) {
						infoDb = info;
						// 1.2.3版本修改-改数据库表结构
						downloadInfo.setId(info.getId());
						break;
					}
				}
				if (infoDb != null) {

					if (downloadedList.contains(downloadInfo)) {
						btn_download.setText(R.string.install);
						pb_download_btn.setProgress(100);
						downloadInfo.setAppState(DownloadInfo.STATE_HAS_DOWNLOADED);
						btn_download.setBackgroundResource(R.drawable.btn_download_info_detail);
					} else if (downloadingList.contains(downloadInfo)) {
						btn_download.setBackgroundResource(android.R.color.transparent);
						HttpHandler<File> handler = infoDb.getDownloadHandler();
						downloadingList.set(downloadingList.indexOf(downloadInfo), infoDb);
						if (handler != null) {
							RequestCallBack callBack = handler.getRequestCallBack();
							if (callBack instanceof DownloadManager.ManagerCallBack) {
								DownloadManager.ManagerCallBack managerCallBack = (DownloadManager.ManagerCallBack) callBack;
								managerCallBack.setBaseCallBack(new DownloadRequestCallBack());
							}
							callBack.setUserTag(new WeakReference<>(holder));
						}
						if (handler != null&& (handler.getState() != HttpHandler.State.CANCELLED && handler
										.getState() != HttpHandler.State.FAILURE)) {
							downloadInfo.setAppState(DownloadInfo.STATE_DOWNLOADING);
							String str;
							if (handler.getState() == HttpHandler.State.WAITING) {
								str = getString(R.string.waiting_download);
							} else {
								str = getString(R.string.pause);
							}
							if (infoDb.getCurrLength() > 0) {
								float curr = (float) infoDb.getCurrLength()/ (float) infoDb.getContentLength();
								btn_download.setText(str + "("+ getString(R.string.has_download)+ (int) (curr * 100) + " %" + ")");
								pb_download_btn.setProgress((int) (curr * 100));
							} else {
								btn_download.setText(str + "("+ getString(R.string.has_download)+ "0 %" + ")");
								pb_download_btn.setProgress(0);
							}
						} else {
							String temp = "";
							if (handler != null&& handler.getState() == HttpHandler.State.FAILURE) {
								temp = getString(R.string.try_again);

							} else {
								temp = getString(R.string.go_on);
							}
							if (infoDb.getCurrLength() > 0) {
								float curr = (float) infoDb.getCurrLength()/ (float) infoDb.getContentLength();
								btn_download.setText(temp + "("+ getString(R.string.has_download)+ (int) (curr * 100) + " %" + ")");
								pb_download_btn.setProgress((int) (curr * 100));
							} else {
								btn_download.setText(temp + "("+ getString(R.string.has_download)+ "0 %" + ")");
								pb_download_btn.setProgress(0);
							}
							downloadInfo.setAppState(DownloadInfo.STATE_PAUSE);
						}
					}
				} else {
					btn_download.setBackgroundResource(R.drawable.btn_download_info_detail);
					downloadInfo.setAppState(DownloadInfo.STATE_NO_DOWNLOAD);
					pb_download_btn.setProgress(0);
					if (state == AppUtils.APP_UPDATE) {
						btn_download.setText(R.string.update);
					} else if (state == AppUtils.APP_NO_EXIST) {
						btn_download.setText(R.string.download);
					}

				}
			}
		}
	}

	private class ButtonChangeHolder {

		public void update() {
			if (!isFinishing()) {
				if (infoDb != null && infoDb.getDownloadHandler() != null) {
					HttpHandler.State state = infoDb.getDownloadHandler().getState();
					switch (state) {
					case LOADING:
						if (infoDb.getContentLength() > 0) {
							float curr = (float) infoDb.getCurrLength()/ (float) infoDb.getContentLength();
							btn_download.setText(getString(R.string.pause)+ "(" + getString(R.string.has_download)+ (int) (curr * 100) + " %" + ")");
							pb_download_btn.setProgress((int) (curr * 100));
						} else {
							btn_download.setText(getString(R.string.pause)+ "(" + getString(R.string.has_download)+ "0 %" + ")");
							pb_download_btn.setProgress(0);
						}
						break;
					default:
						setButton();
						break;
					}
				}
			}
		}
	}

	private class DownloadRequestCallBack extends RequestCallBack<File> {

		@SuppressWarnings("unchecked")
		private void refreshListItem() {
			if (userTag == null)
				return;
			WeakReference<ButtonChangeHolder> tag = (WeakReference<ButtonChangeHolder>) userTag;
			ButtonChangeHolder holder = tag.get();
			if (holder != null) {
				holder.update();
			}
		}

		@Override
		public void onStart() {
			refreshListItem();
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			refreshListItem();
		}

		@Override
		public void onSuccess(ResponseInfo<File> responseInfo) {
			refreshListItem();
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			refreshListItem();
		}

		@Override
		public void onCancelled() {
			refreshListItem();
		}
	}

	@Click(R.id.btn_download)
	void bottomDownload() {
		download();
	}

	private void download() {
		if (downloadInfo != null) {
			int state = downloadInfo.getAppState();
			switch (state) {
			case DownloadInfo.STATE_HAS_INSTALLED:
				boolean b = AppUtils.startApp(this,downloadInfo.getApkPackageName());
				if (!b) {
					ToastUtils.toastShow(this, "该游戏已卸载");
					DownloadManager downloadManager = DownloadService.getDownloadManager(this);
					List<DownloadInfo> installedList = downloadManager.getInstalledAppList();
					if (installedList.contains(downloadInfo)) {
						installedList.remove(downloadInfo);
					}
				}
				break;
			case DownloadInfo.STATE_HAS_DOWNLOADED:
				if (infoDb != null) {
					boolean isInstall = AppUtils.installApp(this, new File(infoDb.getApkSavePath()));
					if (!isInstall) {
						ToastUtils.toastShow(this, "该游戏安装包已删除，请重新下载！");
						try {
							DownloadService.getDownloadManager(this).removeDownload(infoDb);
						} catch (DbException e) {
							e.printStackTrace();
						}
					}
				}
				break;
			case DownloadInfo.STATE_DOWNLOADING:
				if (infoDb != null) {
					downloadManager.stopDownload(infoDb);
				}
				break;
			case DownloadInfo.STATE_PAUSE:
				if (infoDb != null) {
					if (!NetworkUtils.isNetworkAvailable(this)) {
						ToastUtils.toastShow(this, "网络不可用！");
						// return;
					} else {
						if (AppStorage.isDownloadOnlyWifi()) {
							if (!NetworkUtils.isWIfi(this)) {
								// ToastUtils.toastShow(mContext,
								// "您正在使用手机流量下载");
								CommonDialog dialog = new CommonDialog(this,
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(DialogInterface dialog,int arg1) {
												if (arg1 == CommonDialog.CLICK_OK) {
													downloadManager.resumeDownload(infoDb,null);
													setButton();
												}
												dialog.dismiss();
											}
										});
								dialog.setContent(getResources().getString(R.string.network_prompts));
								dialog.setOkBtnText(getResources().getString(
										R.string.continue_download));
								dialog.show();
								return;
							}
						}
					}
					downloadManager.resumeDownload(infoDb, null);
				}
				break;
			case DownloadInfo.STATE_NO_DOWNLOAD:
				if (!NetworkUtils.isNetworkAvailable(this)) {
					ToastUtils.toastShow(this, "网络不可用！");
					// return;
				} else {
					if (AppStorage.isDownloadOnlyWifi()) {
						if (!NetworkUtils.isWIfi(this)) {
							CommonDialog dialog = new CommonDialog(this,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int arg1) {
											if (arg1 == CommonDialog.CLICK_OK) {
												downloadManager.addAppTask(downloadInfo, null);
												setButton();
											}
											dialog.dismiss();
										}
									});
							dialog.setContent(getResources().getString(
									R.string.network_prompts));
							dialog.setOkBtnText(getResources().getString(
									R.string.continue_download));
							dialog.show();
							return;
							// ToastUtils.toastShow(mContext, "您正在使用手机流量下载");
						}
					}
				}
				downloadManager.addAppTask(downloadInfo, null);
				break;
			default:
				break;
			}
			setButton();
		}
	}

	/**
	 * 广播接收器，改变角标的显示
	 * 
	 */
	class BadgeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (downloadingList.size() > 0) {
				badge.setText(downloadingList.size() + "");
				badge.show();
			} else {
				badge.hide();
			}
		}
	}

	private int time = 300;

	private AnimationListener showListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			TransitionDrawable drawable = new TransitionDrawable(new Drawable[] { whiteDrawable, colorDrawable });
			llTitle.setBackgroundDrawable(drawable);
			drawable.startTransition(time);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			ivReturn.setImageResource(R.drawable.btn_head_back_2);
			tvTitle.setText(R.string.game_zone);
			tvTitle.setTextColor(getResources().getColor(android.R.color.white));
			ivDownload.setImageResource(R.drawable.btn_index_title_download);
			ivShare.setImageResource(R.drawable.btn_head_share_white);
			// llTitle.setBackgroundResource(colorRes);
		}
	};

	private AnimationListener hideListener = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			TransitionDrawable drawable = new TransitionDrawable(
					new Drawable[] { colorDrawable, whiteDrawable });
			llTitle.setBackgroundDrawable(drawable);
			drawable.startTransition(time);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			ivReturn.setImageResource(R.drawable.btn_head_back);
			tvTitle.setText(downloadInfo.getAppName());
			tvTitle.setTextColor(getResources().getColor(R.color.index_title));
			ivDownload.setImageResource(R.drawable.btn_title_download);
			ivShare.setImageResource(R.drawable.btn_head_share);
			// llTitle.setBackgroundResource(R.color.bg_title);
		}
	};

	/**
	 * 广播接收器，头部的显示与隐藏
	 * 
	 */
	class TagsReceiver extends BroadcastReceiver {
		private boolean isTagShow = true;

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (!isTagShow&& UserActions.ACTION_APP_DETAIL_TOP_SHOW.equals(action)) {
				android.widget.RelativeLayout.LayoutParams param = (android.widget.RelativeLayout.LayoutParams) llContent.getLayoutParams();
				param.setMargins(0, 0, 0, 0);
				llContent.setLayoutParams(param);
				Animation anim = new TranslateAnimation(0, 0,
						gameItem.getMeasuredHeight() * (-1), 0);
				anim.setDuration(time);
				anim.setAnimationListener(showListener);
				llContent.startAnimation(anim);
				isTagShow = true;
			}
			if (isTagShow&& UserActions.ACTION_APP_DETAIL_TOP_HIDE.equals(action)) {
				android.widget.RelativeLayout.LayoutParams param = (android.widget.RelativeLayout.LayoutParams) llContent
						.getLayoutParams();
				param.setMargins(0, gameItem.getMeasuredHeight() * (-1), 0, 0);
				llContent.setLayoutParams(param);
				Animation anim = new TranslateAnimation(0, 0,gameItem.getMeasuredHeight(), 0);
				anim.setAnimationListener(hideListener);
				anim.setDuration(time);
				llContent.startAnimation(anim);
				isTagShow = false;
			}
		}
	}

	@Override
	public void onResume() {
		setButton();
		super.onResume();
		// 注册广播接收器处理游戏信息的显示和隐藏
		IntentFilter tagFilter = new IntentFilter();
		tagFilter.addAction(UserActions.ACTION_APP_DETAIL_TOP_SHOW);
		tagFilter.addAction(UserActions.ACTION_APP_DETAIL_TOP_HIDE);
		registerReceiver(tagReceiver, tagFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 游戏信息收缩动作只需在显示在最上层进行，所以在这个方法注销接收器
		if (tagReceiver != null) {
			unregisterReceiver(tagReceiver);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 角标数量监听只有页面销毁才注销
		unregisterReceiver(receiveBroadCast);
	}
}
