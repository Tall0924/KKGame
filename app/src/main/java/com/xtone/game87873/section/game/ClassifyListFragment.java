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
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.contants.UserActions;
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
 * 分类下的游戏列表
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-6 下午4:20:41
 */
@EFragment(R.layout.fragment_list)
public class ClassifyListFragment extends BaseFragment implements
		IXListViewListener {
	private List<DownloadInfo> mAppInfos;
	private int type;
	public static final int TYPE_RECOMMEND = 1;
	public static final int TYPE_HOT = 2;
	public static final int TYPE_NEW = 3;
	private LayoutInflater mInflater;
	private Handler mHandler;
	private GameListAdapter mAdapter;
	public boolean isRefresh = true;
	private int page = 1;
	private final int PAGE_SIZE = 12;
	@ViewById
	XListView lvContent;
	@ViewById
	LinearLayout il_loadFailure, llOuter;
	@ViewById
	Button btn_refresh;
	@ViewById
	ImageView ivMsg;
	@ViewById
	TextView tvMsg, tv_Msg2;

	private boolean isLoading = true;

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public void initView() {
		if (mAppInfos != null && (isRefresh)) {
			if (!NetworkUtils.isNetworkAvailable(getActivity())) {
				noNetwork();
				return;
			}
			isLoading = true;
			page = 1;
			getNetData();
		}
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	// 没有数据
	private void noData() {
		lvContent.setVisibility(View.GONE);
		il_loadFailure.setVisibility(View.VISIBLE);
		ivMsg.setImageResource(R.drawable.no_data);
		tvMsg.setVisibility(View.GONE);
		tv_Msg2.setVisibility(View.GONE);
		btn_refresh.setText("重新加载");
	}

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
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			return;
		}
		lvContent.setVisibility(View.VISIBLE);
		il_loadFailure.setVisibility(View.GONE);
		isLoading = true;
		page = 1;
		getNetData();
	}

	@Click(R.id.btn_refresh)
	void refreshClick() {
		reLoadData();
	}

	private void getNetData() {

		String url;
		isRefresh = false;

		HashMap<String, String> params = new HashMap<String, String>();
		if (type == TYPE_HOT) {
			params.put("order", "1");
		} else if (type == TYPE_NEW) {
			params.put("order", "2");
		}
		params.put("client", "2");// 2代表app端,1pc
		params.put("page", page + "");
		params.put("pagesize", PAGE_SIZE + "");
		if (ClassifyDetailActivity.tagId == 0) {
			params.put("tagid", ClassifyDetailActivity.typeId + "");
			url = ApiUrl.GAME_LIST_BY_TAG;
		} else {
			params.put("tagid", ClassifyDetailActivity.tagId + "");
			url = ApiUrl.GAME_LIST_BY_TAG;
		}
		VolleyUtils.requestToString(getActivity(), url, params, this,
				isLoading, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						onLoad();
						AppLog.redLog("classify_detail_list______", response);
						if (page == 1) {
							mAppInfos.clear();
						}
						List<DownloadInfo> appInfos = new ArrayList<DownloadInfo>();
						try {
							JSONObject responseJson = new JSONObject(response);

							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");

							if (status == 200) {

								JSONArray bannerArray = responseJson
										.getJSONArray("data");
								if (bannerArray != null
										&& bannerArray.length() > 0) {
									int len = bannerArray.length();
									for (int i = 0; i < len; i++) {
										JSONObject infoObj = bannerArray
												.getJSONObject(i);
										DownloadInfo info = new DownloadInfo();
										info.setAppId(JsonUtils.getJSONLong(infoObj,"id"));
										info.setApkPackageName(JsonUtils.getJSONString(infoObj,"apk_package_name"));
										info.setVersionCode(JsonUtils.getJSONInt(infoObj,"apk_version_code"));
										info.setVersionName(JsonUtils.getJSONString(infoObj,"apk_version_name"));
										info.setAppName(JsonUtils.getJSONString(infoObj,"name_zh"));
										info.setApkMark(JsonUtils.getJSONInt(infoObj,"game_rank"));
										info.setTypeName(JsonUtils
												.getJSONString(infoObj,
														"typename"));
										// info.setAppDes(infoObj
										// .getString("synopsis"));
										info.setAppSize(JsonUtils.getJSONString(infoObj,"apk_size"));
										info.setApkDownloadUrl(JsonUtils.getJSONString(infoObj,"apk_url"));
										info.setAppIconUrl(JsonUtils.getJSONString(infoObj,"icon"));
										appInfos.add(info);
									}
								}
								mAppInfos.addAll(appInfos);

							} else {
								ToastUtils.toastShow(getActivity(), msg);
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
						if (mAppInfos.size() <= 0) {
							noData();
						} else {
							lvContent.setVisibility(View.VISIBLE);
							il_loadFailure.setVisibility(View.GONE);
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						// ToastUtils.toastShow(getActivity(),
						// error.getLocalizedMessage());
						onLoad();
						if (page > 0) {
							page--;
						}
					}

				});
	}

	@AfterViews
	void afterViews() {
		llOuter.setBackgroundColor(getResources().getColor(R.color.white));
		mInflater = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mAppInfos = new ArrayList<DownloadInfo>();
		mHandler = new Handler();
		mAdapter = new GameListAdapter(getActivity(), mAppInfos);
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
		if (type == TYPE_HOT) {
			initView();
		}
		GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if (((ClassifyDetailActivity) getActivity()).isTagShow()) {
					float y = e2.getY() - e1.getY();

					if (y > 5) {
						getActivity()
								.sendBroadcast(
										new Intent(
												UserActions.ACTION_CLASSIFY_DETAIL_TAG_SHOW));
					} else if (y < -5) {
						getActivity()
								.sendBroadcast(
										new Intent(
												UserActions.ACTION_CLASSIFY_DETAIL_TAG_HIDE));
					}
				}
				return false;
			}
		};
		final GestureDetector gestureDetector = new GestureDetector(
				getActivity(), onGestureListener);
		OnTouchListener touchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
		lvContent.setOnTouchListener(touchListener);
		// il_loadFailure.setOnTouchListener(touchListener);
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
				page = 1;
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
	public void onDestroyView() {
		super.onDestroyView();
	}
}
