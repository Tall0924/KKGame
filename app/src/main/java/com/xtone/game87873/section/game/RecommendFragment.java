package com.xtone.game87873.section.game;

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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.contants.PicSize;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.download.DownloadInfo;
import com.xtone.game87873.general.manager.PreferenceManager;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.CommonUtils;
import com.xtone.game87873.general.utils.JsonUtils;
import com.xtone.game87873.general.utils.NetworkUtils;
import com.xtone.game87873.general.utils.SlideUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.xlistview.IXListViewListener;
import com.xtone.game87873.general.widget.xlistview.XListView;
import com.xtone.game87873.section.entity.HomeBanner;
import com.xtone.game87873.section.entity.TypeTagEntity;
import com.xtone.game87873.section.game.adapter.GameListAdapter;
import com.xtone.game87873.section.game.adapter.SlideAdapter;

/**
 * 首页推荐页
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-3 下午3:09:05
 */
@EFragment(R.layout.fragment_list)
public class RecommendFragment extends BaseFragment implements IXListViewListener {
	private int page = 1;
	private final int PAGE_SIZE = 30;
	private LayoutInflater mInflater;
	private Handler mHandler;
	private ScheduledExecutorService scheduledExecutorService;
	private List<ImageView> slideViews;
	private List<View> dots;
	private SlideAdapter myAdapter;
	private int currentItem;// 图片切换当前的位置
	private int oldPosition = 1;// 图片切换上一个位置的位置
	private GameListAdapter gameListAdapter;
	private View headView;
	private List<HomeBanner> mBanners, mADs;
	private List<DownloadInfo> mAppInfos;
	@ViewById
	XListView lvContent;
	private ViewPager vpSlide;
	private LinearLayout llDots;
	private View rlXinyou, rlJingbao, rlBibei, rlWangyou;
	private List<TypeTagEntity> mTags;
	private boolean isVpTouch = false;
	@ViewById
	LinearLayout il_loadFailure;
	@ViewById
	Button btn_refresh;

	private boolean isLoading = true;// 是否显示加载框

