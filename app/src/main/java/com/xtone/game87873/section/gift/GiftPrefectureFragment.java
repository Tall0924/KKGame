package com.xtone.game87873.section.gift;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.contants.PicSize;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.manager.PreferenceManager;
import com.xtone.game87873.general.utils.BitmapHelper;
import com.xtone.game87873.general.utils.CommonUtils;
import com.xtone.game87873.general.utils.NetworkUtils;
import com.xtone.game87873.general.utils.SlideUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.ObservableScrollView;
import com.xtone.game87873.general.widget.ObservableScrollView.Callbacks;
import com.xtone.game87873.section.StartUtils;
import com.xtone.game87873.section.dialog.CommonDialog;
import com.xtone.game87873.section.entity.GameGiftEntity;
import com.xtone.game87873.section.entity.HomeBanner;
import com.xtone.game87873.section.game.adapter.SlideAdapter;
import com.xtone.game87873.section.gift.adapter.GiftListAdapter;

/**
 * 礼包专区
 * 
 * @author huangzx
 * */

@EFragment(R.layout.fragment_gift_prefecture)
public class GiftPrefectureFragment extends BaseFragment implements Callbacks {
	public final static int GIFT_DETAIL_ACTIVITY = 10;
	public final static String ACTION_LOGOUT_OR_LOGIN = "com.xton.logoutOrLogin";
	public final static String ACTION_GIFT_DETAIL = "com.xton.giftDetail";
	private List<GameGiftEntity> giftList;
	private List<GameGiftEntity> newGiftList;
	private GiftListAdapter listAdapter;
	private List<ImageView> slideViews;
	private List<View> dots;
	private SlideAdapter slideAdapter;
	private List<HomeBanner> mBanners;
	private PreferenceManager pm;
	private int currentPage = 1;
	private int index = 0;
	private int currentItem;// 图片切换当前的位置
	private int oldPosition = 1;// 图片切换上一个位置的位置
	private int rlSearchWith, placeholderTop, moveAmount, oldScrollY = -1,
			move, vRight, edtRight, ivRight, ivLeft, line2Left, line2Right;// 搜索框移动相关
	private boolean isVpTouch = false, loadMore = true, isMoveMax = false,
			isMoveMini = false, isFirst = true, moveEnable = false;
	private String[] arrColor = { "#00ffffff", "#18F0F0F0", "#1FF0F0F0",
			"#28F0F0F0", "#2FF0F0F0", "#38F0F0F0", "#3FF0F0F0", "#48F0F0F0",
			"#4FF0F0F0", "#58F0F0F0", "#5FF0F0F0", "#68F0F0F0", "#6FF0F0F0",
			"#78F0F0F0", "#7FF0F0F0", "#88F0F0F0", "#8FF0F0F0", "#98F0F0F0",
			"#9FF0F0F0", "#A8F0F0F0", "#AFF0F0F0", "#B8F0F0F0", "#BFF0F0F0",
			"#C8F0F0F0", "#CFF0F0F0", "#D8F0F0F0", "#DFF0F0F0", "#E8F0F0F0",
			"#EFF0F0F0", "#F8F0F0F0", "#FFF0F0F0", "#FFF0F0F0", "#FFF0F0F0" };
	private ScheduledExecutorService scheduledExecutorService;
	private RefreshReceiver receiver;
	// 切换当前显示的图片
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (!isVpTouch && slideViews.size() > 1) {
				currentItem = (currentItem) % mBanners.size() + 1;
				vpSlide.setCurrentItem(currentItem, true);// 切换当前显示的图片
			}
		}
	};

	/**
	 * 换行切换任务
	 */
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (vpSlide) {
				if (!isVpTouch && slideViews.size() > 1) {
					handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
				}
			}
		}
	}

	@ViewById
	ObservableScrollView scroll_view;
	@ViewById
	LinearLayout llDots, ll_listHead;
	@ViewById
	View placeholder, placeholder2, v_space;
	@ViewById
	ListView lv_giftPrefecture;
	@ViewById
	LinearLayout il_loadFailure, ll_slide;
	@ViewById
	ImageView ivMsg, iv_search, iv_myGift;
	@ViewById
	TextView tvMsg, tv_Msg2;
	@ViewById
	Button btn_refresh;
	@ViewById
	EditText edt_search;
	@ViewById
	ViewPager vpSlide;
	@ViewById
	ProgressBar xlistview_footer_progressbar;
	@ViewById
	TextView xlistview_footer_hint_textview;
	@ViewById
	RelativeLayout xlistview_footer_content, rl_search;
	@ViewById
	FrameLayout fl_content;
	@ViewById
	View v_bg, line2, v_search;

	@SuppressLint("NewApi")
	@Override
	public void onScrollChanged(int scrollY) {//scrollY 变化后的Y轴位置
		if (Build.VERSION.SDK_INT >= 11 && (oldScrollY != scrollY || scrollY == 0)) {
			oldScrollY = scrollY;
			// 控件移动
			rl_search.setTranslationY(Math.max(placeholder.getTop(), scrollY));//Math.max(i1,i2) i1 > i2 ? i1 : i2
			ll_listHead.setTranslationY(Math.max(placeholder2.getTop(), scrollY+ BitmapHelper.dip2px(getActivity(), 60)));
			iv_myGift.setTranslationY(scrollY);

			if (rlSearchWith == 0) {
				rlSearchWith = v_search.getWidth();
				vRight = v_search.getRight();
				edtRight = edt_search.getRight();
				ivLeft = iv_search.getLeft();
				ivRight = iv_search.getRight();
				line2Left = line2.getLeft();
				line2Right = line2.getRight();
				placeholderTop = placeholder.getTop();
				moveAmount = placeholderTop/ BitmapHelper.dip2px(getActivity(), 40);
			}
			if (moveEnable) {
				vpSlide.setVisibility(View.VISIBLE);
				v_bg.setVisibility(View.VISIBLE);
				// 背景颜色变化
				if (scrollY > placeholder.getTop()) {
					v_bg.setBackgroundResource(R.color.home_bg);
					rl_search.setBackgroundResource(R.color.home_bg);
				} else if (scrollY > 0 && placeholder.getTop() > 0) {
					int changeColorHeight = placeholder.getTop() / 30;
					v_bg.setBackgroundColor(Color.parseColor(arrColor[scrollY/ changeColorHeight]));
					rl_search.setBackgroundResource(R.color.transparent);
				} else if (scrollY <= 0) {
					rl_search.setBackgroundResource(R.color.transparent);
					v_bg.setBackgroundResource(R.color.transparent);
				}
				// 搜索框宽度改变
				if (scrollY > 0 && rlSearchWith > 0) {
					isMoveMini = false;
					if (scrollY < placeholderTop) {
						if (move != scrollY / moveAmount) {
							move = scrollY / moveAmount;
							if (move <= BitmapHelper.dip2px(getActivity(), 40)) {
								moveView(move);
							}
						}
						isMoveMax = false;
					} else if (!isMoveMax) {
						isMoveMax = true;
						move = BitmapHelper.dip2px(getActivity(), 40);
						moveView(move);
					}
				} else if (scrollY <= 0 && !isMoveMini && rlSearchWith > 0) {
					isMoveMini = true;
					moveView(0);
				}
			} else {
				move = BitmapHelper.dip2px(getActivity(), 40);
				vpSlide.setVisibility(View.GONE);
				v_bg.setVisibility(View.GONE);
				v_bg.setBackgroundResource(R.color.home_bg);
				rl_search.setBackgroundResource(R.color.home_bg);
				moveView(move);
			}
		}
	}

	private void moveView(int mMove) {
		line2.layout(line2Left - mMove, line2.getTop(), line2Right - mMove,line2.getBottom());
		edt_search.layout(edt_search.getLeft(), edt_search.getTop(), edtRight- mMove, edt_search.getBottom());
		iv_search.layout(ivLeft - mMove, iv_search.getTop(), ivRight - mMove,iv_search.getBottom());
		v_search.layout(v_search.getLeft(), v_search.getTop(), vRight - mMove,v_search.getBottom());
	}

	@Override
	public void onDownMotionEvent() {

	}

	@Override
	public void onUpOrCancelMotionEvent() {

	}

	@AfterViews
	void afterViews() {
		pm = new PreferenceManager(getActivity());
		receiver = new RefreshReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_LOGOUT_OR_LOGIN);
		filter.addAction(ACTION_GIFT_DETAIL);
		getActivity().registerReceiver(receiver, filter);
		initBanner();
		initScrollView();
		initListView();
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			noNetwork();
			return;
		}
		loadGiftList(true);
		edt_search.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				if (KeyEvent.KEYCODE_ENTER == keyCode&& event.getAction() == KeyEvent.ACTION_DOWN) {
					searchGift();
				}
				return false;
			}
		});
		edt_search.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if (KeyEvent.ACTION_UP == event.getAction()) {
					scroll_view.postDelayed(new Runnable() {

						@Override
						public void run() {
							scroll_view.fullScroll(ScrollView.FOCUS_UP);
						}
					}, 200);
				}
				return false;
			}
		});
		llDots.setVisibility(View.INVISIBLE);
		// 消耗点击事件，防止被覆盖的listItem响应
		ll_listHead.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});
		rl_search.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});
	}

	// 初始化banner
	private void initBanner() {
		mBanners = new ArrayList<HomeBanner>();
		currentItem = 0;

		slideViews = new ArrayList<ImageView>();
		dots = new ArrayList<View>();
		vpSlide.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,CommonUtils.getFitHeightWithSize(getActivity(),
						PicSize.GIFT_WIDTH, PicSize.GIFT_HEIGHT)));
		SlideUtils.initViewPager(getActivity(), llDots, slideViews, dots);
		slideAdapter = new SlideAdapter(slideViews);
		vpSlide.setAdapter(slideAdapter);
		v_bg.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, CommonUtils.getFitHeightWithSize(
						getActivity(), PicSize.GIFT_WIDTH, PicSize.GIFT_HEIGHT)));
		vpSlide.setOnTouchListener(new MyTouchListener());
		vpSlide.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if (slideViews.size() > 1) { // 多于1，才会循环跳转
					if (position < 1) { // 首位之前，跳转到末尾（N）
						position = mBanners.size(); // 注意这里是mList，而不是mViews
						vpSlide.setCurrentItem(position, false);
						return;
					} else if (position > mBanners.size()) { // 末位之后，跳转到首位（1）
						vpSlide.setCurrentItem(1, false); // false:不显示跳转过程的动画
						return;
					}
					currentItem = position;
					int i = position - 1;
					if (dots.size() > i) {
						if (oldPosition > 0 && dots.size() > oldPosition - 1) {
							dots.get(oldPosition - 1).setBackgroundResource(
									R.drawable.dot_n);
						}
						dots.get(i).setBackgroundResource(R.drawable.dot_p);
					}
					oldPosition = position;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		getBannerUrl();
	}

	// 初始化scrollView
	private void initScrollView() {
		scroll_view.setCallbacks(this);
		scroll_view.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						onScrollChanged(scroll_view.getScrollY());
					}
				});
		// 检测scrollView是否滑到底部了
		scroll_view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					index++;
					break;
				}
				if (event.getAction() == MotionEvent.ACTION_UP && index > 0) {
					index = 0;
					View view = ((ScrollView) v).getChildAt(0);
					if (view.getMeasuredHeight() <= v.getScrollY()
							+ v.getHeight()
							&& loadMore) {
						// 加载数据代码
						onLoadMore();
					}
				}
				return false;
			}
		});
	}

	// 初始化listView
	private void initListView() {
		giftList = new ArrayList<GameGiftEntity>();
		newGiftList = new ArrayList<GameGiftEntity>();
		listAdapter = new GiftListAdapter(getActivity(), giftList);
		xlistview_footer_content.setVisibility(View.GONE);
		lv_giftPrefecture.setAdapter(listAdapter);
		lv_giftPrefecture.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (!NetworkUtils.isNetworkAvailable(getActivity())) {
					ToastUtils.toastShow(getActivity(), "网络不可用！");
					return;
				}
				GameGiftEntity gift = giftList.get(position);
				Intent intent = new Intent(getActivity(),GiftDetailActivity_.class);
				intent.putExtra("giftId", gift.getId());
				startActivity(intent);
			}
		});
	}

	/**
	 * 重新计算ListView的高度，解决ScrollView和ListView两个View都有滚动的效果，在嵌套使用时起冲突的问题
	 * 
	 * @param listView
	 */
	public void setListViewHeight(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		if (currentPage == 2 && !isFirst) {
			scroll_view.fullScroll(ScrollView.FOCUS_UP);
		}
		isFirst = false;
	}

	// 没有数据
	private void noData() {
		lv_giftPrefecture.setVisibility(View.GONE);
		il_loadFailure.setVisibility(View.VISIBLE);
		v_space.setVisibility(View.VISIBLE);
		ivMsg.setImageResource(R.drawable.no_data);
		tvMsg.setVisibility(View.GONE);
		tv_Msg2.setVisibility(View.GONE);
		btn_refresh.setText("重新加载");
	}

	// 没有网络
	private void noNetwork() {
		lv_giftPrefecture.setVisibility(View.GONE);
		il_loadFailure.setVisibility(View.VISIBLE);
		v_space.setVisibility(View.VISIBLE);
		ivMsg.setImageResource(R.drawable.icon_signal);
		tvMsg.setText("加载失败");
		tvMsg.setVisibility(View.VISIBLE);
		tv_Msg2.setVisibility(View.VISIBLE);
		btn_refresh.setText("重新加载");
	}

	private void reLoadData() {
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			noNetwork();
			return;
		}
		lv_giftPrefecture.setVisibility(View.VISIBLE);
		il_loadFailure.setVisibility(View.GONE);
		v_space.setVisibility(View.GONE);
		currentPage = 1;
		loadGiftList(true);
		getBannerUrl();
	}

	// 获取banner图
	private void getBannerUrl() {
		VolleyUtils.requestString(getActivity(), ApiUrl.LIBAO_BANNER, null,
				this, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						try {
							// System.out.println("------------LIBAO_BANNER response------------"
							// + response);
							JSONObject responseJson = new JSONObject(response);
							List<HomeBanner> banners = new ArrayList<HomeBanner>();
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								JSONArray bannerArray = responseJson
										.getJSONArray("data");
								if (bannerArray != null
										&& bannerArray.length() > 0) {
									int len = bannerArray.length();
									for (int i = 0; i < len; i++) {
										JSONObject bannerObj = bannerArray
												.getJSONObject(i);
										HomeBanner banner = new HomeBanner();
										banner.setBannerUrl(bannerObj
												.getString("thumbnail"));
										banner.setId(bannerObj
												.getLong("target_id"));
										banner.setType(bannerObj
												.getString("target_type"));
										banner.setTitle(bannerObj
												.getString("title"));
										banners.add(banner);
									}
									mBanners.clear();
									mBanners.addAll(banners);
									oldPosition = 1;
									// 顶部滑动
									moveEnable = true;
									SlideUtils.setImageView(getActivity(),
											llDots, slideViews, dots, mBanners,
											PicSize.GIFT_WIDTH,
											PicSize.GIFT_HEIGHT);
									vpSlide.setAdapter(slideAdapter);
									slideAdapter.notifyDataSetChanged();
									// vpSlide.setCurrentItem(0);
								}
							} else {
								ToastUtils.toastShow(getActivity(), msg);
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

	// 加载礼包列表
	private void loadGiftList(boolean isLoading) {
		if (currentPage == 1) {
			giftList.clear();
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("client", ApiUrl.SOURCE_APP + "");
		params.put("page", currentPage + "");
		params.put("pagesize", ApiUrl.PAGE_SIZE + "");
		if (pm.getUserId() != -1) {
			params.put("uid", pm.getUserId() + "");
		}
		newGiftList.clear();
		VolleyUtils.requestToString(getActivity(), ApiUrl.LIBAO_LIST, params,
				this, isLoading, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						// System.out.println("-------------loadGiftList-------------\n response="
						// + response.toString());
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
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
										GameGiftEntity newGift = new GameGiftEntity(
												obj.getLong("id"), obj
														.getString("name"), obj
														.getString("content"),
												obj.getInt("code_num"), obj
														.getInt("status"), obj
														.getString("icon"), obj
														.getInt("surplus_num"),
												obj.getString("end_time"));
										if (obj.has("is_receive")) {
											newGift.setIs_receive(obj
													.getInt("is_receive"));
										}
										newGiftList.add(newGift);
									}
								}
								giftList.addAll(newGiftList);
								xlistview_footer_progressbar
										.setVisibility(View.INVISIBLE);
								xlistview_footer_hint_textview
										.setVisibility(View.VISIBLE);
								if (newGiftList.size() >= ApiUrl.PAGE_SIZE) {
									currentPage++;
									loadMore = true;
									xlistview_footer_content
											.setVisibility(View.VISIBLE);
								} else {
									xlistview_footer_content
											.setVisibility(View.GONE);
									loadMore = false;
								}
								if (giftList.size() <= 0) {
									noData();
								}
								listAdapter.notifyDataSetChanged();
								setListViewHeight(lv_giftPrefecture);
							} else {
								ToastUtils.toastShow(getActivity(), msg);
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

	@Click(R.id.iv_myGift)
	void toMyGift() {
		if (pm.getUserId() == -1) {
			showLoginDialog();
			return;
		}
		StartUtils.startMyGift(getActivity());
	}

	// 提示登录
	private void showLoginDialog() {
		new CommonDialog(getActivity(), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				if (arg1 == CommonDialog.CLICK_OK) {
					StartUtils.startLogin(getActivity());
				}
				dialog.cancel();
			}
		}).show();
	}

	@Click(R.id.btn_refresh)
	void refreshClick() {
		reLoadData();
	}

	@Click(R.id.iv_search)
	void searchGift() {
		String strSearch = edt_search.getText().toString().trim();
		if (strSearch == null || strSearch.length() == 0) {
			ToastUtils.toastShow(getActivity(), "请输入搜索内容！");
			return;
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("page", "1");
		params.put("pagesize", ApiUrl.PAGE_SIZE + "");
		params.put("wd", strSearch);
		if (pm.getUserId() != -1) {
			params.put("uid", pm.getUserId() + "");
		}
		searchGiftList(params);
	}

	@Click(R.id.iv_refresh)
	void ivRefreshClick() {
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			noNetwork();
			return;
		}
		currentPage = 1;
		loadGiftList(true);
	}

	@Click(R.id.tv_refresh)
	void tvRefreshClick() {
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			noNetwork();
			return;
		}
		v_space.setVisibility(View.GONE);
		currentPage = 1;
		loadGiftList(true);
	}

	// 搜索礼包
	private void searchGiftList(final HashMap<String, String> params) {
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			ToastUtils.toastShow(getActivity(), "网络不可用！");
			return;
		}
		VolleyUtils.requestStringWithLoading(getActivity(),
				ApiUrl.LIBAO_SEARCH, params, this,
				new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						// System.out.println("-------------searchGiftList-------------\n response="
						// + response.toString());
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								List<GameGiftEntity> mList = new ArrayList<GameGiftEntity>();
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
										mList.add(newGift);
									}
									if (mList.size() > 0) {
										Intent intent = new Intent(getActivity(),GiftSearchResultActivity_.class);
										Bundle bd = new Bundle();
										bd.putSerializable("gift",(Serializable) mList);
										bd.putString("search", params.get("wd"));
										intent.putExtras(bd);
										startActivity(intent);
									}
								} else {
									ToastUtils.toastShow(getActivity(),"未找到相关礼包！");
								}
							} else {
								ToastUtils.toastShow(getActivity(), msg);
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

	// 下拉刷新
	public void onRefresh() {
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			noNetwork();
			return;
		}
		currentPage = 1;
		loadGiftList(false);
	}

	// 上拉加载更多
	public void onLoadMore() {
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			noNetwork();
			return;
		}
		xlistview_footer_progressbar.setVisibility(View.VISIBLE);
		xlistview_footer_hint_textview.setVisibility(View.GONE);
		loadGiftList(false);
	}

	@Override
	public void initView() {

	}

	@Override
	public void onStart() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// 当页面显示出来后，每两秒钟切换一次图片显示
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 3,
				TimeUnit.SECONDS);
		super.onStart();
	}

	@Override
	public void onStop() {
		// 当不可见的时候停止切换
		scheduledExecutorService.shutdown();
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (receiver != null) {
			getActivity().unregisterReceiver(receiver);
		}
	}

	class RefreshReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(ACTION_LOGOUT_OR_LOGIN)) {
				reLoadData();
			} else if (intent.getAction().equals(ACTION_GIFT_DETAIL)) {
				int size = giftList.size();
				long id = intent.getLongExtra("id", -1);
				if (id != -1) {
					for (int i = 0; i < size; i++) {
						if (id == giftList.get(i).getId()) {
							giftList.get(i).setIs_receive(
									GameGiftEntity.IS_RECEIVE_YES);
							listAdapter.notifyDataSetChanged();
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * 监听手势监听器
	 * 
	 */
	class MyTouchListener implements OnTouchListener {
		Timer t;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				isVpTouch = true;
				if (t != null) {
					t.cancel();
				}
				break;
			case MotionEvent.ACTION_UP:
			default:
				t = new Timer();
				t.schedule(new TimerTask() {

					@Override
					public void run() {
						isVpTouch = false;
					}
				}, 3000);
				break;
			}
			return false;
		}
	}

}
