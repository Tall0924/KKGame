package com.xtone.game87873.section.gift;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Random;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.contants.UmengEvent;
import com.xtone.game87873.general.base.SwipeBackActivity;
import com.xtone.game87873.general.manager.PreferenceManager;
import com.xtone.game87873.general.utils.AppUtil;
import com.xtone.game87873.general.utils.AppUtils;
import com.xtone.game87873.general.utils.BitmapHelper;
import com.xtone.game87873.general.utils.ClipboardUtil;
import com.xtone.game87873.general.utils.DateUtil;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.section.StartUtils;
import com.xtone.game87873.section.dialog.CommonDialog;
import com.xtone.game87873.section.entity.GameGiftEntity;

/**
 * 礼包详情
 * 
 * @author huangzx
 * */

@EActivity(R.layout.activity_gift_detail)
public class GiftDetailActivity extends SwipeBackActivity {

	@ViewById
	ImageView iv_headLeft, iv_gameIcon, iv_headRight;
	@ViewById
	TextView tv_headTitle, tv_giftName, tv_residueCount, tv_alreadyTakeCount,
			tv_residueTime, tv_residueTimeL, tv_applicablePlatform,
			tv_applicableValidity, tv_giftDetail, tv_giftDestription,
			tv_giftRemark, tv_residue, tv_part;
	@ViewById
	Button btn_download, btn_take, btn_copy;
	@ViewById
	LinearLayout ll_container, ll_count, ll_residueTime;
	@ViewById
	ScrollView scrollView;
	private HashMap<String, String> params;
	private GameGiftEntity gift;
	private PreferenceManager pm;
	private long uid, libaoId;
	private boolean isInstal = false;
	private String giftCode;
	private PopupWindow popDownload, popCopy;
	private Random random;
	private PopHandler pHandler;

