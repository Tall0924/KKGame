package com.xtone.game87873.general.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.xtone.game87873.R;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.section.HomeActivity;

/**
 * 所有的Activity的基类，前期先增加一个全局异常捕获，后续增加响应的接口方法来规范代码编写
 * 
 * @author 杨伟锦
 * @e-mail 1147953072@qq.com
 * @version v1.0
 * @copyright 2010-2015
 * @create-time 2015-1-20 下午2:55:02
 */
public class BaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 友盟消息推送 日活统计
		PushAgent.getInstance(this).onAppStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		/**
		 * 友盟统计分析 在每个Activity的onResume方法中调用 MobclickAgent.onResume(Context),
		 * onPause方法中调用 MobclickAgent.onPause(Context)
		 * 保证获取正确的新增用户、活跃用户、启动次数、使用时长等基本数据
		 * ***/
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
		MobclickAgent.onPause(this);
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
	protected void onDestroy() {
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
