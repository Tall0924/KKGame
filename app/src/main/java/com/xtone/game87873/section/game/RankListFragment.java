package com.xtone.game87873.section.game;

import java.util.ArrayList;
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
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.download.DownloadInfo;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.JsonUtils;
import com.xtone.game87873.general.utils.NetworkUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.xlistview.IXListViewListener;
import com.xtone.game87873.general.widget.xlistview.XListView;
import com.xtone.game87873.section.game.adapter.GameListAdapter;

/**
 * 排行列表
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-6 下午4:20:41
 */
@EFragment(R.layout.fragment_list)
public class RankListFragment extends BaseFragment implements
		IXListViewListener {
	private List<DownloadInfo> mAppInfos;
	private int type;
	public static final int TYPE_DANJI = 1;
	public static final int TYPE_WANGYOU = 2;
	private LayoutInflater mInflater;
	private Handler mHandler;
	private GameListAdapter mAdapter;
	@ViewById
	XListView lvContent;
	@ViewById
	LinearLayout il_loadFailure;
	@ViewById
	Button btn_refresh;

	private boolean isLoading = true;// 是否显示加载框

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public void initView() {
		if (mAppInfos != null && mAppInfos.size() == 0) {
			if (!NetworkUtils.isNetworkAvailable(getActivity())) {
				noNetwork();
				return;
			}
			isLoading = true;
			getNetData();
		}
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	// 没有网络
	private void noNetwork() {
		lvContent.setVisibility(View.GONE);
		il_loadFailure.setVisibility(View.VISIBLE);
	}

	private void reLoadData() {
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			return;
		}
		lvContent.setVisibility(View.VISIBLE);
		il_loadFailure.setVisibility(View.GONE);
		isLoading = true;
		getNetData();
	}

	@Click(R.id.btn_refresh)
	void refreshClick() {
		reLoadData();
	}

	private void getNetData() {

		HashMap<String, String> params = new HashMap<String, String>();
		if (type == TYPE_DANJI) {
			params.put("type", "2");
		} else if (type == TYPE_WANGYOU) {
			params.put("type", "1");
		}
		VolleyUtils.requestToString(getActivity(), ApiUrl.RANK_LIST, params,
				this, isLoading, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						onLoad();
						try {
							AppLog.redLog("jjjjj___rank___", response);
							JSONObject responseJson = new JSONObject(response);

							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								List<DownloadInfo> appInfos = new ArrayList<DownloadInfo>();
								JSONArray bannerArray = JsonUtils.getJSONArray(
										responseJson, "data");
								if (bannerArray != null
										&& bannerArray.length() > 0) {
									int len = bannerArray.length();
									for (int i = 0; i < len; i++) {
										JSONObject infoObj = bannerArray
												.getJSONObject(i);
										DownloadInfo info = new DownloadInfo();
										info.setAppId(JsonUtils.getJSONLong(
												infoObj, "id"));
										info.setApkPackageName(JsonUtils
												.getJSONString(infoObj,
														"apk_package_name"));
										info.setVersionCode(JsonUtils
												.getJSONInt(infoObj,
														"apk_version_code"));
										info.setVersionName(JsonUtils
												.getJSONString(infoObj,
														"apk_version_name"));
										info.setAppName(JsonUtils
												.getJSONString(infoObj,
														"name_zh"));
										info.setApkMark(JsonUtils.getJSONInt(
												infoObj, "game_rank"));
										info.setAppDes(JsonUtils.getJSONString(
												infoObj, "synopsis"));
										info.setTypeName(JsonUtils
												.getJSONString(infoObj,
														"typename"));
										info.setAppSize(JsonUtils
												.getJSONString(infoObj,
														"apk_size"));
										info.setApkDownloadUrl(JsonUtils
												.getJSONString(infoObj,
														"apk_url"));
										info.setAppIconUrl(JsonUtils
												.getJSONString(infoObj, "icon"));
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
						onLoad();
						// TODO Auto-generated method stub
						// ToastUtils.toastShow(getActivity(),
						// error.getLocalizedMessage());
					}

				});
	}

	@AfterViews
	void afterViews() {
		if (mAdapter == null) {
			mInflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			mAppInfos = new ArrayList<DownloadInfo>();
			mHandler = new Handler();
			mAdapter = new GameListAdapter(getActivity(), mAppInfos);
			mAdapter.setType(GameListAdapter.TYPE_RANDOM);
		}
		lvContent.setAdapter(mAdapter);
		lvContent.setXListViewListener(this);
		lvContent.setPullRefreshEnable(true);
		lvContent.setPullLoadEnable(false);
		lvContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position > 0) {
					Intent intent = new Intent(getActivity(),
							AppDetailActivity_.class);
					intent.putExtra(AppDetailActivity.GAME_NAME,
							mAppInfos.get(position - 1).getAppName());
					intent.putExtra(AppDetailActivity.GAME_ID,
							mAppInfos.get(position - 1).getAppId());
					startActivity(intent);
				}
			}
		});
	}

	// 下拉刷新
	@Override
	public void onRefresh() {
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			noNetwork();
			onLoad();
			return;
		}
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				isLoading = false;
				getNetData();
			}
		}, 2000);
	}

	// 上拉加载更多
	@Override
	public void onLoadMore() {
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			noNetwork();
			onLoad();
			return;
		}
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
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Date refreshDate = new Date();
		// lvContent.setRefreshTime(sdf.format(refreshDate));
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

}
