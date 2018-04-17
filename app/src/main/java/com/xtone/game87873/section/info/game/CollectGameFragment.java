package com.xtone.game87873.section.info.game;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.download.DownloadInfo;
import com.xtone.game87873.general.manager.PreferenceManager;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.JsonUtils;
import com.xtone.game87873.general.utils.NetworkUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.xlistview.IXListViewListener;
import com.xtone.game87873.general.widget.xlistview.XListView;
import com.xtone.game87873.section.HomeActivity;
import com.xtone.game87873.section.StartUtils;
import com.xtone.game87873.section.game.GameFragment;
import com.xtone.game87873.section.game.adapter.GameListAdapter;
import com.xtone.game87873.section.info.adapter.CollectGameListAdapter;

/**
 * 收藏列表
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-6 下午4:20:41
 */
@EFragment(R.layout.fragment_collect_list)
public class CollectGameFragment extends BaseFragment implements
		IXListViewListener {
	private List<DownloadInfo> mAppInfos;
	private LayoutInflater mInflater;
	private Handler mHandler;
	private CollectGameListAdapter mAdapter;
	private int page = 1;
	private final int PAGE_SIZE = 12;
	private boolean isRefresh;
	public static final int RESULT_CODE_COLLECT = 2;
	public static final int RESULT_CODE_NO_COLLECT = 3;
	private List<Long> choosedIds;
	private boolean isChooseAll;
	@ViewById
	XListView lvContent;
	@ViewById
	LinearLayout il_loadFailure;
	@ViewById
	Button btn_refresh;
	@ViewById
	ImageView ivMsg, ivSelectAll;
	@ViewById
	TextView tvMsg, tv_Msg2, tvDel;
	@ViewById
	RelativeLayout rlChooseAll;
	@ViewById
	LinearLayout llChooseAll;

	private boolean isLoading = true;

	@Click(R.id.llChooseAll)
	void chooseAll() {
		if (isChooseAll) {
			isChooseAll = false;
			ivSelectAll.setImageResource(R.drawable.cb_my_gift_unselected);
			choosedIds.clear();
			mAdapter.notifyDataSetChanged();
		} else {
			isChooseAll = true;
			ivSelectAll.setImageResource(R.drawable.cb_my_gift_selected);
			choosedIds.clear();
			for (DownloadInfo info : mAppInfos) {
				choosedIds.add(info.getAppId());
			}
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void initView() {
		if (mAppInfos != null && mAppInfos.size() == 0) {
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
		if (MyCollectionActivity.isEdit) {
			rlChooseAll.setVisibility(View.VISIBLE);
			if (isChooseAll) {
				ivSelectAll.setImageResource(R.drawable.cb_my_gift_selected);
			} else {
				ivSelectAll.setImageResource(R.drawable.cb_my_gift_unselected);
			}
		} else {
			rlChooseAll.setVisibility(View.GONE);
		}
	}

	// 没有数据
	private void noData() {
		lvContent.setVisibility(View.GONE);
		il_loadFailure.setVisibility(View.VISIBLE);
		ivMsg.setImageResource(R.drawable.icon_user_default_login);
		tvMsg.setVisibility(View.VISIBLE);
		tvMsg.setText(R.string.no_collect);
		tv_Msg2.setVisibility(View.GONE);
		btn_refresh.setText(R.string.to_see);
		isRefresh = false;
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
		isRefresh = true;
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

	@Click(R.id.tvDel)
	void delCollect() {
		if (choosedIds.size() == 0) {
			return;
		}
		long userId = new PreferenceManager(getActivity()).getUserId();
		String idStr = "";
		for (long id : choosedIds) {
			idStr += id + ",";
		}
		idStr = idStr.substring(0, idStr.length() - 1);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("type", "1");// 1游戏，2专题
		params.put("uid", userId + "");
		params.put("targetid", idStr);
		VolleyUtils.requestString(getActivity(), ApiUrl.DEL_COLLECTION, params,
				this, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						try {
							AppLog.redLog("jjjjj___rank___", response);
							JSONObject responseJson = new JSONObject(response);

							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								List<DownloadInfo> infos = new ArrayList<DownloadInfo>();
								for (DownloadInfo info : mAppInfos) {
									if (choosedIds.contains(info.getAppId())) {
										infos.add(info);
									}
								}
								for (DownloadInfo info : infos) {
									mAppInfos.remove(info);
								}
								mAdapter.notifyDataSetChanged();
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

	@Click(R.id.btn_refresh)
	void refreshClick() {
		if (isRefresh) {
			reLoadData();
		} else {
			HomeActivity.mInstance.setViewShow(0);
			GameFragment.mInstance.setCurrentItem(0);
			StartUtils.startHomeActivity(getActivity());
		}
	}

	private void getNetData() {

		HashMap<String, String> params = new HashMap<String, String>();
		// if (type == TYPE_GAME) {
		params.put("type", "1");
		// } else if (type == TYPE_SUBJECT) {
		// params.put("type", "2");
		// }
		params.put("page", page + "");
		params.put("pagesize", PAGE_SIZE + "");
		long userId = new PreferenceManager(getActivity()).getUserId();
		if (userId != -1) {
			params.put("uid", userId + "");
		}
		VolleyUtils.requestToString(getActivity(), ApiUrl.COLLECTION_LIST,
				params, this, isLoading, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						onLoad();
						if (page == 1) {
							mAppInfos.clear();
						}
						List<DownloadInfo> appInfos = new ArrayList<DownloadInfo>();
						try {
							AppLog.redLog("jjjjj___classify_list___", response);
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
										info.setAppDes(infoObj
												.getString("synopsis"));
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
		mInflater = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mAppInfos = new ArrayList<DownloadInfo>();
		choosedIds = new ArrayList<Long>();
		mHandler = new Handler();
		mAdapter = new CollectGameListAdapter(getActivity(), mAppInfos,
				choosedIds);
		mAdapter.setType(GameListAdapter.TYPE_COLLECT);
		lvContent.setAdapter(mAdapter);
		lvContent.setXListViewListener(this);
		lvContent.setPullRefreshEnable(true);
		lvContent.setPullLoadEnable(false);
		lvContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position > 0) {
					if (!MyCollectionActivity.isEdit) {
						DownloadInfo game = mAppInfos.get(position - 1);
						StartUtils.startAppDetailForResult(
								CollectGameFragment.this, game.getAppName(),
								game.getAppId(), position - 1);
					} else {
						long id = mAppInfos.get(position - 1).getAppId();
						if (choosedIds.contains(id)) {
							choosedIds.remove(id);
						} else {
							choosedIds.add(id);
						}
						mAdapter.notifyDataSetChanged();
						if (choosedIds.size() < mAppInfos.size()) {
							isChooseAll = false;
							ivSelectAll
									.setImageResource(R.drawable.cb_my_gift_unselected);
						}
					}
				}
			}
		});
		initView();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CODE_NO_COLLECT) {
			mAppInfos.remove(requestCode);
			initView();
		}
		super.onActivityResult(requestCode, resultCode, data);
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

	public void clearChoosed() {
		if (choosedIds != null) {
			choosedIds.clear();
		}
		isChooseAll = false;
	}
}
