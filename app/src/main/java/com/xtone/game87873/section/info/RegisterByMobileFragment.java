package com.xtone.game87873.section.info;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.umeng.analytics.MobclickAgent;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.contants.UmengEvent;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;

@EFragment(R.layout.fragment_register_by_mobile)
public class RegisterByMobileFragment extends BaseFragment {

	private ReSendHandler reSendHandler;
	@ViewById
	TextView tv_toLogin;
	@ViewById
	EditText edt_mobile, edt_password, edt_smsCode;
	@ViewById
	Button btn_getSmsCode, btn_register;
	@ViewById
	LinearLayout ll_mobile, ll_pwd;
	private HashMap<String, String> params;

	@AfterViews
	void afterViews() {
		reSendHandler = new ReSendHandler(this);
		params = new HashMap<String, String>();
		// edt_mobile.setOnFocusChangeListener(this);
		// edt_password.setOnFocusChangeListener(this);
	}

	@Override
	public void initView() {

	}

	@Click(R.id.tv_toLogin)
	void toLoginClick() {
		getActivity().finish();
	}

	@Click(R.id.btn_getSmsCode)
	void getSmsCodeClick() {
		String mobile = edt_mobile.getText().toString().trim();
		String pwd = edt_password.getText().toString().trim();
		if (validateMobile(mobile) && validatePwd(pwd)) {
			params.put("mobile", mobile);
			params.put("type", ApiUrl.SEND_CODE_TYPE_REGISTER + "");
			getSmsCode();
		}
	}

	// 检查手机号
	private boolean validateMobile(String mobile) {
		if (mobile == null || mobile.length() == 0) {
			ToastUtils.toastShow(getActivity(), "请输入手机号！");
			ll_mobile.setBackgroundResource(R.drawable.edt_error_bg);
			return false;
		}
		if (mobile.length() != 11) {
			ToastUtils.toastShow(getActivity(), "请输入正确的手机号！");
			ll_mobile.setBackgroundResource(R.drawable.edt_error_bg);
			return false;
		}
		return true;
	}

	// 检查密码
	private boolean validatePwd(String pwd) {
		if (pwd == null || pwd.length() == 0) {
			ToastUtils.toastShow(getActivity(), "请输入密码！");
			ll_pwd.setBackgroundResource(R.drawable.edt_error_bg);
			return false;
		}
		if (pwd.length() < 6 || pwd.length() > 18) {
			ToastUtils.toastShow(getActivity(), "请输入6-18位密码！");
			ll_pwd.setBackgroundResource(R.drawable.edt_error_bg);
			return false;
		}
		return true;
	}

	@Click(R.id.btn_register)
	void registerClick() {
		if (validate()) {
			register();
		}
	}

	// 手机号验证
	private boolean validate() {
		params.clear();
		String mobile = edt_mobile.getText().toString().trim();
		String pwd = edt_password.getText().toString().trim();
		String code = edt_smsCode.getText().toString().trim();
		if (validateMobile(mobile) && validatePwd(pwd)) {
			if (code == null || code.length() == 0) {
				ToastUtils.toastShow(getActivity(), "请输入短信校验码！");
				return false;
			}
			params.put("source", ApiUrl.SOURCE_APP + "");
			params.put("mobile", mobile);
			params.put("pwd", pwd);
			params.put("code", code);
			return true;
		}
		return false;
	}

	// 获取验证码
	private void getSmsCode() {
		VolleyUtils.requestStringWithLoadingNoRetry(getActivity(),
				ApiUrl.SEND_SMS_CODE, params, this,
				new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								btn_getSmsCode.setEnabled(false);
								btn_getSmsCode
										.setTextColor(getResources().getColor(
												R.color.mine_text_gray_808080));
								reSendHandler.sendEmptyMessage(60);
								ToastUtils.toastShow(getActivity(), "验证码已发送！");
							} else {
								ToastUtils.toastShow(getActivity(), msg);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						ToastUtils.toastShow(getActivity(), "发送失败！");
					}
				});
	}

	// 注册
	private void register() {
		// 友盟计数统计
		MobclickAgent.onEvent(getActivity(), UmengEvent.REGISTER);
		VolleyUtils.requestStringWithLoading(getActivity(),
				ApiUrl.USER_REGISTER, params, this,
				new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								ToastUtils.toastShow(getActivity(), "注册成功！");
								Intent intent = new Intent();
								intent.putExtra("mobile", params.get("mobile"));
								intent.putExtra("pwd", params.get("pwd"));
								getActivity().setResult(Activity.RESULT_OK,
										intent);
								getActivity().finish();
							} else {
								ToastUtils.toastShow(getActivity(), msg);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						ToastUtils.toastShow(getActivity(), "注册失败！");
					}
				});
	}

	// 从发短信校验码计时
	static class ReSendHandler extends Handler {
		WeakReference<RegisterByMobileFragment> mActivity;

		public ReSendHandler(RegisterByMobileFragment mActivity) {
			super();
			this.mActivity = new WeakReference<RegisterByMobileFragment>(
					mActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			RegisterByMobileFragment activity = mActivity.get();
			int time = msg.what;
			if (time > 0) {
				activity.btn_getSmsCode.setText(time + "秒后重发");
				activity.reSendHandler.sendEmptyMessageDelayed(--time, 1000);
			} else {
				activity.btn_getSmsCode.setText(R.string.get_sms_code);
				activity.btn_getSmsCode.setText("重新发送");
				activity.btn_getSmsCode.setEnabled(true);
				activity.btn_getSmsCode.setTextColor(activity.getResources()
						.getColor(R.color.white));
			}
		}

	}

	// @Override
	// public void onFocusChange(View v, boolean focus) {
	// if (isHidden()) {
	// return;
	// }
	// switch (v.getId()) {
	// case R.id.edt_mobile:
	// if (focus) {
	// ll_mobile.setBackgroundResource(R.drawable.mine_user_info_item_bg);
	// } else {
	// validateMobile(edt_mobile.getText().toString().trim());
	// }
	// break;
	// case R.id.edt_password:
	// if (focus) {
	// ll_pwd.setBackgroundResource(R.drawable.mine_user_info_item_bg);
	// } else {
	// validatePwd(edt_password.getText().toString().trim());
	// }
	// break;
	// }
	// }

}
