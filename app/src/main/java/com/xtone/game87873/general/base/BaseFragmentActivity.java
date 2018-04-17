package com.xtone.game87873.general.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.xtone.game87873.R;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.section.HomeActivity;

/**
 * 所有的FragmentActivity的基类
 * 
 * @author ywj
 * @version v1.0
 * @copyright 2010-2015
 * @create-time 2015-1-20 下午2:55:02
 */
public class BaseFragmentActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 友盟消息推送 日活统计
		PushAgent.getInstance(this).onAppStart();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(HomeActivity.IS_CLEAR, true);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.getBoolean(HomeActivity.IS_CLEAR)) {
			finish();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyUtils.getRequestQueue(this).cancelAll(this);
	}
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
	}

	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}
}
