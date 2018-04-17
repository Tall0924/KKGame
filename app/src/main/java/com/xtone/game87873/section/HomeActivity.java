package com.xtone.game87873.section;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.AppUtils;
import com.xtone.game87873.general.utils.JsonUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.section.dialog.UpdateDialog;
import com.xtone.game87873.section.dialog.UserProgressDialog;

@EActivity(R.layout.activity_home)
public class HomeActivity extends FragmentActivity {

	public static final String IS_CLEAR = "is_clear";
	public static HomeActivity mInstance;
	public static boolean isShowGuide = false;//是否显示新手指引
	/**
	 * 记录当前位置
	 */
	private int curindex = 0;
	private boolean isExit = false;
	/**
	 * 3个tab页
	 */
	private BaseFragment[] homeviews = new BaseFragment[3];
	/**
	 * 3个tab按钮
	 */
	@ViewById
	ImageView iv_tab_index, iv_tab_sec, iv_tab_third, ivGuide,ivContinue;
	@ViewById
	TextView tv_tab_index, tv_tab_sec, tv_tab_third;
	@ViewById
	RelativeLayout rlGuide;

	/**
	 * 动画
	 */
	private Animation left_in, left_out, right_in, right_out;

	// 引导图片资源
	private final int[] guides = { R.drawable.img_guide_01,R.drawable.img_guide_02, R.drawable.img_guide_03 };

	@Click(R.id.rlGuide)
	void showGuide() {
		
	}
	
	@Click(R.id.tab_index)
	public void gameClick() {
		changeview(0, true);
	}

	@Click(R.id.tab_sec)
	public void giftClick() {
		changeview(1, true);
	}

	@Click(R.id.tab_third)
	public void infoClick() {
		changeview(2, true);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		// 友盟自动更新
//		UmengUpdateAgent.setUpdateAutoPopup(true);
//		UmengUpdateAgent.setUpdateListener(null);
//		UmengUpdateAgent.setUpdateOnlyWifi(false);
//		UmengUpdateAgent.update(this);
		checkUpdate();
		// 友盟消息推送
		PushAgent mPushAgent = PushAgent.getInstance(this);
		// if (AppStorage.isReceiveNotification()) {
		// mPushAgent.enable();
		// } else {
		// mPushAgent.disable();
		// }
		mPushAgent.onAppStart();
		// 在“23:00”到“7:00”之间收到通知消息时不响铃，不振动，不闪灯
		// mPushAgent.setNoDisturbMode(23, 0, 7, 0);
	}

	private void checkUpdate() {
		// new UpdateDialog(HomeActivity.this,
		// "http://down.87873.cn/com.xtone.game87873_v1.3.0.apk ", "更新日志：\n" +
		// "更新来了").show();
		HashMap<String, String> params = new HashMap<String, String>();
		final int versionCode = AppUtils.getVersionCode(this);
		params.put("version_code", versionCode + "");
		VolleyUtils.requestString(this, ApiUrl.CHECK_UPDATE, params, this,
				new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						try {
							AppLog.redLog("CHECK_UPDATE__", response);
							JSONObject responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200 && responseJson.has("data")) {
								JSONArray jsonArray = responseJson.getJSONArray("data");
								if (jsonArray != null && jsonArray.length() > 0) {
									JSONObject jsonObj = jsonArray.getJSONObject(0);
									int code = JsonUtils.getJSONInt(jsonObj,"version_code");
									String log = JsonUtils.getJSONString(jsonObj, "update_remark");
									String downloadUrl = JsonUtils.getJSONString(jsonObj,"update_url");
									if (versionCode < code) {
										new UpdateDialog(HomeActivity.this,downloadUrl, log).show();
										UserProgressDialog.getInstane().dismiss();
									}
								}
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
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@AfterViews
	public void afterView() {
		mInstance = this;
		// 初始化UI
		initComponet();
		// 初始化动画
		initAnim();
		// 初始化页面
		changeview(0, true);
		

		if (isShowGuide && guides.length > 0) {
			isShowGuide = false;
			rlGuide.setVisibility(View.VISIBLE);
//			ImageLoaderUtils.loadImg(ivGuide, "drawable://" + guides[0], 0, 0);
			ivGuide.setImageResource(guides[0]);
			if (guides.length == 1) {
				ivContinue.setImageResource(R.drawable.btn_guide);
			} else {
				ivContinue.setImageResource(R.drawable.btn_guide_next);
			}
			ivContinue.setOnClickListener(new OnClickListener() {
				private int position = 1;

				@Override
				public void onClick(View v) {
					if (guides.length > position) {
//						ImageLoaderUtils.loadImg(ivGuide, "drawable://"+ guides[position], 0, 0);
						ivGuide.setImageResource(guides[position]);
						if (guides.length == position + 1) {
							ivContinue.setImageResource(R.drawable.btn_guide);
						} else {
							ivContinue.setImageResource(R.drawable.btn_guide_next);
						}
						position++;
					} else {
						rlGuide.setVisibility(View.GONE);
					}
				}
			});
		} else {
			rlGuide.setVisibility(View.GONE);
		}
	}

	/**
	 * 初始化特效
	 */
	private void initAnim() {
		left_in = AnimationUtils.loadAnimation(this, R.anim.left_in);
		left_out = AnimationUtils.loadAnimation(this, R.anim.left_out);
		right_in = AnimationUtils.loadAnimation(this, R.anim.right_in);
		right_out = AnimationUtils.loadAnimation(this, R.anim.right_out);
	}

	/**
	 * 初始化UI
	 */
	private void initComponet() {
		homeviews[0] = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.frag_game);
		homeviews[1] = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.frag_gift);
		homeviews[2] = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.frag_info);
	}

