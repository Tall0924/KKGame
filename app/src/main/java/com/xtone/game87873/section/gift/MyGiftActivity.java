package com.xtone.game87873.section.gift;

import java.lang.ref.WeakReference;
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
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.base.SwipeBackActivity;
import com.xtone.game87873.general.manager.PreferenceManager;
import com.xtone.game87873.general.utils.BitmapHelper;
import com.xtone.game87873.general.utils.ClipboardUtil;
import com.xtone.game87873.general.utils.ImageLoaderUtils;
import com.xtone.game87873.general.utils.NetworkUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.xlistview.IXListViewListener;
import com.xtone.game87873.general.widget.xlistview.XListView;
import com.xtone.game87873.section.HomeActivity;
import com.xtone.game87873.section.StartUtils;
import com.xtone.game87873.section.entity.GameGiftEntity;

@EActivity(R.layout.activity_my_gift)
public class MyGiftActivity extends SwipeBackActivity implements IXListViewListener {

	private boolean isEdit = false;
	private List<GameGiftEntity> giftList;
	private List<GameGiftEntity> newGiftList;
	private HashMap<String, String> params;
	private int currentPage = 1;
	private MyGiftListAdapter mAdapter;
	private PreferenceManager pm;
	private long uid;
	private int nodataType = 0;// 0没网络，1没数据
	// 删除
	private boolean booAll;
	private boolean canLoadMore = false;
	private int iAll;

	@ViewById
	XListView lv_myGift;
	@ViewById
	ImageView iv_headLeft, iv_headRight, ivMsg, iv_all;
	@ViewById
	TextView tv_headTitle, tvMsg, tv_Msg2;
	@ViewById
	LinearLayout ll_operate, ll_content, il_loadFailure;
	@ViewById
	Button btn_delete, btn_refresh;
	private PopupWindow pop;
	private View popView;
	private PopHandler pHandler;

