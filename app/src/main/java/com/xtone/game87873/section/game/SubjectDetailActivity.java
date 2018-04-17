package com.xtone.game87873.section.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sohu.cyan.android.sdk.api.CyanSdk;
import com.umeng.analytics.MobclickAgent;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.contants.PicSize;
import com.xtone.game87873.contants.UmengEvent;
import com.xtone.game87873.contants.UserActions;
import com.xtone.game87873.general.base.SwipeBackActivity;
import com.xtone.game87873.general.download.DownloadInfo;
import com.xtone.game87873.general.download.DownloadManager;
import com.xtone.game87873.general.download.DownloadService;
import com.xtone.game87873.general.manager.PreferenceManager;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.BitmapHelper;
import com.xtone.game87873.general.utils.CommonUtils;
import com.xtone.game87873.general.utils.DensityUtil;
import com.xtone.game87873.general.utils.JsonUtils;
import com.xtone.game87873.general.utils.NetworkUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.BadgeView;
import com.xtone.game87873.general.widget.xlistview.IXListViewListener;
import com.xtone.game87873.general.widget.xlistview.XListView;
import com.xtone.game87873.section.StartUtils;
import com.xtone.game87873.section.entity.SubjectEntity;
import com.xtone.game87873.section.game.adapter.GameListAdapter;

/**
 * 专题详情页
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-9 上午11:32:34
 */
