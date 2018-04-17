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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.manager.PreferenceManager;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.CommonUtils;
import com.xtone.game87873.general.utils.NetworkUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.xlistview.IXListViewListener;
import com.xtone.game87873.general.widget.xlistview.XListView;
import com.xtone.game87873.section.HomeActivity;
import com.xtone.game87873.section.StartUtils;
import com.xtone.game87873.section.entity.Information;
import com.xtone.game87873.section.game.GameFragment;

/**
 * 收藏列表 资讯
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-9-9 下午4:20:41
 */
@EFragment(R.layout.fragment_collect_list)
public class CollectInformationFragment extends BaseFragment implements
		IXListViewListener {
	private List<Information> mInformations;
	private LayoutInflater mInflater;
	private Handler mHandler;
	private MyAdapter mAdapter;
	private int page = 1;
	private final int PAGE_SIZE = 12;
	private boolean isRefresh;
	public static final int RESULT_CODE_COLLECT = 2;
	public static final int RESULT_CODE_NO_COLLECT = 3;
	private List<Long> choosedIds;
	private boolean isChooseAll;
	private int width;
	private int height;
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
			for (Information info : mInformations) {
				choosedIds.add(info.getId());
			}
			mAdapter.notifyDataSetChanged();
		}
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
		params.put("type", "3");// 1游戏，2专题
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
								List<Information> infos = new ArrayList<Information>();
								for (Information info : mInformations) {
									if (choosedIds.contains(info.getId())) {
										infos.add(info);
									}
								}
								for (Information info : infos) {
									mInformations.remove(info);
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

	@Override
	public void initView() {
		if (mInformations != null && mInformations.size() == 0) {
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

	@Click(R.id.btn_refresh)
	void refreshClick() {
		if (isRefresh) {
			reLoadData();
		} else {
			HomeActivity.mInstance.setViewShow(0);
			GameFragment.mInstance.setCurrentItem(4);
			StartUtils.startHomeActivity(getActivity());
		}
	}

	private void getNetData() {

		HashMap<String, String> params = new HashMap<String, String>();
		// if (type == TYPE_GAME) {
		params.put("type", "3");
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
							mInformations.clear();
						}
						List<Information> infos = new ArrayList<Information>();
						try {
							AppLog.redLog("collect_list_info_____", response);
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
										Information info = new Information();
										info.setId(infoObj.getLong("id"));
										info.setTitle(infoObj
												.getString("title"));
										info.setPublishTime(infoObj
												.getString("publish_time"));
										info.setThumbnail(infoObj
												.getString("thumbnail"));
										infos.add(info);
									}
								}
								mInformations.addAll(infos);

							} else {
								ToastUtils.toastShow(getActivity(), msg);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (infos.size() >= PAGE_SIZE) {
							lvContent.setPullLoadEnable(true);
						} else {
							lvContent.setPullLoadEnable(false);
						}
						if (page > 0 && infos.size() == 0) {
							page--;
						}
						mAdapter.notifyDataSetChanged();
						if (mInformations.size() <= 0) {
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
		mInformations = new ArrayList<Information>();
		choosedIds = new ArrayList<Long>();
		mHandler = new Handler();
		width = CommonUtils.getScreenWidth(getActivity());
		height = CommonUtils.getFitHeight(getActivity(),
				R.drawable.pic_index_slide);
		mAdapter = new MyAdapter();
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
						Information info = mInformations.get(position - 1);
						StartUtils.startInformationDetail(getActivity(),
								info.getId(), info.getTitle());
					} else {
						long id = mInformations.get(position - 1).getId();
						if (choosedIds.contains(id)) {
							choosedIds.remove(id);
						} else {
							choosedIds.add(id);
						}
						mAdapter.notifyDataSetChanged();
						if (choosedIds.size() < mInformations.size()) {
							isChooseAll = false;
							ivSelectAll
									.setImageResource(R.drawable.cb_my_gift_unselected);
						}
					}
				}
			}
		});
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mInformations.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			InformationViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.item_collect_information, null);
				holder = new InformationViewHolder();
				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.tvTitle);
				holder.ivSelect = (ImageView) convertView
						.findViewById(R.id.ivSelect);
				holder.tvTime = (TextView) convertView
						.findViewById(R.id.tvTime);
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.ivIcon);
				convertView.setTag(holder);
			} else {
				holder = (InformationViewHolder) convertView.getTag();
			}
			Information info = mInformations.get(position);
			holder.tvTitle.setText(info.getTitle());
			holder.tvTime.setText(info.getPublishTime());
			VolleyUtils.setURLImage(getActivity(), holder.ivIcon,
					info.getThumbnail(),
					R.drawable.pic_collect_subject_loading,
					R.drawable.pic_collect_subject_loading, width, height);
			if (MyCollectionActivity.isEdit) {
				holder.ivSelect.setVisibility(View.VISIBLE);
				if (choosedIds != null && choosedIds.contains(info.getId())) {
					holder.ivSelect
							.setImageResource(R.drawable.cb_my_gift_selected);
				} else {
					holder.ivSelect
							.setImageResource(R.drawable.cb_my_gift_unselected);
				}
			} else {
				holder.ivSelect.setVisibility(View.GONE);
			}

			return convertView;
		}
	}

	class InformationViewHolder {
		TextView tvTitle;
		TextView tvTime;
		ImageView ivSelect;
		ImageView ivIcon;

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CODE_NO_COLLECT) {
			mInformations.remove(requestCode);
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
