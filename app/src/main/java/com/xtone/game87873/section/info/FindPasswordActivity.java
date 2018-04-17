package com.xtone.game87873.section.info;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.base.SwipeBackActivity;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;

@EActivity(R.layout.activity_find_password)
public class FindPasswordActivity extends SwipeBackActivity {

	private ReSendHandler reSendHandler;
	@ViewById
	ImageView iv_headLeft;
	@ViewById
	TextView tv_headTitle;
	@ViewById
	EditText edt_mobile, edt_smsCode, edt_password;
	@ViewById
	Button btn_getSmsCode, btn_ok;
	private HashMap<String, String> params;
	private String mobile;
	private int time = 60;

	@AfterViews
	void afterViews() {
		tv_headTitle.setText(R.string.find_password);
		reSendHandler = new ReSendHandler(this);
		params = new HashMap<String, String>();
	}

	@Click(R.id.iv_headLeft)
	void backClick() {
		finish();
	}

	@Click(R.id.btn_getSmsCode)
	void getSmsCodeClick() {
		String mobile = edt_mobile.getText().toString().trim();
		String pwd = edt_password.getText().toString().trim();
		if (validateMobile(mobile) && validatePwd(pwd)) {
			params.clear();
			params.put("mobile", mobile);
			params.put("type", ApiUrl.SEND_CODE_TYPE_FIND_PASSWORD + "");
			getSmsCode();
		}
	}

	// 检查手机号
	private boolean validateMobile(String mobile) {
		if (mobile == null || mobile.length() == 0) {
			ToastUtils.toastShow(this, "请输入手机号！");
			edt_mobile.requestFocus();
			return false;
		}
		if (mobile.length() != 11) {
			ToastUtils.toastShow(this, "请输入正确的手机号！");
			edt_mobile.requestFocus();
			return false;
		}
		return true;
	}

	// 检查密码
	private boolean validatePwd(String pwd) {
		if (pwd == null || pwd.length() == 0) {
			ToastUtils.toastShow(this, "请输入密码！");
			edt_password.requestFocus();
			return false;
		}
		if (pwd.length() < 6 || pwd.length() > 18) {
			ToastUtils.toastShow(this, "请输入6-18位密码！");
			edt_password.requestFocus();
			return false;
		}
		return true;
	}

	@Click(R.id.btn_ok)
	void submitClick() {
		String code = edt_smsCode.getText().toString().trim();
		String pwd = edt_password.getText().toString().trim();
		mobile = edt_mobile.getText().toString().trim();
		if (validateMobile(mobile) && validatePwd(pwd)) {
			if (code == null || code.length() == 0) {
				ToastUtils.toastShow(this, "请输入验证码！");
				edt_smsCode.requestFocus();
				return;
			}
			params.clear();
			params.put("mobile", mobile);
			params.put("code", code);
			params.put("pwd", pwd);
			changePwd();
		}
	}

	// 获取验证码
	private void getSmsCode() {
		VolleyUtils.requestStringWithLoadingNoRetry(this, ApiUrl.SEND_SMS_CODE,
				params, this, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						// System.out.println("-----------------send code-------------------\n response="
						// + response);
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								btn_getSmsCode.setEnabled(false);
								reSendHandler.sendEmptyMessage(0);
								ToastUtils.toastShow(FindPasswordActivity.this,
										"验证码已发送！");
								btn_getSmsCode
										.setTextColor(getResources().getColor(
												R.color.mine_text_gray_808080));
							} else {
								ToastUtils.toastShow(FindPasswordActivity.this,
										msg);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// System.out.println("-----------------send code-------------------\n error="+error.getMessage());
						ToastUtils
								.toastShow(FindPasswordActivity.this, "发送失败！");
					}
				});
	}

	private void changePwd() {
		VolleyUtils.requestStringWithLoading(this, ApiUrl.USER_REPWD, params,
				this, new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						// System.out.println("-----------------changePwd-------------------\n response="
						// + response);
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								ToastUtils.toastShow(FindPasswordActivity.this,
										"密码修改成功！");
								finish();
							} else {
								ToastUtils.toastShow(FindPasswordActivity.this,
										msg);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						// System.out.println("-----------------verify mobile-------------------\n error="+error.getMessage());
					}
				});
	}

	static class ReSendHandler extends Handler {
		WeakReference<FindPasswordActivity> mActivity;

		public ReSendHandler(FindPasswordActivity mActivity) {
			super();
			this.mActivity = new WeakReference<FindPasswordActivity>(mActivity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			FindPasswordActivity activity = mActivity.get();
			if (activity.time > 0) {
				activity.btn_getSmsCode.setText("(" + activity.time + "秒)重发");
				activity.reSendHandler.sendEmptyMessageDelayed(0, 1000);
				activity.time--;
			} else {
				activity.time = 60;
				activity.btn_getSmsCode.setText(R.string.get_sms_code);
				activity.btn_getSmsCode.setEnabled(true);
				activity.btn_getSmsCode.setTextColor(activity.getResources()
						.getColor(R.color.white));
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (reSendHandler.hasMessages(0)) {
			reSendHandler.removeMessages(0);
		}
	}

}
