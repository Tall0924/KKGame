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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.JsonUtils;
import com.xtone.game87873.general.utils.NetworkUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.xlistview.IXListViewListener;
import com.xtone.game87873.general.widget.xlistview.XListView;
import com.xtone.game87873.section.entity.TypeEntity;
import com.xtone.game87873.section.entity.TypeTagEntity;

/**
 * 首页分类页
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-3 下午3:09:05
 */
@EFragment(R.layout.fragment_list)
public class ClassifyFragment extends BaseFragment implements
		IXListViewListener {
	private LayoutInflater mInflater;
	private GridView gvTop;
	private Handler mHandler;
	private TopGridViewAdapter mTopAdapter;
	private MyAdapter mAdapter;
	private List<TypeEntity> mTypes;
	private List<TypeEntity> mTopTypes;
	private int iconwWidth;
	private View headView;
	@ViewById
	XListView lvContent;
	@ViewById
	LinearLayout il_loadFailure;
	@ViewById
	Button btn_refresh;

	private boolean isLoading = true;

	@Override
	public void initView() {

		if (mTypes != null && mTypes.size() == 0) {
			if (!NetworkUtils.isNetworkAvailable(getActivity())) {
				noNetwork();
				return;
			}
			isLoading = true;
			getNetData();
		}
	}

	@AfterViews
	void afterViews() {
		if (mAdapter == null) {
			mTypes = new ArrayList<TypeEntity>();
			mTopTypes = new ArrayList<TypeEntity>();
			mInflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			mHandler = new Handler();
			mAdapter = new MyAdapter();
		}
		addHeadView();
		lvContent.setAdapter(mAdapter);
		lvContent.setXListViewListener(this);
		lvContent.setPullRefreshEnable(true);
		lvContent.setPullLoadEnable(false);

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
		params.put("client", ApiUrl.SOURCE_APP + "");// 2为app，1pc
		VolleyUtils.requestToString(getActivity(), ApiUrl.TYPE_LIST, params,
				this, isLoading, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						onLoad();
						try {
							AppLog.redLog("typelist____", response);
							JSONObject responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								List<TypeEntity> types = new ArrayList<TypeEntity>();
								List<TypeEntity> topTypes = new ArrayList<TypeEntity>();
								JSONObject dataObj = responseJson
										.getJSONObject("data");
								JSONArray featureTypeArray = dataObj
										.getJSONArray("feature_tag");
								if (featureTypeArray != null
										&& featureTypeArray.length() > 0) {
									int len = featureTypeArray.length();
									for (int i = 0; i < len; i++) {
										JSONObject typeObj = featureTypeArray
												.getJSONObject(i);
										TypeEntity type = new TypeEntity();
										type.setId(typeObj.getLong("tag_id"));
										type.setImgUrl(typeObj
												.getString("icon"));
										type.setName(typeObj.getString("name"));
										topTypes.add(type);

									}
								}
								JSONArray generalTypeArray = dataObj
										.getJSONArray("general_tag");
								if (generalTypeArray != null
										&& generalTypeArray.length() > 0) {
									int len = generalTypeArray.length();
									for (int i = 0; i < len; i++) {
										JSONObject typeObj = generalTypeArray
												.getJSONObject(i);
										TypeEntity type = new TypeEntity();
										type.setId(typeObj.getLong("tag_id"));
										type.setName(typeObj.getString("name"));
										type.setImgUrl(typeObj
												.getString("icon"));

										List<TypeTagEntity> tags = new ArrayList<TypeTagEntity>();
										JSONArray typeTagArray = JsonUtils
												.getJSONArray(typeObj, "tags");
										if (typeTagArray != null
												&& typeTagArray.length() > 0) {
											int tagLen = typeTagArray.length();
											for (int j = 0; j < tagLen; j++) {
												JSONObject tagObj = typeTagArray
														.getJSONObject(j);
												TypeTagEntity tag = new TypeTagEntity();
												tag.setId(tagObj.getLong("id"));
												tag.setName(tagObj
														.getString("name"));
												tags.add(tag);
											}
										}
										type.setTags(tags);
										types.add(type);

									}
								}
								mTypes.clear();
								mTypes.addAll(types);
								mAdapter.notifyDataSetChanged();
								mTopTypes.clear();
								mTopTypes.addAll(topTypes);
								mTopAdapter.notifyDataSetChanged();
								if (mTopTypes.size() > 0) {
									headView.setVisibility(View.VISIBLE);
								} else {
									headView.setVisibility(View.GONE);
								}
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
						onLoad();
					}

				});

	}

	private void addHeadView() {
		headView = mInflater.inflate(R.layout.fragment_classify_top, null);
		headView.setVisibility(View.GONE);
		gvTop = (GridView) headView.findViewById(R.id.gvTop);
		mTopAdapter = new TopGridViewAdapter();
		gvTop.setAdapter(mTopAdapter);
		gvTop.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						ClassifyDetailActivity_.class);
				intent.putExtra(ClassifyDetailActivity.TAG_ID,
						mTopTypes.get(position).getId());
				intent.putExtra(ClassifyDetailActivity.TAG_NAME,
						mTopTypes.get(position).getName());
				startActivity(intent);
			}
		});
		lvContent.addHeaderView(headView);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_tag_loaging);
		iconwWidth = bitmap.getWidth();
	}

	/**
	 * 顶部列表列表适配器
	 */
	private class TopGridViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mTopTypes.size();
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
			TypeItemViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_type, null);
				holder = new TypeItemViewHolder();
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				convertView.setTag(holder);
			} else {
				holder = (TypeItemViewHolder) convertView.getTag();
			}
			if (position < mTopTypes.size()) {
				TypeEntity type = mTopTypes.get(position);
				holder.tvName.setText(type.getName());
				LayoutParams params = new LayoutParams(iconwWidth, iconwWidth);
				params.addRule(RelativeLayout.CENTER_HORIZONTAL);
				holder.ivIcon.setLayoutParams(params);
				VolleyUtils.setURLImage(getActivity(), holder.ivIcon,
						type.getImgUrl(), R.drawable.icon_tag_loaging,
						R.drawable.icon_tag_loaging, iconwWidth, iconwWidth);
			}
			return convertView;
		}
	}

	/**
	 * 类别适配器
	 */
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mTypes.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TypeViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_classify, null);
				holder = new TypeViewHolder();
				holder.rl_type = (RelativeLayout) convertView
						.findViewById(R.id.rl_type);
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.iv_icon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.gvContent = (GridView) convertView
						.findViewById(R.id.gvContent);
				convertView.setTag(holder);
			} else {
				holder = (TypeViewHolder) convertView.getTag();
			}
			final TypeEntity type = mTypes.get(position);
			holder.tv_name.setText(type.getName());
			LayoutParams params = new LayoutParams(iconwWidth, iconwWidth);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			holder.iv_icon.setLayoutParams(params);
			VolleyUtils.setURLImage(getActivity(), holder.iv_icon,
					type.getImgUrl(), R.drawable.icon_tag_loaging,
					R.drawable.icon_tag_loaging, iconwWidth, iconwWidth);
			holder.gvContent.setAdapter(new MyGridViewAdapter(type.getTags()));
			holder.gvContent.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					if (position < type.getTags().size()) {
						Intent intent = new Intent(getActivity(),
								ClassifyDetailActivity_.class);
						intent.putExtra(ClassifyDetailActivity.TYPE_ID,
								type.getId());
						intent.putExtra(ClassifyDetailActivity.TYPE_NAME,
								type.getName());
						intent.putExtra(ClassifyDetailActivity.TAG_NAME, type
								.getTags().get(position).getName());
						intent.putExtra(ClassifyDetailActivity.TAG_ID, type
								.getTags().get(position).getId());
						startActivity(intent);
					}
				}
			});
			holder.rl_type.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getActivity(),
							ClassifyDetailActivity_.class);
					intent.putExtra(ClassifyDetailActivity.TYPE_ID,
							type.getId());
					intent.putExtra(ClassifyDetailActivity.TYPE_NAME,
							type.getName());
					intent.putExtra(ClassifyDetailActivity.TAG_ID, 0L);
					startActivity(intent);

				}
			});
			return convertView;
		}

	}

	class TypeItemViewHolder {
		TextView tvName;
		ImageView ivIcon;
	}

	class TypeViewHolder {
		RelativeLayout rl_type;
		ImageView iv_icon;
		TextView tv_name;
		GridView gvContent;
	}

	private class MyGridViewAdapter extends BaseAdapter {

		private List<TypeTagEntity> mTags;

		MyGridViewAdapter(List<TypeTagEntity> tags) {
			mTags = tags;
		}

		@Override
		public int getCount() {
			return 6;
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
			TypeTagViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_classify_text,
						null);
				holder = new TypeTagViewHolder();
				holder.tvTypeTag = (TextView) convertView
						.findViewById(R.id.tvTypeTag);
				convertView.setTag(holder);
			} else {
				holder = (TypeTagViewHolder) convertView.getTag();
			}
			if (position < mTags.size()) {
				TypeTagEntity tag = mTags.get(position);
				holder.tvTypeTag.setText(tag.getName());
			} else {
				holder.tvTypeTag.setText("");
			}
			return convertView;
		}
	}

	class TypeTagViewHolder {
		TextView tvTypeTag;
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
