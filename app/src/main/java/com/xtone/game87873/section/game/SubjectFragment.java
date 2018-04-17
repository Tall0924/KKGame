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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.contants.PicSize;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.CommonUtils;
import com.xtone.game87873.general.utils.DensityUtil;
import com.xtone.game87873.general.utils.JsonUtils;
import com.xtone.game87873.general.utils.NetworkUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.xlistview.IXListViewListener;
import com.xtone.game87873.general.widget.xlistview.XListView;
import com.xtone.game87873.section.entity.SubjectEntity;

/**
 * 首页专题页
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-3 下午3:09:05
 */
@EFragment(R.layout.fragment_list)
public class SubjectFragment extends BaseFragment implements IXListViewListener {

	private LayoutInflater mInflater;
	private Handler mHandler;
	private MyAdapter mAdapter;
	private List<SubjectEntity> mSubjects;
	private int page = 1;
	private final int PAGE_SIZE = 4;
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
		if (mSubjects != null && mSubjects.size() == 0) {
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
			mInflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			mHandler = new Handler();
			mSubjects = new ArrayList<SubjectEntity>();
			width = CommonUtils.getScreenWidth(getActivity())
					- DensityUtil.dip2px(getActivity(), 10) * 2;
			height = CommonUtils.getHeightWithSize(getActivity(),
					PicSize.SUBJECT_WIDTH, PicSize.SUBJECT_HEIGHT, width);
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
					Intent intent = new Intent(getActivity(),
							SubjectDetailActivity_.class);
					intent.putExtra(SubjectDetailActivity.SUBJECT_ID, mSubjects
							.get(position - 1).getId());
					intent.putExtra(SubjectDetailActivity.SUBJECT_TITLE,
							mSubjects.get(position - 1).getTitle());
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
		VolleyUtils.requestToString(getActivity(), ApiUrl.SUBJECT_LIST, params,
				this, isLoading, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						onLoad();
						AppLog.redLog("subjectlist____", response);
						if (page == 1) {
							mSubjects.clear();
						}
						List<SubjectEntity> subjects = new ArrayList<SubjectEntity>();
						try {
							JSONObject responseJson = new JSONObject(response);

							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {

								JSONArray subjectArray = responseJson
										.getJSONArray("data");
								if (subjectArray != null
										&& subjectArray.length() > 0) {
									int len = subjectArray.length();
									for (int i = 0; i < len; i++) {
										JSONObject subjectObj = subjectArray
												.getJSONObject(i);
										SubjectEntity subject = new SubjectEntity();
										subject.setId(JsonUtils.getJSONLong(
												subjectObj, "id"));
										subject.setTitle(JsonUtils
												.getJSONString(subjectObj,
														"title"));
										subject.setPublishTime(JsonUtils
												.getJSONString(subjectObj,
														"publish_time"));
										subject.setThumb(JsonUtils
												.getJSONString(subjectObj,
														"banner"));
										subject.setContent(JsonUtils
												.getJSONString(subjectObj,
														"content"));
										subjects.add(subject);
									}
								}
								mSubjects.addAll(subjects);
							} else {
								ToastUtils.toastShow(getActivity(), msg);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (subjects.size() >= PAGE_SIZE) {
							lvContent.setPullLoadEnable(true);
						} else {
							lvContent.setPullLoadEnable(false);
						}
						if (page > 0 && subjects.size() == 0) {
							page--;
						}
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						onLoad();
						// TODO Auto-generated method stub
						if (page > 0) {
							page--;
						}
					}

				});
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mSubjects.size();
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
			SubjectViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_subject, null);
				holder = new SubjectViewHolder();
				holder.tvSubjectName = (TextView) convertView
						.findViewById(R.id.tvSubjectName);
				holder.tvTime = (TextView) convertView
						.findViewById(R.id.tvTime);
				holder.ivSubject = (ImageView) convertView
						.findViewById(R.id.ivSubject);
				convertView.setTag(holder);
			} else {
				holder = (SubjectViewHolder) convertView.getTag();
			}
			SubjectEntity subject = mSubjects.get(position);
			holder.tvSubjectName.setText(subject.getTitle());
			holder.tvTime.setText(subject.getPublishTime());
			holder.ivSubject.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, height));

			VolleyUtils.setURLImage(getActivity(), holder.ivSubject,
					subject.getThumb(), R.drawable.pic_banner_loading,
					R.drawable.pic_banner_loading, width, height);
			return convertView;
		}
	}

	class SubjectViewHolder {
		TextView tvSubjectName;
		TextView tvTime;
		ImageView ivSubject;

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
