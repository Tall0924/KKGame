package com.xtone.game87873.section.info;

import java.util.HashMap;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sohu.cyan.android.sdk.api.CallBack;
import com.sohu.cyan.android.sdk.api.CyanSdk;
import com.sohu.cyan.android.sdk.entity.AccountInfo;
import com.sohu.cyan.android.sdk.exception.CyanException;
import com.umeng.analytics.MobclickAgent;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.contants.UmengEvent;
import com.xtone.game87873.general.base.SwipeBackActivity;
import com.xtone.game87873.general.db.UserAccountDao;
import com.xtone.game87873.general.manager.PreferenceManager;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.general.utils.NetworkUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.utils.volley.VolleyCallback;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.general.widget.CircleImageView;
import com.xtone.game87873.section.entity.UserAccount;
import com.xtone.game87873.section.gift.GiftPrefectureFragment;

@EActivity(R.layout.activity_login)
public class LoginActivity extends SwipeBackActivity {

	@ViewById
	ImageView iv_headLeft;
	@ViewById
	CircleImageView iv_user_icon;
	@ViewById
	EditText edt_userName, edt_userPassword;
	@ViewById
	TextView tv_headTitle, tv_forgetPassword;
	@ViewById
	Button btn_login, btn_register;
	private HashMap<String, String> params;
	public final static int REGISTER_CODE = 20;
	private UserAccount userAccount;

	@AfterViews
	void afterViews() {
		tv_headTitle.setText(R.string.login_87873);
		params = new HashMap<>();
	}

	@Click(R.id.iv_headLeft)
	void backClick() {
		finish();
	}

	@Click(R.id.tv_forgetPassword)
	void forgetPasswordClick() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			ToastUtils.toastShow(this, "网络不可用！");
			return;
		}
		Intent intent = new Intent(this, FindPasswordActivity_.class);
		startActivity(intent);
	}

	@Click(R.id.btn_register)
	void registerClick() {
		if (!NetworkUtils.isNetworkAvailable(this)) {
			ToastUtils.toastShow(this, "网络不可用！");
			return;
		}
		Intent intent = new Intent(this, RegisterActivity_.class);
		startActivityForResult(intent, REGISTER_CODE);
	}

	@Click(R.id.btn_login)
	void login() {
		if (validate()) {
			if (!NetworkUtils.isNetworkAvailable(this)) {
				ToastUtils.toastShow(this, "网络不可用！");
				return;
			}
			// 友盟计数统计
			MobclickAgent.onEvent(this, UmengEvent.LOGIN);
			VolleyUtils.requestStringWithLoading(this, ApiUrl.USER_LOGIN,
					params, this, new VolleyCallback<String>() {

						@Override
						public void onResponse(String response) {
							AppLog.greenLog("huangzx", "---------------response="+response);
							JSONObject responseJson;
							try {
								responseJson = new JSONObject(response);
								int status = responseJson.getInt("status");
								String msg = responseJson.getString("message");
								if (status == 200) {
									ToastUtils.toastShow(LoginActivity.this,"登录成功！");
									JSONObject obj = new JSONObject(responseJson.getString("data"));
									PreferenceManager pm = new PreferenceManager(LoginActivity.this);
									pm.setUserId(obj.getLong("id"));
									pm.setUserNick(obj.getString("nick"));
									pm.setUserFigureUrl(obj.getString("figureurl"));
									pm.setUserMobile(obj.getString("mobile"));
									setResult(RESULT_OK);
									sendLogoutBroadcast();
									new UserAccountDao(LoginActivity.this).addOrUpdate(userAccount);
									// 畅言sdk同步用戶信息
									AccountInfo account = new AccountInfo();
									// 应用自己的用户id
									account.isv_refer_id = obj.getLong("id")+ "";
									account.nickname = obj.getString("nick");
									account.img_url = obj.getString("figureurl");
									CyanSdk.getInstance(LoginActivity.this).setAccountInfo(account,
													new CallBack() {
														@Override
														public void success() {

														}

														@Override
														public void error(
																CyanException arg0) {
														}
													});

									finish();
								} else {
									ToastUtils.toastShow(LoginActivity.this,msg);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onErrorResponse(VolleyError error) {
							ToastUtils.toastShow(LoginActivity.this, "登录失败！");
						}
					});
		}
	}

	private void sendLogoutBroadcast() {
		Intent intent = new Intent(
				GiftPrefectureFragment.ACTION_LOGOUT_OR_LOGIN);
		sendBroadcast(intent);
	}

	// 输入验证
	private boolean validate() {
		String account = edt_userName.getText().toString().trim();
		String pwd = edt_userPassword.getText().toString().trim();
		if (account == null || account.length() == 0) {
			ToastUtils.toastShow(this, "请输入手机号、账号或邮箱！");
			edt_userName.requestFocus();
			return false;
		}
		if (pwd == null || pwd.length() == 0) {
			ToastUtils.toastShow(this, "请输入密码！");
			edt_userPassword.requestFocus();
			return false;
		}
		if (pwd.length() < 6 || pwd.length() > 18) {
			ToastUtils.toastShow(this, "请输入6-18位密码！");
			edt_userPassword.requestFocus();
			return false;
		}
		params.put("account", account);
		params.put("pwd", pwd);
		userAccount = new UserAccount(account, pwd);
		params.put("type", ApiUrl.SOURCE_APP + "");
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REGISTER_CODE && resultCode == RESULT_OK) {
			String mobile = data.getStringExtra("mobile");
			String pwd = data.getStringExtra("pwd");
			edt_userName.setText(mobile);
			edt_userPassword.setText(pwd);
			login();
		}
	}

}
