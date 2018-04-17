package com.xtone.game87873.section.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.contants.UserActions;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.manager.PreferenceManager;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.DateUtil;
import com.xtone.game87873.general.utils.JsonUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.xlistview.IXListViewListener;
import com.xtone.game87873.general.widget.xlistview.XListView;
import com.xtone.game87873.section.StartUtils;
import com.xtone.game87873.section.entity.GameGiftEntity;
import com.xtone.game87873.section.gift.GiftPrefectureFragment;

/**
 * 游戏详情页-礼包
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2016-1-12 上午10:44:15
 */
@EFragment(R.layout.fragment_list)
public class AppDetailGiftFragment extends BaseFragment implements
		IXListViewListener {

	private boolean hasGetData = false;
	private long mGameId;
	private List<GameGiftEntity> mGiftList;
	private Handler mHandler;
	private GiftListAdapter mAdapter;
	private RefreshReceiver receiver;
	@ViewById
	XListView lvContent;
	@ViewById
	ImageView ivNodate;
	@ViewById
	TextView tvMsgNodata;
	@ViewById
	View noData;

	@Override
	public void initView() {
		if (!hasGetData) {
			getNetData(true);
		}
	}

	public void setGameId(long id) {
		mGameId = id;
	}

	@AfterViews
	void afterViews() {
		receiver = new RefreshReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GiftPrefectureFragment.ACTION_LOGOUT_OR_LOGIN);
		filter.addAction(GiftPrefectureFragment.ACTION_GIFT_DETAIL);
		getActivity().registerReceiver(receiver, filter);

		mGiftList = new ArrayList<GameGiftEntity>();
		mHandler = new Handler();
		mAdapter = new GiftListAdapter();
		lvContent.setAdapter(mAdapter);
		lvContent.setXListViewListener(this);
		lvContent.setPullRefreshEnable(true);
		lvContent.setPullLoadEnable(false);
		lvContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position > 0) {
					GameGiftEntity gift = mGiftList.get(position - 1);
					StartUtils.startGiftDetail(getActivity(), gift);
				}
			}
		});

		GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				try {
					float y = e2.getY() - e1.getY();

					if (y > 5) {
						getActivity()
								.sendBroadcast(
										new Intent(
												UserActions.ACTION_APP_DETAIL_TOP_SHOW));
					} else if (y < -5) {
						getActivity()
								.sendBroadcast(
										new Intent(
												UserActions.ACTION_APP_DETAIL_TOP_HIDE));
					}
				} catch (Exception e) {

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
	}

	private void getNetData(boolean isLoading) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("gameid", mGameId + "");
		PreferenceManager pm = new PreferenceManager(getActivity());
		if (pm.getUserId() != -1) {
			params.put("uid", pm.getUserId() + "");
		}
		VolleyUtils.requestToString(getActivity(), ApiUrl.GIFT_LIST_BY_GAME,
				params, this, isLoading, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						hasGetData = true;
						AppLog.redLog("++++___gamedetail_giftlist___", response);
						List<GameGiftEntity> newGiftList = new ArrayList<GameGiftEntity>();
						try {
							JSONObject responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								JSONArray jsonArr = responseJson
										.getJSONArray("data");
								if (jsonArr != null && jsonArr.length() > 0) {
									int len = jsonArr.length();
									for (int i = 0; i < len; i++) {
										JSONObject obj = jsonArr
												.getJSONObject(i);
										GameGiftEntity gift = new GameGiftEntity();
										gift.setId(JsonUtils.getJSONLong(obj,
												"id"));
										gift.setName(JsonUtils.getJSONString(
												obj, "name"));
										gift.setContent(JsonUtils
												.getJSONString(obj, "content"));
										gift.setStatus(JsonUtils.getJSONInt(
												obj, "status"));
										gift.setIs_receive(JsonUtils
												.getJSONInt(obj, "is_receive"));
										gift.setSurplus_num(JsonUtils
												.getJSONInt(obj, "surplus_num"));
										gift.setEnd_time(JsonUtils
												.getJSONString(obj, "end_time"));
										newGiftList.add(gift);
									}
									mGiftList.clear();
									mGiftList.addAll(newGiftList);
									mAdapter.notifyDataSetChanged();
								} else {
									showNoData();
								}

							} else {
								showNoData();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						onLoad();
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						onLoad();
					}

				});

	}

	private void showNoData() {
		noData.setVisibility(View.VISIBLE);
		lvContent.setVisibility(View.GONE);
		tvMsgNodata.setText(R.string.no_gift);
		ivNodate.setImageResource(R.drawable.ic_gameinfo_gift_empty);
	}

	// 下拉刷新
	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				getNetData(false);
			}
		}, 1000);
	}

	// 上拉加载更多
	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				onLoad();
			}
		}, 1000);
	}

	private void onLoad() {
		lvContent.stopRefresh();
		lvContent.stopLoadMore();
	}

	class RefreshReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(
					GiftPrefectureFragment.ACTION_LOGOUT_OR_LOGIN)) {
				getNetData(true);
			} else if (intent.getAction().equals(
					GiftPrefectureFragment.ACTION_GIFT_DETAIL)) {
				int size = mGiftList.size();
				long id = intent.getLongExtra("id", -1);
				if (id != -1) {
					for (int i = 0; i < size; i++) {
						if (id == mGiftList.get(i).getId()) {
							mGiftList.get(i).setIs_receive(
									GameGiftEntity.IS_RECEIVE_YES);
							mAdapter.notifyDataSetChanged();
							break;
						}
					}
				}
			}
		}
	}

	class GiftListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mGiftList.size();
		}

		@Override
		public Object getItem(int position) {
			return mGiftList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup arg2) {
			LayoutInflater infalter = LayoutInflater.from(getActivity());
			ViewHolder holder;
			if (view == null) {
				view = infalter.inflate(R.layout.item_appdetail_gift, null);
				holder = new ViewHolder();
				holder.tvGiftName = (TextView) view
						.findViewById(R.id.tvGiftName);
				holder.tvGiftContent = (TextView) view
						.findViewById(R.id.tvGiftContent);
				holder.tv_status = (TextView) view.findViewById(R.id.tv_status);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			GameGiftEntity gift = mGiftList.get(position);
			holder.tvGiftContent.setText(gift.getContent().replace("\n", " "));
			holder.tvGiftName.setText(gift.getName());
			switch (gift.getStatus()) {
			case ApiUrl.GIFT_STATUS_TAKE_NO:
				holder.tv_status.setText(R.string.take);
				if (gift.getSurplus_num() == 0) {
					holder.tv_status.setText(R.string.take_out);
				}
				break;
			case ApiUrl.GIFT_STATUS_FOR_NO:
				holder.tv_status.setText(R.string.for_num);
				break;
			case ApiUrl.GIFT_STATUS_FINISH:
				holder.tv_status.setText(R.string.finish);
				break;
			case ApiUrl.GIFT_STATUS_ORDER:

				break;
			}
			if (gift.getIs_receive() == 1) {
				holder.tv_status.setText(R.string.already_take_2);
			}
			long residueTime = DateUtil.strToTime(gift.getEnd_time())
					- System.currentTimeMillis();
			if (residueTime <= 0) {
				holder.tv_status.setText(R.string.finish);
			}
			holder.tv_status.setTag(gift);
			holder.tv_status.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					GameGiftEntity temp = (GameGiftEntity) v.getTag();
					StartUtils.startGiftDetail(getActivity(), temp);
				}
			});
			return view;
		}

		class ViewHolder {
			TextView tvGiftName, tvGiftContent, tv_status;
		}

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (receiver != null) {
			getActivity().unregisterReceiver(receiver);
		}
	}
}
