package com.xtone.game87873.section.gift;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.base.SwipeBackActivity;
import com.xtone.game87873.general.manager.PreferenceManager;
import com.xtone.game87873.general.utils.NetworkUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.xlistview.IXListViewListener;
import com.xtone.game87873.general.widget.xlistview.XListView;
import com.xtone.game87873.section.entity.GameGiftEntity;
import com.xtone.game87873.section.gift.adapter.GiftListAdapter;

@EActivity(R.layout.activity_gift_search_result)
public class GiftSearchResultActivity extends SwipeBackActivity implements
		OnClickListener, IXListViewListener {

	@ViewById
	ImageView iv_headLeft;
	@ViewById
	TextView tv_headTitle;
	@ViewById
	XListView lv_giftList;
	private HashMap<String, String> params;
	private List<GameGiftEntity> giftList;
	private List<GameGiftEntity> newGiftList;
	private GiftListAdapter listAdapter;
	private int curPage = 1;
	private String strSearch;
	private RefreshReceiver receiver;
	private PreferenceManager pm;

	@SuppressWarnings("unchecked")
	@AfterViews
	void afterViews() {
		tv_headTitle.setText("搜索结果");
		pm = new PreferenceManager(this);
		params = new HashMap<String, String>();
		newGiftList = new ArrayList<GameGiftEntity>();
		giftList = (List<GameGiftEntity>) getIntent().getExtras().getSerializable("gift");
		strSearch = getIntent().getExtras().getString("search");
		listAdapter = new GiftListAdapter(this, giftList);
		lv_giftList.setAdapter(listAdapter);
		lv_giftList.setXListViewListener(this);
		lv_giftList.setPullRefreshEnable(false);
		lv_giftList.setPullLoadEnable(false);
		lv_giftList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
				if (position > 0) {
					if (!NetworkUtils.isNetworkAvailable(GiftSearchResultActivity.this)) {
						ToastUtils.toastShow(GiftSearchResultActivity.this,"网络不可用！");
						return;
					}
					GameGiftEntity gift = giftList.get(position - 1);
					Intent intent = new Intent(GiftSearchResultActivity.this,GiftDetailActivity_.class);
					intent.putExtra("giftId", giftList.get(position - 1).getId());
					if (gift.getStatus() == ApiUrl.GIFT_STATUS_FOR_NO) {
						intent.putExtra("forNo", gift.getAlreadyForNo());
					}
					startActivity(intent);
				}
			}
		});
		if (giftList.size() >= ApiUrl.PAGE_SIZE) {
			curPage++;
			lv_giftList.setPullLoadEnable(true);
		} else {
			lv_giftList.setPullLoadEnable(false);
		}
		receiver = new RefreshReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GiftPrefectureFragment.ACTION_GIFT_DETAIL);
		registerReceiver(receiver, filter);
	}

	@Click(R.id.iv_headLeft)
	void backClick() {
		finish();
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {

	}

	// 加载礼包列表
	private void loadGiftList() {
		if (curPage == 1) {
			giftList.clear();
		}
		params.clear();
		params.put("page", curPage + "");
		params.put("pagesize", ApiUrl.PAGE_SIZE + "");
		params.put("wd", strSearch);
		if (pm.getUserId() != -1) {
			params.put("uid", pm.getUserId() + "");
		}
		newGiftList.clear();
		VolleyUtils.requestString(this, ApiUrl.LIBAO_SEARCH, params, this,new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						// System.out.println("-------------response-------------\n response="+ response.toString());
						onLoad();
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								JSONArray jsonArr = responseJson.getJSONArray("data");
								if (jsonArr != null && jsonArr.length() > 0) {
									int len = jsonArr.length();
									for (int i = 0; i < len; i++) {
										JSONObject obj = jsonArr.getJSONObject(i);
										GameGiftEntity newGift = new GameGiftEntity(
												obj.getLong("id"),
												obj.getString("name"),
												obj.getString("content"),
												obj.getInt("code_num"),
												obj.getInt("status"),
												obj.getString("icon"),
												obj.getInt("surplus_num"),
												obj.getString("end_time"));
										if (obj.has("isReceive")) {
											newGift.setIs_receive(obj.getInt("isReceive"));
										}
										newGiftList.add(newGift);
									}
								}
								giftList.addAll(newGiftList);
								if (newGiftList.size() >= ApiUrl.PAGE_SIZE) {
									curPage++;
									lv_giftList.setPullLoadEnable(true);
								} else {
									lv_giftList.setPullLoadEnable(false);
								}
								listAdapter.notifyDataSetChanged();
							} else {
								ToastUtils.toastShow(GiftSearchResultActivity.this, msg);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// System.out.println("------------error--------------\n error="
						// + error.getMessage());
					}
				});
	}

	// 下拉刷新
	@Override
	public void onRefresh() {
		curPage = 1;
		loadGiftList();
	}

	// 上拉加载更多
	@Override
	public void onLoadMore() {
		loadGiftList();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@SuppressLint("SimpleDateFormat")
	private void onLoad() {
		lv_giftList.stopRefresh();
		lv_giftList.stopLoadMore();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date refreshDate = new Date();
		lv_giftList.setRefreshTime(sdf.format(refreshDate));
	}

	class RefreshReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(GiftPrefectureFragment.ACTION_GIFT_DETAIL)) {
				int size = giftList.size();
				long id = intent.getLongExtra("id", -1);
				if (id != -1) {
					for (int i = 0; i < size; i++) {
						if (id == giftList.get(i).getId()) {
							giftList.get(i).setIs_receive(GameGiftEntity.IS_RECEIVE_YES);
							listAdapter.notifyDataSetChanged();
							break;
						}
					}
				}
			}
		}
	}
}
