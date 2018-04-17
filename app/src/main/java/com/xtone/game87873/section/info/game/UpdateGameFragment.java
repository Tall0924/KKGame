package com.xtone.game87873.section.info.game;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.download.DownloadInfo;
import com.xtone.game87873.general.download.DownloadManager;
import com.xtone.game87873.general.download.DownloadService;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.AppUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.xlistview.IXListViewListener;
import com.xtone.game87873.general.widget.xlistview.XListView;
import com.xtone.game87873.section.HomeActivity;
import com.xtone.game87873.section.StartUtils;
import com.xtone.game87873.section.game.GameFragment;
import com.xtone.game87873.section.game.adapter.GameListAdapter;

/**
 * 可更新
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-6 下午4:20:41
 */
@EFragment(R.layout.fragment_update_game)
public class UpdateGameFragment extends BaseFragment implements
		IXListViewListener {

	private LayoutInflater mInflater;
	private Handler mHandler;
	private GameListAdapter mAdapter;
	private List<DownloadInfo> mAppInfos;
	private DownloadManager downloadManager;
	private List<DownloadInfo> updateList;
	@ViewById
	XListView lvContent;
	@ViewById
	View noData;
	@ViewById
	ImageView ivNodate;
	@ViewById
	Button btn_tohome;
	@ViewById
	TextView tvMsgNodata;

	@Override
	public void initView() {
		getData();
	}

	@Click(R.id.btn_tohome)
	void toHome() {
		HomeActivity.mInstance.setViewShow(0);
		GameFragment.mInstance.setCurrentItem(0);
		StartUtils.startHomeActivity(getActivity());
	}

	@AfterViews
	void afterViews() {
		mInflater = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mAppInfos = new ArrayList<DownloadInfo>();
		mHandler = new Handler();
		mAdapter = new GameListAdapter(getActivity(), mAppInfos);
		mAdapter.setType(GameListAdapter.TYPE_UPDATE);
		lvContent.setAdapter(mAdapter);
		lvContent.setXListViewListener(this);
		lvContent.setPullRefreshEnable(false);
		lvContent.setPullLoadEnable(false);
		lvContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				DownloadInfo game = mAppInfos.get(position - 1);
				StartUtils.startAppDetail(getActivity(), game.getAppName(),
						game.getAppId());
			}
		});
		btn_tohome.setText("返回首页");
		btn_tohome.setVisibility(View.VISIBLE);
		// getData();
	}

	private void getData() {
		downloadManager = DownloadService.getDownloadManager(getActivity());
		updateList = downloadManager.getUpdateAppList();
		mAppInfos.clear();
		mAppInfos.addAll(updateList);
		mAdapter.notifyDataSetChanged();
		if (mAppInfos.size() == 0) {
			noData.setVisibility(View.VISIBLE);
			ivNodate.setImageResource(R.drawable.icon_update_null);
			tvMsgNodata.setText("不错！都是最新版！");
		} else {
			noData.setVisibility(View.GONE);
		}
	}

	private void getaData() {
		List<DownloadInfo> appInfos = AppUtils
				.getInstalledAppInfo(getActivity());
		JSONArray appArr = new JSONArray();
		try {
			for (int i = 0; i < appInfos.size(); i++) {
				JSONObject appJson = new JSONObject();
				DownloadInfo app = appInfos.get(i);
				appJson.put("apk_package_name", app.getApkPackageName());
				appJson.put("apk_version_code", app.getVersionCode());
				appArr.put(appJson);
			}
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("checklist", appArr.toString());
			VolleyUtils.requestStringWithLoading(getActivity(),
					ApiUrl.UPDATE_LIST, params, this,
					new VolleyCallback<String>() {

						@Override
						public void onResponse(String response) {
							try {
								AppLog.redLog("jjjjj___rank___", response);
								JSONObject responseJson = new JSONObject(
										response);

								int status = responseJson.getInt("status");
								String msg = responseJson.getString("message");
								if (status == 200) {
									List<DownloadInfo> appInfos = new ArrayList<DownloadInfo>();
									JSONArray bannerArray = responseJson
											.getJSONArray("data");
									if (bannerArray != null
											&& bannerArray.length() > 0) {
										int len = bannerArray.length();
										for (int i = 0; i < len; i++) {
											JSONObject infoObj = bannerArray
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
											// info.setApkMark(infoObj
											// .getInt("game_rank"));
											// info.setAppDes(infoObj
											// .getString("synopsis"));
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
									mAdapter.notifyDataSetChanged();
								} else {
									ToastUtils.toastShow(getActivity(), msg);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						@Override
						public void onErrorResponse(VolleyError error) {
							// TODO Auto-generated method stub
							// ToastUtils.toastShow(getActivity(),
							// error.getLocalizedMessage());
						}

					});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	// 下拉刷新
	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				getData();
				onLoad();
			}
		}, 2000);
	}

	// 上拉加载更多
	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				onLoad();
			}
		}, 2000);
	}

	private void onLoad() {
		lvContent.stopRefresh();
		lvContent.stopLoadMore();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date refreshDate = new Date();
		lvContent.setRefreshTime(sdf.format(refreshDate));
	}
}