	@SuppressWarnings("deprecation")
	@AfterViews
	void afterViews() {
		tv_headTitle.setText(R.string.gift_detail);
		iv_headRight.setVisibility(View.VISIBLE);
		iv_headRight.setImageResource(R.drawable.btn_head_share);
		params = new HashMap<String, String>();
		pm = new PreferenceManager(this);
		random = new Random();
		libaoId = getIntent().getExtras().getLong("giftId", -1);
		if (libaoId != -1) {
			getGiftDetail();
		} else {
			ToastUtils.toastShow(this, "出错了，请稍候重试！");
			finish();
		}
		View popViewDownload = LayoutInflater.from(this).inflate(
				R.layout.layout_pop_download_tip, null);
		popDownload = new PopupWindow(popViewDownload, BitmapHelper.dip2px(
				this, 134), BitmapHelper.dip2px(this, 40));
		// 需要设置一下此参数，点击外边可消失
		popDownload.setBackgroundDrawable(new BitmapDrawable());
		// 设置点击窗口外边窗口消失
		popDownload.setOutsideTouchable(true);
		View popViewCopy = LayoutInflater.from(this).inflate(
				R.layout.layout_pop_gift_copy, null);
		popCopy = new PopupWindow(popViewCopy,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		popCopy.setBackgroundDrawable(new BitmapDrawable());
		popCopy.setOutsideTouchable(true);
		pHandler = new PopHandler(this);
	}

	@Click(R.id.iv_headLeft)
	void backClick() {
		finish();
	}

	@Click(R.id.tv_headTitle)
	void titleClick() {
		finish();
	}

	@Click(R.id.iv_headRight)
	void shareClick() {
		Bundle bundle = new Bundle();
		bundle.putString("share_content",
				"免费拿，礼包免费拿！来87手游宝，就是有意想不到的福利，热门手游限量礼包免费领取，让你畅快玩手游。");
		bundle.putString("share_url", "http://app.87873.cn");
		bundle.putString("share_title", gift.getName());
		bundle.putString("icon", gift.getIcon());
		StartUtils.startCustomShareDialogActivity(GiftDetailActivity.this,
				bundle);
	}

	@Click(R.id.iv_gameIcon)
	void gameIconClick() {
		toGameDetail();
	}

	@Click(R.id.btn_download)
	void downloadClick() {
		isInstal = AppUtil.isApkInstall(this, gift.getApk_package_name());
		if (isInstal) {
			btn_download.setText("打开");
			AppUtils.startApp(this, gift.getApk_package_name());
		} else {
			toGameDetail();
		}
	}

	@Click(R.id.btn_take)
	void takeClick() {
		isInstal = AppUtil.isApkInstall(this, gift.getApk_package_name());
		if (isInstal) {
			btn_download.setText("打开");
		}
		if (isInstal || gift.getStatus() == ApiUrl.GIFT_STATUS_FOR_NO
				|| gift.getIs_download() == 0) {
			switch (gift.getStatus()) {
			case ApiUrl.GIFT_STATUS_TAKE_NO:
				takeGameGift();
				break;
			case ApiUrl.GIFT_STATUS_FOR_NO:
				forNoGift();
				break;
			case ApiUrl.GIFT_STATUS_ORDER:

				break;
			}
		} else {
			scrollView.fullScroll(ScrollView.FOCUS_DOWN);// 滑到底部
			popDownload.showAtLocation(scrollView, Gravity.BOTTOM, 0,
					BitmapHelper.dip2px(this, 55));
		}
	}

	@Click(R.id.btn_copy)
	void copyClick() {
		popCopy.showAsDropDown(btn_copy,
				-BitmapHelper.dip2px(GiftDetailActivity.this, 60),
				BitmapHelper.dip2px(GiftDetailActivity.this, 8));
		ClipboardUtil.setClipboardText(GiftDetailActivity.this, giftCode);
		pHandler.sendEmptyMessageDelayed(0, 2000);
	}

	// 提示登录
	private void showLoginDialog() {
		new CommonDialog(this, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				if (arg1 == CommonDialog.CLICK_OK) {
					StartUtils.startLogin(GiftDetailActivity.this);
				}
				dialog.cancel();
			}
		}).show();
	}

	// 跳转到游戏详情页
	private void toGameDetail() {
		StartUtils.startAppDetail(this, gift.getName(), gift.getGame_id());
	}

	// 获取礼包详情
	private void getGiftDetail() {
		params.clear();
		params.put("libaoid", libaoId + "");
		if (pm.getUserId() != -1) {
			params.put("uid", pm.getUserId() + "");
		}
		VolleyUtils.requestStringWithLoading(this, ApiUrl.LIBAO_INFO, params,
				this, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								JSONObject obj = responseJson
										.getJSONObject("data");
								gift = new GameGiftEntity(obj.getLong("id"),
										obj.getString("name"), obj
												.getLong("game_id"), obj
												.getString("content"), obj
												.getInt("code_num"), obj
												.getInt("status"), obj
												.getString("icon"), obj
												.getInt("surplus_num"), obj
												.getString("range"), obj
												.getString("explain"), obj
												.getString("tips"), obj
												.getString("remarks"), obj
												.getString("start_time"), obj
												.getString("end_time"), obj
												.getString("apk_package_name"),
										obj.getString("apk_version_code"));
								if (obj.has("is_download")) {
									gift.setIs_download(obj
											.getInt("is_download"));
								}
								if (obj.has("is_receive")) {
									gift.setIs_receive(obj.getInt("is_receive"));
								}
								initViews();
							} else {
								ToastUtils.toastShow(GiftDetailActivity.this,
										msg);
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

	private void initViews() {
		// 友盟计数统计
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("gift_id", gift.getId() + "");
		map.put("gift_title", gift.getName());
		MobclickAgent.onEvent(this, UmengEvent.CLICK_GIFT, map);

		ll_container.setVisibility(View.VISIBLE);
		VolleyUtils.setURLImage(this, iv_gameIcon, gift.getIcon(),
				R.drawable.icon_game_loading, R.drawable.icon_game_loading);
		switch (gift.getStatus()) {
		case ApiUrl.GIFT_STATUS_TAKE_NO:
			tv_residueCount.setText(gift.getSurplus_num() + "");
			tv_alreadyTakeCount.setText(getResources().getString(
					R.string.already_take)
					+ (gift.getCode_num() - gift.getSurplus_num())
					+ getResources().getString(R.string.part));
			if (gift.getSurplus_num() == 0) {
				setBtnTakeEnable(getString(R.string.take_out));
			}
			break;
		case ApiUrl.GIFT_STATUS_FOR_NO:
			tv_residue.setText("已淘");
			tv_part.setText("次");
			int forNo = random.nextInt(gift.getCode_num());
			tv_residueCount.setText((gift.getCode_num() + forNo) + "");
			btn_take.setText(getString(R.string.for_num));
			break;
		case ApiUrl.GIFT_STATUS_ORDER:
		case ApiUrl.GIFT_STATUS_FINISH:
			tv_residueCount.setText(gift.getSurplus_num() + "");
			tv_alreadyTakeCount.setText(getResources().getString(
					R.string.already_take)
					+ (gift.getCode_num() - gift.getSurplus_num())
					+ getResources().getString(R.string.part));
			setBtnTakeEnable(getString(R.string.finish));
			break;
		}
		if (gift.getIs_receive() == 1) {
			setBtnTakeEnable(getString(R.string.already_take_2));
		}
		long residueTime = DateUtil.strToTime(gift.getEnd_time())
				- System.currentTimeMillis();
		if (residueTime > 0) {
			tv_residueTime.setText(DateUtil.getRestidueTime(residueTime));
		} else {
			tv_residueTimeL.setText(getString(R.string.finish));
			setBtnTakeEnable(getString(R.string.finish));
		}
		tv_applicableValidity.setText(gift.getStart_time().subSequence(0, 4)
				+ "年" + gift.getStart_time().subSequence(5, 7) + "月"
				+ gift.getStart_time().subSequence(8, 10) + "日" + "-"
				+ gift.getEnd_time().subSequence(0, 4) + "年"
				+ gift.getEnd_time().subSequence(5, 7) + "月"
				+ gift.getEnd_time().subSequence(8, 10) + "日");
		tv_applicablePlatform.setText(gift.getRange());
		tv_giftName.setText(gift.getName());
		tv_giftDetail.setText(gift.getContent());
		tv_giftDestription.setText(gift.getExplain());
		tv_giftRemark.setText(Html.fromHtml(gift.getRemarks()));
		isInstal = AppUtil.isApkInstall(this, gift.getApk_package_name());
		if (isInstal) {
			btn_download.setText("打开");
		}
	}

	private void setBtnTakeEnable(String text) {
		btn_take.setText(text);
		btn_take.setTextColor(getResources().getColor(
				R.color.mine_text_gray_636363));
		btn_take.setEnabled(false);
	}

	// 领取礼包
	private void takeGameGift() {
		// 友盟计数统计
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("gift_id", gift.getId() + "");
		map.put("gift_title", gift.getName());
		MobclickAgent.onEvent(this, UmengEvent.GET_GIFT, map);

		uid = pm.getUserId();
		if (uid == -1) {
			showLoginDialog();
			return;
		}
		params.clear();
		params.put("userid", uid + "");
		params.put("libaoid", gift.getId() + "");
		VolleyUtils.requestStringWithLoading(this, ApiUrl.LIBAO_RECEIVE,
				params, this, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								String code = "0";
								if (responseJson.has("data")) {
									code = responseJson.getString("data");
									giftCode = code;
									Intent data = new Intent(
											GiftPrefectureFragment.ACTION_GIFT_DETAIL);
									data.putExtra("id", gift.getId());
									sendBroadcast(data);
								}
								if (code.equals("0")) {
									ToastUtils
											.toastShow(GiftDetailActivity.this,
													"您已经领取过了！");
								} else {
									showGiftCode(code);
								}
							} else {
								ToastUtils.toastShow(GiftDetailActivity.this,
										msg);
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

	// 礼包淘号
	private void forNoGift() {
		// 友盟计数统计
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("gift_id", gift.getId() + "");
		map.put("gift_title", gift.getName());
		MobclickAgent.onEvent(this, UmengEvent.TAO_GIFT, map);

		params.clear();
		params.put("num", "1");
		params.put("libaoid", gift.getId() + "");
		VolleyUtils.requestStringWithLoading(this, ApiUrl.LIBAO_TAO_HAO,
				params, this, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								if (responseJson.has("code")) {
									String code = responseJson
											.getString("code");
									giftCode = code;
									showGiftCode(code);
								} else {
									ToastUtils.toastShow(
											GiftDetailActivity.this, "淘号成功！");
								}
							} else {
								ToastUtils.toastShow(GiftDetailActivity.this,
										msg);
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

	private void showGiftCode(String code) {
		btn_take.setVisibility(View.GONE);
		btn_copy.setVisibility(View.VISIBLE);
		tv_residueTimeL.setText("激活码：");
		tv_residueTimeL.setTextColor(getResources().getColor(
				R.color.mine_text_black_333333));
		tv_residueTime.setText(code);
		tv_residueTime.setTextColor(getResources().getColor(
				R.color.mine_text_red_ff0000));
	}

	static class PopHandler extends Handler {
		WeakReference<GiftDetailActivity> mActivity;

		public PopHandler(GiftDetailActivity mActivity) {
			super();
			this.mActivity = new WeakReference<GiftDetailActivity>(mActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			GiftDetailActivity activity = mActivity.get();
			if (activity.popCopy.isShowing()) {
				// 隐藏窗口，如果设置了点击窗口外消失即不需要此方式隐藏
				activity.popCopy.dismiss();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (gift != null) {
			isInstal = AppUtil.isApkInstall(this, gift.getApk_package_name());
			if (isInstal) {
				btn_download.setText("打开");
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (pHandler.hasMessages(0)) {
			pHandler.removeMessages(0);
		}
	}

}