	/**
	 * 跳转事件触发，即将发生的事件
	 * 
	 * @param indexs
	 */
	private void changeview(int indexs, boolean isHasAnim) {
		boolean isSameTab = false;
		boolean isleft = false;
		if (indexs != curindex) {

			if (indexs < curindex) {
				isleft = true;
				if (isHasAnim) {
					homeviews[curindex].getView().startAnimation(right_out);
				}
			} else {
				isleft = false;
				if (isHasAnim)
					homeviews[curindex].getView().startAnimation(left_out);
			}

			isSameTab = false;
		} else {
			isSameTab = true;
		}

		resetVisible(indexs);
		curindex = indexs;
		homeviews[curindex].initView();

		if (!isSameTab)
			if (isleft) {
				if (isHasAnim) {
					homeviews[curindex].getView().startAnimation(left_in);
				}
			} else {
				if (isHasAnim) {
					homeviews[curindex].getView().startAnimation(right_in);
				}
			}

		if (indexs == 0) {
			iv_tab_index.setBackgroundResource(R.drawable.tab_game_p);
			tv_tab_index.setTextColor(getResources().getColor(
					R.color.home_tab_text_p));
		} else {
			iv_tab_index.setBackgroundResource(R.drawable.tab_game_n);
			tv_tab_index.setTextColor(getResources().getColor(
					R.color.home_tab_text_n));
		}

		if (indexs == 1) {
			iv_tab_sec.setBackgroundResource(R.drawable.tab_gif_p);
			tv_tab_sec.setTextColor(getResources().getColor(
					R.color.home_tab_text_p));
		} else {
			iv_tab_sec.setBackgroundResource(R.drawable.tab_gif_n);
			tv_tab_sec.setTextColor(getResources().getColor(
					R.color.home_tab_text_n));
		}

		if (indexs == 2) {
			iv_tab_third.setBackgroundResource(R.drawable.tab_me_p);
			tv_tab_third.setTextColor(getResources().getColor(
					R.color.home_tab_text_p));
		} else {
			iv_tab_third.setBackgroundResource(R.drawable.tab_me_n);
			tv_tab_third.setTextColor(getResources().getColor(
					R.color.home_tab_text_n));
		}

	}

	/**
	 * 重置所有的fragment
	 */
	private void resetVisible(int index) {
		// for (int i = 0; i < homeviews.length; i++) {
		// homeviews[i].getView().setVisibility(View.GONE);
		// }
		//
		// homeviews[index].getView().setVisibility(View.VISIBLE);
		homeviews[index].getView().bringToFront();//bringToFront:将view从父view中移除，然后再加入到父view的顶端
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click();
		}
		return false;
	}

	private void exitBy2Click() {
		Timer tExit = null;
		if (!isExit) {
			isExit = true;
			ToastUtils.toastShow(this, R.string.exit);
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false;
				}
			}, 2000);

		} else {
			finish();
			// ProjectManagerApplication.getInstance().exit();
		}
	}

	public void setViewShow(int i) {
		changeview(i, false);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(IS_CLEAR, true);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.getBoolean(IS_CLEAR)) {
			finish();
			startActivity(getIntent());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		VolleyUtils.getRequestQueue(this).cancelAll(this);
	}
}
