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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.CommonUtils;
import com.xtone.game87873.general.utils.JsonUtils;
import com.xtone.game87873.general.utils.NetworkUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.xlistview.IXListViewListener;
import com.xtone.game87873.general.widget.xlistview.XListView;
import com.xtone.game87873.section.entity.Information;

/**
 * 资讯
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-9-8 下午2:37:12
 */
@EFragment(R.layout.fragment_list)
public class InformationFragment extends BaseFragment implements IXListViewListener {

	private LayoutInflater mInflater;
	private Handler mHandler;
	private MyAdapter mAdapter;
	private List<Information> mInformations;
	private int page = 1;
	private final int PAGE_SIZE = 12;
	private int width;
	private int height;
	@ViewById
	XListView lvContent;
	@ViewById
	LinearLayout il_loadFailure;
	@ViewById
	Button btn_refresh;

	private boolean isLoading = true;// 是否显示加载框

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
	}

	@AfterViews
	void afterViews() {
		if (mAdapter == null) {
			mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mHandler = new Handler();
			mInformations = new ArrayList<>();
			width = CommonUtils.getScreenWidth(getActivity());
			height = CommonUtils.getFitHeight(getActivity(),R.drawable.pic_index_slide);
			mAdapter = new MyAdapter();
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
					Intent intent = new Intent(getActivity(),InformationDetailWebViewActivity_.class);
					intent.putExtra(InformationDetailWebViewActivity.INFO_ID,mInformations.get(position - 1).getId());
					intent.putExtra(InformationDetailWebViewActivity.INFO_TITLE,mInformations.get(position - 1).getTitle());
					startActivity(intent);
				}

			}
		});
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
		page = 1;
		getNetData();
	}

	@Click(R.id.btn_refresh)
	void refreshClick() {
		reLoadData();
	}

	private void getNetData() {

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("client", ApiUrl.SOURCE_APP + "");
		params.put("page", page + "");
		params.put("pagesize", PAGE_SIZE + "");
		VolleyUtils.requestToString(getActivity(), ApiUrl.INFORMATION_LIST,
				params, this, isLoading, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						AppLog.redLog("-----info----", response);
						onLoad();
						if (page == 1) {
							mInformations.clear();
						}
						List<Information> infos = new ArrayList<Information>();
						try {
							JSONObject responseJson = new JSONObject(response);

							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {

								JSONArray infoArray = responseJson
										.getJSONArray("data");
								if (infoArray != null && infoArray.length() > 0) {
									int len = infoArray.length();
									for (int i = 0; i < len; i++) {
										JSONObject infoObj = infoArray
												.getJSONObject(i);
										Information info = new Information();
										info.setId(JsonUtils.getJSONLong(
												infoObj, "id"));
										info.setTitle(JsonUtils.getJSONString(
												infoObj, "title"));
										info.setQuotes(JsonUtils.getJSONString(
												infoObj, "quotes"));
										info.setThumbnail(JsonUtils
												.getJSONString(infoObj,
														"thumbnail"));
										info.setPublishTime(JsonUtils
												.getJSONString(infoObj,
														"publish_time"));
										infos.add(info);
									}
									mInformations.addAll(infos);
								}

							} else {
								ToastUtils.toastShow(getActivity(), msg);
							}

							if (infos.size() >= PAGE_SIZE) {
								lvContent.setPullLoadEnable(true);
							} else {
								lvContent.setPullLoadEnable(false);
							}
							if (page > 0 && infos.size() == 0) {
								page--;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							page--;
						}

						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						onLoad();
						if (page > 0) {
							page--;
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
				convertView = mInflater
						.inflate(R.layout.item_information, null);
				holder = new InformationViewHolder();
				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.tvTitle);
				holder.tvDetail = (TextView) convertView
						.findViewById(R.id.tvDetail);
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
			holder.tvDetail.setText(info.getQuotes());
			String time = info.getPublishTime();
			if (time.contains(" ")) {
				time = time.substring(0, time.indexOf(" "));
			}
			holder.tvTime.setText(time);
			VolleyUtils.setURLImage(getActivity(), holder.ivIcon,
					info.getThumbnail(),
					R.drawable.pic_collect_subject_loading,
					R.drawable.pic_zixun_fail, width, height);
			return convertView;
		}
	}

	class InformationViewHolder {
		TextView tvTitle;
		TextView tvTime;
		TextView tvDetail;
		ImageView ivIcon;

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

	@SuppressLint("SimpleDateFormat")
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
