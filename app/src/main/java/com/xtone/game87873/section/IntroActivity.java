package com.xtone.game87873.section;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.lidroid.xutils.HttpUtils;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.umeng.message.PushAgent;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.base.BaseActivity;
import com.xtone.game87873.general.download.DownloadManager;
import com.xtone.game87873.general.download.UpadteAndInstalledUtils;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.AppStorage;
import com.xtone.game87873.general.utils.AppUtils;
import com.xtone.game87873.general.utils.ImageLoaderUtils;
import com.xtone.game87873.general.utils.JsonUtils;
import com.xtone.game87873.general.utils.SlideUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.section.entity.IndexAdEntity;

@EActivity(R.layout.activity_intro)
public class IntroActivity extends BaseActivity {
	private boolean isClick = false;
	private boolean isLoadAd = false;
	@ViewById
	ImageView iv_intro;
	@ViewById
	ViewPager vp_intro;
	@ViewById
	TextView tvTiaoguo;
	@ViewById
	LinearLayout ll_dots;
	// 引导图片资源
	private final int[] pics = { R.drawable.intro1, R.drawable.intro2,R.drawable.intro3 };
	// 底部小店图片
	private ImageView[] dots;
	// 记录当前选中位置
	private int currentIndex;
	private List<View> views;

	@Click(R.id.tvTiaoguo)
	void doClose() {
		isClick = true;
		toHome();
	}

	@AfterViews
	public void AfterViews() {
		new UpadteAndInstalledUtils(this, null).getUpdateAndInstalled();// 检查是否有可更新和已安装
		getAd();// 联网获取广告

		// 友盟消息推送
		PushAgent mPushAgent = PushAgent.getInstance(this);
		if (AppStorage.isReceiveNotification()) {
			mPushAgent.enable();
		} else {
			mPushAgent.disable();
		}

		if (AppUtils.isFirstStart(this)) {// 该应用是否第一次安装
			HomeActivity.isShowGuide = true;
			iv_intro.setImageResource(R.drawable.intro);
			tvTiaoguo.setVisibility(View.GONE);
			showGuideView();
		} else {
			final IndexAdEntity ad = AppStorage.getIndexAdInfo();
			File file = new File(ad.getDir());
			if (ad.getAdId() != 0&& new Date().getTime() / 1000 <= ad.getEndTime()&& new Date().getTime() / 1000 >= ad.getStartTime()&& file.exists()) {
				showAd(ad);
			} else {
				tvTiaoguo.setVisibility(View.GONE);
				iv_intro.setImageResource(R.drawable.intro);
				isLoadAd = true;
				turnHomeView();
			}

		}

	}

