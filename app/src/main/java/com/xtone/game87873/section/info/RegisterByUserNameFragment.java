package com.xtone.game87873.section.info;

import java.util.HashMap;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
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
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;

@EFragment(R.layout.fragment_register_by_user_name)
public class RegisterByUserNameFragment extends BaseFragment implements
		OnFocusChangeListener {

	@ViewById
	TextView tv_toLogin;
	@ViewById
	EditText edt_userName, edt_password;
	@ViewById
	Button btn_register;
	@ViewById
	LinearLayout ll_userName, ll_pwd;
	private HashMap<String, String> params;

	@Override
	public void initView() {
		params = new HashMap<String, String>();
		edt_userName.setOnFocusChangeListener(this);
		edt_password.setOnFocusChangeListener(this);
	}

	@Override
	public void onFocusChange(View arg0, boolean arg1) {

	}

	@Click(R.id.tv_toLogin)
	void toLoginClick() {
		getActivity().finish();
	}

	// 检查手机号
	private boolean validateUserName(String userName) {
		if (userName == null || userName.length() == 0) {
			ToastUtils.toastShow(getActivity(), "请输入用户名！");
			ll_userName.setBackgroundResource(R.drawable.edt_error_bg);
			return false;
		}
		if (userName.length() < 6 || userName.length() > 18) {
			ToastUtils.toastShow(getActivity(), "请输入正确的用户名！");
			ll_userName.setBackgroundResource(R.drawable.edt_error_bg);
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
		String strUserName = edt_userName.getText().toString().trim();
		String strPwd = edt_password.getText().toString().trim();
		AppLog.greenLog("huangzx", strUserName + "    " + strPwd);
		if (validateUserName(strUserName) && validatePwd(strPwd)) {
			params.clear();
			params.put("account", strUserName);
			params.put("pwd", strPwd);
			register();
		}
	}

	// 注册
	private void register() {
		// 友盟计数统计
		MobclickAgent.onEvent(getActivity(), UmengEvent.REGISTER);
		VolleyUtils.requestStringWithLoading(getActivity(),
				ApiUrl.USER_REGISTER_BY_ACCOUNT, params, this,
				new VolleyCallback<String>() {

					@Override
					public void onResponse(String response) {
						AppLog.greenLog("huangzx", response);
						JSONObject responseJson;
						try {
							responseJson = new JSONObject(response);
							int status = responseJson.getInt("status");
							String msg = responseJson.getString("message");
							if (status == 200) {
								ToastUtils.toastShow(getActivity(), "注册成功！");
								Intent intent = new Intent();
								intent.putExtra("mobile", params.get("account"));
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
						// AppLog.greenLog("huangzx", error.getMessage());
						ToastUtils.toastShow(getActivity(), "注册失败！");
					}
				});
	}

}