	// 切换当前显示的图片
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (!isVpTouch && slideViews.size() > 1) {
				currentItem = (currentItem) % mBanners.size() + 1;
				vpSlide.setCurrentItem(currentItem, true);// 切换当前显示的图片
			}
		}
	};

	@Override
	public void initView() {
		if (gameListAdapter != null) {
			gameListAdapter.notifyDataSetChanged();
		}
	}

	@AfterViews
	void afterViews() {
		mBanners = new ArrayList<HomeBanner>();
		mADs = new ArrayList<HomeBanner>();
		mAppInfos = new ArrayList<DownloadInfo>();
		mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mHandler = new Handler();
		mTags = new ArrayList<>();
		slideViews = new ArrayList<ImageView>();
		dots = new ArrayList<View>();
		currentItem = 0;
		addHeadView();
		vpSlide.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,CommonUtils.getFitHeightWithSize(getActivity(),
						PicSize.INDEX_AD_WIDTH, PicSize.INDEX_AD_HEIGHT)));
		SlideUtils.initViewPager(getActivity(), llDots, slideViews, dots);
		myAdapter = new SlideAdapter(slideViews);
		vpSlide.setAdapter(myAdapter);
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
							dots.get(oldPosition - 1).setBackgroundResource(R.drawable.dot_n);
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

		gameListAdapter = new GameListAdapter(getActivity(), mAppInfos);
		gameListAdapter.setType(GameListAdapter.TYPE_HOME_LIST);
		gameListAdapter.setADList(mADs);
		lvContent.setAdapter(gameListAdapter);
		lvContent.setXListViewListener(this);
		lvContent.setPullRefreshEnable(true);
		lvContent.setPullLoadEnable(false);
		lvContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
				if (position > 1) {
					Intent intent = new Intent(getActivity(),AppDetailActivity_.class);
					intent.putExtra(AppDetailActivity.GAME_NAME,mAppInfos.get(position - 2).getAppName());
					intent.putExtra(AppDetailActivity.GAME_ID,mAppInfos.get(position - 2).getAppId());
					startActivity(intent);
				}

			}
		});
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			noNetwork();
			return;
		}
		isLoading = true;
		getInitData();
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
		getInitData();
	}

	@Click(R.id.btn_refresh)
	void refreshClick() {
		reLoadData();
	}

	/**
	 * 添加头部视图
	 */
	private void addHeadView() {
		headView = mInflater.inflate(R.layout.fragment_recommend_top, null);
		vpSlide = (ViewPager) headView.findViewById(R.id.vpSlide);
		llDots = (LinearLayout) headView.findViewById(R.id.llDots);

		lvContent.addHeaderView(headView);
	}

	private void getInitData() {
		VolleyUtils.requestString(getActivity(), ApiUrl.HOME_BANNER, null,
				this, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						try {
							AppLog.redLog("HOME_BANNER__", response);
							JSONObject responseJson = new JSONObject(response);
							List<HomeBanner> banners = new ArrayList<HomeBanner>();
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								JSONArray bannerArray = responseJson.getJSONArray("data");
								if (bannerArray != null&& bannerArray.length() > 0) {
									int len = bannerArray.length();
									for (int i = 0; i < len; i++) {
										JSONObject bannerObj = bannerArray.getJSONObject(i);
										HomeBanner banner = new HomeBanner();
										banner.setBannerUrl(JsonUtils.getJSONString(bannerObj,"banner"));
										banner.setId(JsonUtils.getJSONLong(bannerObj, "target_id"));
										banner.setType(JsonUtils.getJSONString(bannerObj, "type"));
										banner.setTitle(JsonUtils.getJSONString(bannerObj,"title"));
										banners.add(banner);
									}
									mBanners.clear();
									mBanners.addAll(banners);
									oldPosition = 1;
									SlideUtils.setImageView(getActivity(),
											llDots, slideViews, dots, mBanners,
											PicSize.INDEX_AD_WIDTH,
											PicSize.INDEX_AD_HEIGHT);
									vpSlide.setAdapter(myAdapter);
									myAdapter.notifyDataSetChanged();
									// for (ImageView v : slideViews) {
									// v.setOnTouchListener(new
									// OnTouchListener() {
									//
									// @Override
									// public boolean onTouch(
									// View arg0,
									// MotionEvent event) {
									// if (event.getAction() ==
									// MotionEvent.ACTION_MOVE) {
									// isVpTouch = true;
									// } else {
									// isVpTouch = false;
									// }
									// return false;
									// }
									// });
									// }
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
		VolleyUtils.requestString(getActivity(), ApiUrl.HOME_TAG_LIST, null,
				this, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						try {
							AppLog.redLog("HOME_TAG__", response);
							JSONObject responseJson = new JSONObject(response);
							List<TypeTagEntity> tags = new ArrayList<TypeTagEntity>();
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								JSONArray tagArray = responseJson.getJSONArray("data");
								if (tagArray != null && tagArray.length() > 0) {
									int len = tagArray.length();
									for (int i = 0; i < len; i++) {
										JSONObject tagObj = tagArray.getJSONObject(i);
										TypeTagEntity tag = new TypeTagEntity();
										tag.setId(Long.parseLong(JsonUtils.getJSONString(tagObj, "tag_id")));
										tag.setName(JsonUtils.getJSONString(tagObj, "title"));
										tag.setImg(JsonUtils.getJSONString(tagObj, "img"));
										tags.add(tag);
									}
									mTags.clear();
									mTags.addAll(tags);
									setTags();
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
		VolleyUtils.requestString(getActivity(), ApiUrl.HOME_AD, null, this,
				new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						try {
							AppLog.redLog("AD_____", response);
							JSONObject responseJson = new JSONObject(response);
							List<HomeBanner> banners = new ArrayList<HomeBanner>();
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								JSONArray bannerArray = responseJson.getJSONArray("data");
								if (bannerArray != null&& bannerArray.length() > 0) {
									int len = bannerArray.length();
									for (int i = 0; i < len; i++) {
										JSONObject bannerObj = bannerArray.getJSONObject(i);
										HomeBanner banner = new HomeBanner();
										banner.setBannerUrl(JsonUtils.getJSONString(bannerObj,"banner"));
										banner.setId(JsonUtils.getJSONLong(bannerObj, "target_id"));
										banner.setType(JsonUtils.getJSONString(bannerObj, "type"));
										banner.setTitle(JsonUtils.getJSONString(bannerObj,"title"));
										banners.add(banner);
									}
									mADs.clear();
									mADs.addAll(banners);
									gameListAdapter.notifyDataSetChanged();
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

					}
				});
		page = 1;
		getGameList();
	}

	private void setTags() {
		rlBibei = headView.findViewById(R.id.rlBibei);
		rlJingbao = headView.findViewById(R.id.rlJingbao);
		rlWangyou = headView.findViewById(R.id.rlWangyou);
		rlXinyou = headView.findViewById(R.id.rlXinyou);
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(),R.drawable.icon_tag_loaging, options);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.icon_tag_loaging);
		int width = bitmap.getWidth();
		if (mTags.size() > 0) {
			((TextView) headView.findViewById(R.id.tv_text1)).setText(mTags.get(0).getName());
			ImageView icon = (ImageView) headView.findViewById(R.id.iv_icon1);
			LayoutParams params = new LayoutParams(width, width);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			icon.setLayoutParams(params);
			VolleyUtils.setURLImage(getActivity(), icon, mTags.get(0).getImg(),
					R.drawable.icon_tag_loaging, R.drawable.icon_tag_loaging,
					options.outWidth, options.outHeight);
		}
		if (mTags.size() > 1) {
			((TextView) headView.findViewById(R.id.tv_text2)).setText(mTags.get(1).getName());
			ImageView icon = (ImageView) headView.findViewById(R.id.iv_icon2);
			LayoutParams params = new LayoutParams(width, width);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			icon.setLayoutParams(params);
			VolleyUtils.setURLImage(getActivity(), icon, mTags.get(1).getImg(),
					R.drawable.icon_tag_loaging, R.drawable.icon_tag_loaging,
					options.outWidth, options.outHeight);
		}
		if (mTags.size() > 2) {
			new PreferenceManager(getActivity()).setTypeTagEntity(mTags.get(2));
			((TextView) headView.findViewById(R.id.tv_text3)).setText(mTags.get(2).getName());
			ImageView icon = (ImageView) headView.findViewById(R.id.iv_icon3);
			LayoutParams params = new LayoutParams(width, width);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			icon.setLayoutParams(params);
			VolleyUtils.setURLImage(getActivity(), icon, mTags.get(2).getImg(),
					R.drawable.icon_tag_loaging, R.drawable.icon_tag_loaging,
					options.outWidth, options.outHeight);
		}
		if (mTags.size() > 3) {
			((TextView) headView.findViewById(R.id.tv_text4)).setText(mTags.get(3).getName());
			ImageView icon = (ImageView) headView.findViewById(R.id.iv_icon4);
			LayoutParams params = new LayoutParams(width, width);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL);
			icon.setLayoutParams(params);
			VolleyUtils.setURLImage(getActivity(), icon, mTags.get(3).getImg(),
					R.drawable.icon_tag_loaging, R.drawable.icon_tag_loaging,
					options.outWidth, options.outHeight);
		}

		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.rlXinyou: {
					// Intent intent = new Intent(getActivity(),
					// NewGameActivity.class);
					// startActivity(intent);
					if (mTags.size() > 0) {
						Intent intent = new Intent(getActivity(),ClassifyDetailActivity_.class);
						intent.putExtra(ClassifyDetailActivity.TAG_NAME, mTags.get(0).getName());
						intent.putExtra(ClassifyDetailActivity.TAG_ID, mTags.get(0).getId());
						startActivity(intent);
					}
					break;
				}
				case R.id.rlJingbao: {
					// Intent intent = new Intent(getActivity(),
					// JingbaoActivity.class);
					// startActivity(intent);
					if (mTags.size() > 1) {
						Intent intent = new Intent(getActivity(),ClassifyDetailActivity_.class);
						intent.putExtra(ClassifyDetailActivity.TAG_NAME, mTags.get(1).getName());
						intent.putExtra(ClassifyDetailActivity.TAG_ID, mTags.get(1).getId());
						startActivity(intent);
					}
					break;
				}
				case R.id.rlBibei: {
					// Intent intent = new Intent(getActivity(),
					// BibeiActivity.class);
					// startActivity(intent);
					if (mTags.size() > 2) {
						Intent intent = new Intent(getActivity(),ClassifyDetailActivity_.class);
						intent.putExtra(ClassifyDetailActivity.TAG_NAME, mTags.get(2).getName());
						intent.putExtra(ClassifyDetailActivity.TAG_ID, mTags.get(2).getId());
						startActivity(intent);
					}
					break;
				}
				case R.id.rlWangyou: {
					// Intent intent = new Intent(getActivity(),
					// WangyouActivity.class);
					// startActivity(intent);

					if (mTags.size() > 3) {
						Intent intent = new Intent(getActivity(),ClassifyDetailActivity_.class);
						intent.putExtra(ClassifyDetailActivity.TAG_NAME, mTags.get(3).getName());
						intent.putExtra(ClassifyDetailActivity.TAG_ID, mTags.get(3).getId());
						startActivity(intent);
					}
					break;
				}
				default:
					break;
				}

			}
		};
		rlXinyou.setOnClickListener(listener);
		rlJingbao.setOnClickListener(listener);
		rlBibei.setOnClickListener(listener);
		rlWangyou.setOnClickListener(listener);

	}

	private void getGameList() {
		HashMap<String, String> params = new HashMap<>();
		params.put("page", page + "");
		params.put("pagesize", PAGE_SIZE + "");

		VolleyUtils.requestToString(getActivity(), ApiUrl.HOME_GAME_LIST,
				params, this, isLoading, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						onLoad();
						AppLog.redLog("gamelist_____", response);
						if (page == 1) {
							mAppInfos.clear();
						}
						List<DownloadInfo> appInfos = new ArrayList<>();
						try {
							JSONObject responseJson = new JSONObject(response);

							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {

								JSONArray bannerArray = responseJson.getJSONArray("data");
								if (bannerArray != null&& bannerArray.length() > 0) {
									int len = bannerArray.length();
									for (int i = 0; i < len; i++) {
										JSONObject infoObj = bannerArray.getJSONObject(i);
										DownloadInfo info = new DownloadInfo();
										info.setAppId(infoObj.getLong("id"));
										info.setApkPackageName(JsonUtils.getJSONString(infoObj,"apk_package_name"));
										info.setVersionCode(JsonUtils.getJSONInt(infoObj,"apk_version_code"));
										info.setVersionName(JsonUtils.getJSONString(infoObj,"apk_version_name"));
										info.setAppName(JsonUtils.getJSONString(infoObj,"name_zh"));
										info.setApkMark(JsonUtils.getJSONInt(infoObj, "game_rank"));
										info.setAppDes(JsonUtils.getJSONString(infoObj, "synopsis"));
										info.setTypeName(JsonUtils.getJSONString(infoObj,"typename"));
										info.setAppSize(JsonUtils.getJSONString(infoObj,"apk_size"));
										info.setApkDownloadUrl(JsonUtils.getJSONString(infoObj,"apk_url"));
										info.setAppIconUrl(JsonUtils.getJSONString(infoObj, "icon"));
										info.setDownloadTotal(JsonUtils.getJSONLong(infoObj,"dowaload_total"));
										info.setGameIconSuperscript(JsonUtils.getJSONInt(infoObj,"game_icon_superscript"));
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
						if (page > 0 && appInfos.size() == 0) {
							page--;
						}

						if (appInfos.size() >= PAGE_SIZE) {
							lvContent.setPullLoadEnable(true);
						} else {
							lvContent.setPullLoadEnable(false);
						}
						gameListAdapter.notifyDataSetChanged();
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

	@Override
	public void onStart() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// 当页面显示出来后，每两秒钟切换一次图片显示
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 3,TimeUnit.SECONDS);
		super.onStart();
	}

	@Override
	public void onStop() {
		// 当不可见的时候停止切换
		scheduledExecutorService.shutdown();
		super.onStop();
	}

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

	@Override
	public void onResume() {
		if (gameListAdapter != null) {
			gameListAdapter.notifyDataSetChanged();
		}
		super.onResume();
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
				getInitData();
			}
		}, 1000);
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
				getGameList();
			}
		}, 1000);
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