	@SuppressWarnings("deprecation")
	@AfterViews
	void afterViews() {
		tv_headTitle.setText(R.string.mine_gift);
		iv_headRight.setVisibility(View.VISIBLE);
		popView = LayoutInflater.from(this).inflate(
				R.layout.layout_pop_gift_copy, null);
		pop = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// 需要设置一下此参数，点击外边可消失
		pop.setBackgroundDrawable(new BitmapDrawable());
		// 设置点击窗口外边窗口消失
		pop.setOutsideTouchable(true);
		// // 设置此参数获得焦点，否则无法点击
		// pop.setFocusable(true);
		pHandler = new PopHandler(this);

		iv_headRight.setImageResource(R.drawable.btn_head_edit);
		params = new HashMap<String, String>();
		giftList = new ArrayList<GameGiftEntity>();
		newGiftList = new ArrayList<GameGiftEntity>();
		mAdapter = new MyGiftListAdapter();
		pm = new PreferenceManager(this);
		uid = pm.getUserId();
		lv_myGift.setAdapter(mAdapter);
		lv_myGift.setXListViewListener(this);
		lv_myGift.setPullRefreshEnable(true);
		lv_myGift.setPullLoadEnable(canLoadMore);
		lv_myGift.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (isEdit) {
					select(position - 1);
				}
			}
		});
		if (uid != -1) {
			loadGiftList(true);
		}
	}

	// 没有网络
	private void noNetwork() {
		nodataType = 0;
		lv_myGift.setVisibility(View.GONE);
		il_loadFailure.setVisibility(View.VISIBLE);
		ivMsg.setImageResource(R.drawable.icon_signal);
		tvMsg.setText("加载失败");
		tvMsg.setVisibility(View.VISIBLE);
		tv_Msg2.setVisibility(View.VISIBLE);
		btn_refresh.setText("重新加载");
	}

	// 没有数据
	private void noData() {
		nodataType = 1;
		lv_myGift.setVisibility(View.GONE);
		il_loadFailure.setVisibility(View.VISIBLE);
		ivMsg.setImageResource(R.drawable.icon_user_default_login);
		tvMsg.setText("您还没领取礼包");
		tv_Msg2.setVisibility(View.GONE);
		btn_refresh.setText("去逛逛");
	}

	private void reLoadData() {
		uid = pm.getUserId();
		lv_myGift.setVisibility(View.VISIBLE);
		il_loadFailure.setVisibility(View.GONE);
		currentPage = 1;
		loadGiftList(true);
	}

	// 加载礼包列表
	private void loadGiftList(boolean isLoading) {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			noNetwork();
			giftList.clear();
			return;
		}

		params.clear();
		params.put("uid", uid + "");
		params.put("page", currentPage + "");
		params.put("pagesize", ApiUrl.PAGE_SIZE + "");
		newGiftList.clear();
		VolleyUtils.requestToString(this, ApiUrl.GET_MY_LIBAO, params, this,
				isLoading, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						// System.out.println("--------my Libao-----response-------------\n response="
						// + response.toString());
						if (currentPage == 1) {
							giftList.clear();
						}
						onLoad();
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								if (!responseJson.has("data")) {
									noData();
									return;
								}
								JSONArray jsonArr = responseJson
										.getJSONArray("data");
								if (jsonArr != null && jsonArr.length() > 0) {
									int len = jsonArr.length();
									for (int i = 0; i < len; i++) {
										JSONObject obj = jsonArr
												.getJSONObject(i);
										newGiftList.add(new GameGiftEntity(obj
												.getLong("id"), obj
												.getString("name"), obj
												.getString("icon"), obj
												.getString("code"), obj
												.getInt("code_num"), obj
												.getInt("surplus_num")));
										// System.out.println("-----------gift--------------\n gift="
										// + newGiftList.get(i));
									}
								}
								giftList.addAll(newGiftList);
								if (newGiftList.size() >= ApiUrl.PAGE_SIZE) {
									currentPage++;
									canLoadMore = true;
									lv_myGift.setPullLoadEnable(canLoadMore);
								} else {
									canLoadMore = false;
									lv_myGift.setPullLoadEnable(canLoadMore);
								}
								if (giftList.size() <= 0) {
									noData();
								}
								mAdapter.notifyDataSetChanged();
							} else {
								ToastUtils.toastShow(MyGiftActivity.this, msg);
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

	@Click(R.id.btn_refresh)
	void refreshClick() {
		if (nodataType == 0) {
			reLoadData();
		} else {
			HomeActivity.mInstance.setViewShow(1);
			StartUtils.startHomeActivity(this);
		}
	}

	@Click(R.id.iv_headLeft)
	void backClick() {
		back();
	}

	private void back() {
		if (isEdit) {
			isEdit = false;
			iv_headRight.setImageResource(R.drawable.btn_head_edit);
			ll_operate.setVisibility(View.GONE);
		} else {
			finish();
		}
	}

	@Click(R.id.iv_headRight)
	void editOrFinishClik() {
		if (giftList.size() <= 0) {
			ToastUtils.toastShow(this, "没有可删除的数据！");
			return;
		}
		if (isEdit) {
			isEdit = false;
			iv_headRight.setImageResource(R.drawable.btn_head_edit);
			ll_operate.setVisibility(View.GONE);
			lv_myGift.setPullRefreshEnable(true);
			lv_myGift.setPullLoadEnable(canLoadMore);
		} else {
			isEdit = true;
			iv_headRight.setImageResource(R.drawable.btn_head_finish);
			ll_operate.setVisibility(View.VISIBLE);
			lv_myGift.setPullRefreshEnable(false);
			lv_myGift.setPullLoadEnable(canLoadMore);
		}
	}

	@Click(R.id.iv_all)
	void allClick() {
		int size = giftList.size();
		if (booAll) {
			for (int i = 0; i < size; i++) {
				giftList.get(i).setDelete(false);
			}
			iAll = 0;
			booAll = false;
			iv_all.setBackgroundResource(R.drawable.cb_my_gift_unselected);
		} else {
			for (int i = 0; i < size; i++) {
				giftList.get(i).setDelete(true);
			}
			iAll = size;
			booAll = true;
			iv_all.setBackgroundResource(R.drawable.cb_my_gift_selected);
		}
		mAdapter.notifyDataSetChanged();
	}

	@Click(R.id.btn_delete)
	void deleteClick() {
		params.clear();
		params.put("uid", uid + "");
		StringBuffer strB = new StringBuffer();
		if (iAll > 0) {
			int size = giftList.size();
			for (int i = 0; i < size; i++) {
				GameGiftEntity temp = giftList.get(i);
				if (temp.isDelete()) {
					strB.append(temp.getId() + ",");
				}
			}
		} else {
			ToastUtils.toastShow(this, "请选择要删除的礼包");
			return;
		}
		params.put("libao", strB.substring(0, strB.length() - 1).toString());
		deleteGift();
	}

	// 删除礼包
	private void deleteGift() {
		VolleyUtils.requestStringWithLoading(this, ApiUrl.DELETE_MY_LIBAO,
				params, this, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						// System.out.println("--------del Libao-----response-------------\n response="
						// + response.toString());
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								ll_operate.setVisibility(View.GONE);
								isEdit = false;
								iAll = 0;
								iv_headRight
										.setImageResource(R.drawable.btn_head_edit);
								ll_operate.setVisibility(View.GONE);
								lv_myGift.setPullRefreshEnable(true);
								lv_myGift.setPullLoadEnable(canLoadMore);
								currentPage = 1;
								loadGiftList(true);
							} else {
								ToastUtils.toastShow(MyGiftActivity.this, msg);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// System.out.println("------------del libao--------------\n error="
						// + error.getMessage());
					}
				});
	}

	@Override
	public void onBackPressed() {
		back();
	}

	private class MyGiftListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return giftList.size();
		}

		@Override
		public Object getItem(int position) {
			return giftList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup arg2) {
			LayoutInflater infalter = LayoutInflater.from(MyGiftActivity.this);
			ViewHolder holder;
			if (view == null) {
				view = infalter.inflate(
						R.layout.list_item_my_gift_with_checkbox, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_giftName = (TextView) view
						.findViewById(R.id.tv_giftName);
				holder.tv_giftKey = (TextView) view
						.findViewById(R.id.tv_giftKey);
				holder.tv_copy = (TextView) view.findViewById(R.id.tv_copy);
				holder.iv_select = (ImageView) view
						.findViewById(R.id.iv_select);
				holder.tv_alreadyTakeCount = (TextView) view
						.findViewById(R.id.tv_alreadyTakeCount);
				holder.tv_residueCount = (TextView) view
						.findViewById(R.id.tv_residueCount);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			GameGiftEntity gift = giftList.get(position);
			// VolleyUtils.setURLImage(MyGiftActivity.this, holder.iv_icon,
			// gift.getIcon(), R.drawable.icon_game_loading,
			// R.drawable.icon_game_loading);
			ImageLoaderUtils.loadImgWithConner(holder.iv_icon, gift.getIcon(),
					R.drawable.icon_game_loading, R.drawable.icon_game_loading);
			holder.tv_copy.setTag(position);
			holder.iv_select.setTag(position);
			holder.tv_giftKey.setText(gift.getCode() + "");
			holder.tv_giftName.setText(gift.getName());
			holder.tv_residueCount.setText(gift.getSurplus_num() + "");
			holder.tv_alreadyTakeCount.setText((gift.getCode_num() - gift
					.getSurplus_num()) + "");
			holder.tv_copy.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int index = (Integer) v.getTag();
					String code = giftList.get(index).getCode();
					ClipboardUtil.setClipboardText(MyGiftActivity.this, code);
					// 显示窗口 设置弹出效果
					pop.showAsDropDown(v,
							-BitmapHelper.dip2px(MyGiftActivity.this, 60),
							BitmapHelper.dip2px(MyGiftActivity.this, 8));
					pHandler.sendEmptyMessageDelayed(0, 2000);
				}
			});
			holder.iv_select.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int index = (Integer) v.getTag();
					select(index);
				}
			});
			if (isEdit) {
				holder.iv_select.setVisibility(View.VISIBLE);
				holder.tv_copy.setVisibility(View.GONE);
				if (gift.isDelete()) {
					holder.iv_select
							.setImageResource(R.drawable.cb_my_gift_selected);
				} else {
					holder.iv_select
							.setImageResource(R.drawable.cb_my_gift_unselected);
				}
			} else {
				holder.iv_select.setVisibility(View.GONE);
				holder.tv_copy.setVisibility(View.VISIBLE);
			}
			return view;
		}

		class ViewHolder {
			ImageView iv_icon, iv_select;
			TextView tv_giftName, tv_giftKey, tv_residueCount,
					tv_alreadyTakeCount;
			TextView tv_copy;
		}

	}

	private void select(int index) {
		GameGiftEntity temp = giftList.get(index);
		if (temp.isDelete()) {
			temp.setDelete(false);
			iAll--;
		} else {
			temp.setDelete(true);
			iAll++;
		}
		if (iAll == giftList.size()) {
			booAll = true;
			iv_all.setBackgroundResource(R.drawable.cb_my_gift_selected);
		} else {
			booAll = false;
			iv_all.setBackgroundResource(R.drawable.cb_my_gift_unselected);
		}
		mAdapter.notifyDataSetChanged();
	}

	static class PopHandler extends Handler {
		WeakReference<MyGiftActivity> mActivity;

		public PopHandler(MyGiftActivity mActivity) {
			super();
			this.mActivity = new WeakReference<MyGiftActivity>(mActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			MyGiftActivity activity = mActivity.get();
			if (activity.pop.isShowing()) {
				// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
				activity.pop.dismiss();
			}
		}
	}

	// 下拉刷新
	@Override
	public void onRefresh() {
		currentPage = 1;
		loadGiftList(false);
	}

	// 上拉加载更多
	@Override
	public void onLoadMore() {
		loadGiftList(false);
	}

	@SuppressLint("SimpleDateFormat")
	private void onLoad() {
		lv_myGift.stopRefresh();
		lv_myGift.stopLoadMore();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date refreshDate = new Date();
		lv_myGift.setRefreshTime(sdf.format(refreshDate));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (pHandler.hasMessages(0)) {
			pHandler.removeMessages(0);
		}
	}
}