	private void showAd(final IndexAdEntity ad) {
		tvTiaoguo.setVisibility(View.VISIBLE);
		String imageUrl = Scheme.FILE.wrap(ad.getDir());
		ImageLoaderUtils.loadImg(iv_intro, imageUrl, 0, 0);
		iv_intro.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				isClick = true;
				toHome();
				if (SlideUtils.TYPE_GAME.equals(ad.getType())) {
					StartUtils.startAppDetail(IntroActivity.this,
							ad.getTitle(), ad.getTargetId());
				} else if (SlideUtils.TYPE_TAG.equals(ad.getType())) {
					StartUtils.startTagDetail(IntroActivity.this,
							ad.getTitle(), ad.getTargetId());
				} else if (SlideUtils.TYPE_GIFT.equals(ad.getType())) {
					StartUtils.startGiftDetail(IntroActivity.this,
							ad.getTargetId());
				} else if (SlideUtils.TYPE_SUBJECT.equals(ad.getType())) {
					StartUtils.startSubjectDetail(IntroActivity.this,
							ad.getTargetId(), ad.getTitle());
				} else if (SlideUtils.TYPE_CLASSIFY.equals(ad.getType())) {
					StartUtils.startTypeDetail(IntroActivity.this,
							ad.getTitle(), ad.getTargetId());
				}
			}
		});
		turnHomeView();
	}

	// 获取广告数据
	private void getAd() {
		VolleyUtils.requestString(this, ApiUrl.INDEX_AD, null, this,
				new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);

							AppLog.redLog("***************indexAd______",
									response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								JSONObject infoObj = JsonUtils.getJSONObject(
										responseJson, "data");
								long id = JsonUtils.getJSONLong(infoObj, "id");
								String type = JsonUtils.getJSONString(infoObj,
										"type");
								Long targetId = JsonUtils.getJSONLong(infoObj,
										"target_id");
								String imgUrl = JsonUtils.getJSONString(
										infoObj, "img");
								String title = JsonUtils.getJSONString(infoObj,
										"title");
								long startTime = JsonUtils.getJSONLong(infoObj,
										"time_s");
								long endTime = JsonUtils.getJSONLong(infoObj,
										"time_e");
								String fileName = imgUrl.substring(imgUrl
										.lastIndexOf("/") + 1);
								String fileDir = DownloadManager.AD_SAVE_PATH
										+ fileName;
								AppStorage.saveIndexAdInfo(id, imgUrl,
										startTime, endTime, type, targetId,
										fileDir, title);

								File file = new File(fileDir);
								if (!file.exists()) {
									File dir = new File(
											DownloadManager.AD_SAVE_PATH);
									if (!dir.exists()) {
										dir.mkdirs();
									}
									new HttpUtils().download(imgUrl, fileDir,
											true, true, null);
								}
							} else {
								AppStorage.saveIndexAdInfo(0, "", 0, 0, "", 0,
										"", "");
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

	/**
	 * 显示引导页
	 */
	@UiThread(delay = 0)
	public void showGuideView() {
		Animation anim = AnimationUtils.loadAnimation(this,R.anim.alpha_dismiss);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				tvTiaoguo.setVisibility(View.VISIBLE);
			}
		});
		iv_intro.setAnimation(anim);
		iv_intro.setVisibility(View.GONE);

		views = new ArrayList<View>();
		for (int i = 0; i < pics.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.setBackgroundResource(pics[i]);
			if (i == pics.length - 1) {
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						isClick = true;
						toHome();
						// SharedPreferencesUtil.saveBoolean(SharedPreferencesUtil.IS_FIRST,
						// false);
					}
				});
			}
			views.add(imageView);
		}
		vp_intro.setAdapter(new ViewPagerAdapter());
		vp_intro.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if (position == pics.length - 1) {
					tvTiaoguo.setVisibility(View.GONE);
				} else {
					tvTiaoguo.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	/**
	 * 进入首页
	 */
	@UiThread(delay = 1)
	public void turnHomeView() {
		if (isLoadAd) {
			final IndexAdEntity ad = AppStorage.getIndexAdInfo();
			File file = new File(ad.getDir());
			isLoadAd = false;
			if (ad.getAdId() != 0&& new Date().getTime() / 1000 <= ad.getEndTime()&& new Date().getTime() / 1000 >= ad.getStartTime()&& file.exists()) {
				showAd(ad);
				return;
			}
		}
		if (!isClick) {
			iv_intro.setClickable(false);
			toHome();
		}
	}

	private void toHome() {
		// if (AccountConfig.isLogin())
		// {// 判断是否登录

		Intent intent = new Intent(this, HomeActivity_.class);
		startActivity(intent);
		if (!isClick) {
			overridePendingTransition(R.anim.alpha_into,R.anim.alpha_bigger_dismiss);
		}
		finish();
		// }
		// else
		// {
		// SectionLogic.getInstance().toLoginView(this);// 没有登录的话去登录页
		// overridePendingTransition(R.anim.alpha_into,
		// R.anim.alpha_bigger_dismiss);
		// finish();
		// }
	}

	public class ViewPagerAdapter extends PagerAdapter {

		// 销毁arg1位置的界面
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(views.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		// 获得当前界面数
		@Override
		public int getCount() {
			if (views != null) {
				return views.size();
			}

			return 0;
		}

		// 初始化arg1位置的界面
		@Override
		public Object instantiateItem(View arg0, int arg1) {

			((ViewPager) arg0).addView(views.get(arg1), 0);

			return views.get(arg1);
		}

		// 判断是否由对象生成界面
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

	}

}