@EActivity(R.layout.activity_list)
public class SubjectDetailActivity extends SwipeBackActivity implements
		IXListViewListener {
	public LayoutInflater mInflater;
	private GameListAdapter mAdapter;
	private TextView tvSubjectIntro;
	private ImageView ivSubject;
	private List<DownloadInfo> mAppInfos;
	private List<String> imgUrls;
	private long subjectId;
	// private String subjectTitle;
	public static final String SUBJECT_ID = "subject_id";
	public static final String SUBJECT_TITLE = "subject_title";
	private Handler handler = new Handler();
	private SubjectEntity subject;
	private int page = 1;
	private final int PAGE_SIZE = 6;
	private int width, height;
	@ViewById
	XListView lvContent;
	@ViewById
	TextView tvTitle;

	@ViewById
	LinearLayout il_loadFailure;
	@ViewById
	Button btn_refresh;
	@ViewById
	ImageView ivMsg, ivDownload;
	@ViewById
	TextView tvMsg, tv_Msg2;

	private BadgeView badge;
	private DownloadManager downloadManager;
	private List<DownloadInfo> downloadingList;
	private BroadcastReceiver receiveBroadCast;

	private boolean isLoading = true;// 是否显示加载框

	// 没有网络
	private void noNetwork() {
		lvContent.setVisibility(View.GONE);
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
		isLoading = true;
		page = 1;
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

	@Click(R.id.tvTitle)
	void titleClick() {
		finish();
	}

	@Click(R.id.ivDownload)
	void toDownloadPage() {
		StartUtils.startMyGame(this);
	}

	@AfterViews
	public void afterViews() {

		// 角标显示当前下载中的游戏有几个
		badge = new BadgeView(this, ivDownload);
		downloadManager = DownloadService.getDownloadManager(this);
		downloadingList = downloadManager.getDownloadingInfoList();
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

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		lvContent.setVisibility(View.GONE);
		View view = mInflater.inflate(R.layout.include_subject_intro, null);
		mAppInfos = new ArrayList<DownloadInfo>();
		imgUrls = new ArrayList<String>();
		width = CommonUtils.getScreenWidth(this) - DensityUtil.dip2px(this, 10)
				* 2;
		height = CommonUtils.getHeightWithSize(this, PicSize.SUBJECT_WIDTH,
				PicSize.SUBJECT_HEIGHT, width);
		tvSubjectIntro = (TextView) view.findViewById(R.id.tvIntro);
		ivSubject = (ImageView) view.findViewById(R.id.ivSubject);
		lvContent.addHeaderView(view);
		mAdapter = new GameListAdapter(this, mAppInfos);
		lvContent.setAdapter(mAdapter);
		lvContent.setXListViewListener(this);
		lvContent.setPullRefreshEnable(true);
		lvContent.setPullLoadEnable(false);
		subjectId = getIntent().getLongExtra(SUBJECT_ID, 0);
		String subjectTitle = getIntent().getStringExtra(SUBJECT_TITLE);
		if (subjectTitle.length() > 11) {
			subjectTitle = subjectTitle.substring(0, 11) + "...";
		}
		tvTitle.setText(subjectTitle);
		// tvTitle.setText(R.string.subject_detail);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				height);
		params.setMargins(BitmapHelper.dip2px(this, 10),
				BitmapHelper.dip2px(this, 5), BitmapHelper.dip2px(this, 10),
				BitmapHelper.dip2px(this, 5));
		ivSubject.setLayoutParams(params);
		lvContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position > 1) {
					Intent intent = new Intent(SubjectDetailActivity.this,
							AppDetailActivity_.class);
					intent.putExtra(AppDetailActivity.GAME_NAME,
							mAppInfos.get(position - 2).getAppName());
					intent.putExtra(AppDetailActivity.GAME_ID,
							mAppInfos.get(position - 2).getAppId());
					startActivity(intent);
				}

			}
		});
		isLoading = true;
		page = 1;
		getNetData();

		CyanSdk.getInstance(this).addCommentToolbar(this,
				"87873game_topic-" + subjectId,
				getIntent().getStringExtra(SUBJECT_TITLE), "");
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

	private void getNetData() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			noNetwork();
			return;
		}
		if (page == 1) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("topicid", subjectId + "");
			params.put("client", ApiUrl.SOURCE_APP + "");
			long userId = new PreferenceManager(this).getUserId();
			if (userId != -1) {
				params.put("userid", userId + "");
			}
			VolleyUtils.requestToString(this, ApiUrl.SUBJECT_INFO, params,
					this, isLoading, new VolleyCallback<String>() {

						@Override
						public void onResponse(String response) {
							onLoad();
							try {
								AppLog.redLog("subject_detail_____", response);
								JSONObject responseJson = new JSONObject(
										response);
								int status = responseJson.getInt("status");
								String msg = responseJson.getString("message");
								if (status == 200) {

									JSONObject dataObj = responseJson
											.getJSONObject("data");
									subject = new SubjectEntity();
									subject.setId(dataObj.getLong("id"));
									subject.setTitle(dataObj.getString("title"));
									subject.setContent(dataObj
											.getString("content"));
									String urlStr = dataObj.getString("banner");
									String[] urlArr = urlStr.split(",");
									imgUrls.clear();
									for (String url : urlArr) {
										imgUrls.add(url);
									}
									if (imgUrls.size() > 0) {

										VolleyUtils.setURLImage(
												SubjectDetailActivity.this,
												ivSubject, imgUrls.get(0),
												R.drawable.pic_banner_loading,
												R.drawable.pic_banner_loading,
												width, height);
									}
									tvSubjectIntro.setText(Html
											.fromHtml(subject.getContent()));
									JSONArray subjectArray = dataObj
											.getJSONArray("games");
									List<DownloadInfo> appInfos = new ArrayList<DownloadInfo>();
									if (subjectArray != null
											&& subjectArray.length() > 0) {
										int len = subjectArray.length();
										for (int i = 0; i < len; i++) {
											JSONObject infoObj = subjectArray
													.getJSONObject(i);
											DownloadInfo info = new DownloadInfo();
											info.setAppId(infoObj.getLong("id"));
											info.setApkPackageName(infoObj
													.getString("apk_package_name"));
											info.setVersionCode(infoObj
													.getInt("apk_version_code"));
											info.setVersionName(infoObj
													.getString("apk_version_name"));
											info.setAppName(infoObj
													.getString("name_zh"));
											info.setApkMark(infoObj
													.getInt("game_rank"));
											// info.setAppDes(infoObj
											// .getString("synopsis"));
											info.setTypeName(JsonUtils
													.getJSONString(infoObj,
															"typename"));
											info.setAppSize(infoObj
													.getString("apk_size"));
											info.setApkDownloadUrl(infoObj
													.getString("apk_url"));
											info.setAppIconUrl(infoObj
													.getString("icon"));
											appInfos.add(info);
										}
									}
									mAppInfos.clear();
									mAppInfos.addAll(appInfos);
									lvContent.setVisibility(View.VISIBLE);
									if (mAppInfos.size() == PAGE_SIZE) {
										lvContent.setPullLoadEnable(true);
									} else {
										lvContent.setPullLoadEnable(false);
									}
									mAdapter.notifyDataSetChanged();
								} else {
									ToastUtils.toastShow(
											SubjectDetailActivity.this, msg);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						@Override
						public void onErrorResponse(VolleyError error) {
							onLoad();
							// TODO Auto-generated method stub
							// ToastUtils.toastShow(
							// SubjectDetailActivity.this,
							// error.getLocalizedMessage());
						}

					});
		} else {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("page", page + "");
			params.put("pagesize", PAGE_SIZE + "");
			params.put("topicid", subjectId + "");
			VolleyUtils.requestString(this, ApiUrl.GAME_LIST_BY_SUBJECT,
					params, this, new VolleyCallback<String>() {

						@Override
						public void onResponse(String response) {
							onLoad();
							List<DownloadInfo> appInfos = new ArrayList<DownloadInfo>();
							try {
								AppLog.redLog("jjjjj___subject_info___",
										response);
								JSONObject responseJson = new JSONObject(
										response);
								int status = responseJson.getInt("status");
								String msg = responseJson.getString("message");
								if (status == 200) {

									JSONArray subjectArray = responseJson
											.getJSONArray("data");

									if (subjectArray != null
											&& subjectArray.length() > 0) {
										int len = subjectArray.length();
										for (int i = 0; i < len; i++) {
											JSONObject infoObj = subjectArray
													.getJSONObject(i);
											DownloadInfo info = new DownloadInfo();
											info.setAppId(infoObj.getLong("id"));
											info.setApkPackageName(infoObj
													.getString("apk_package_name"));
											info.setVersionCode(infoObj
													.getInt("apk_version_code"));
											info.setVersionName(infoObj
													.getString("apk_version_name"));
											info.setAppName(infoObj
													.getString("name_zh"));
											info.setApkMark(infoObj
													.getInt("game_rank"));
											// info.setAppDes(infoObj
											// .getString("synopsis"));
											info.setTypeName(JsonUtils
													.getJSONString(infoObj,
															"typename"));
											info.setAppSize(infoObj
													.getString("apk_size"));
											info.setApkDownloadUrl(infoObj
													.getString("apk_url"));
											info.setAppIconUrl(infoObj
													.getString("icon"));
											appInfos.add(info);
										}
									}
									mAppInfos.addAll(appInfos);
								} else {
									ToastUtils.toastShow(
											SubjectDetailActivity.this, msg);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (appInfos.size() >= PAGE_SIZE) {
								lvContent.setPullLoadEnable(true);
							} else {
								lvContent.setPullLoadEnable(false);
							}
							if (page > 0 && appInfos.size() == 0) {
								page--;
							}
							mAdapter.notifyDataSetChanged();
						}

						@Override
						public void onErrorResponse(VolleyError error) {
							// ToastUtils.toastShow(
							// SubjectDetailActivity.this,
							// error.getLocalizedMessage());
							onLoad();
							if (page > 0) {
								page--;
							}
						}

					});
		}

	}

	// 下拉刷新
	@Override
	public void onRefresh() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				isLoading = false;
				page = 1;
				getNetData();
			}
		}, 2000);
	}

	// 上拉加载更多
	@Override
	public void onLoadMore() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				isLoading = false;
				page++;
				getNetData();
			}
		}, 2000);
	}

	private void onLoad() {
		lvContent.stopRefresh();
		lvContent.stopLoadMore();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Date refreshDate = new Date();
		// lvContent.setRefreshTime(sdf.format(refreshDate));
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
		// 友盟计数统计
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("subject_id", subjectId + "");
		map.put("subject_name", getIntent().getStringExtra(SUBJECT_TITLE));
		MobclickAgent.onEvent(this, UmengEvent.CLICK_SUBJECT, map);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiveBroadCast);
	}
}
