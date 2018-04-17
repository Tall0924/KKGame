package com.xtone.game87873;

import java.util.Map.Entry;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sohu.cyan.android.sdk.api.Config;
import com.sohu.cyan.android.sdk.api.CyanSdk;
import com.sohu.cyan.android.sdk.exception.CyanException;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.xtone.game87873.section.info.LoginActivity_;

/**
 * MyApplication.java
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-6-23 下午3:09:05
 */
public class MyApplication extends Application {
	private static MyApplication application;

	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		// 全局异常捕获
		// GlobalExceptionHanlder.getInstance().register(this);

		// startService(new Intent(this, DownloadService.class));

		// 创建默认的ImageLoader配置参数
		ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(configuration);

		// 取到在线配置的发送策略
		MobclickAgent.updateOnlineConfig(this);
		// Umeng,禁止默认的页面统计方式，这样将不会再自动统计Activity。
		MobclickAgent.openActivityDurationTrack(false);

		PushAgent mPushAgent = PushAgent.getInstance(this);
		/**
		 * 该Handler是在BroadcastReceiver中被调用，故
		 * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK 参考集成文档的1.6.2
		 * http://dev.umeng.com/push/android/integration#1_6_2
		 * */
		UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
			@Override
			public void dealWithCustomAction(Context context, UMessage msg) {
				// Toast.makeText(context, msg.custom,Toast.LENGTH_LONG).show();
			}

			@Override
			public void openActivity(Context context, UMessage msg) {
				try {
					Intent intent = new Intent(context,Class.forName(msg.activity));
					addMessageToIntent(intent, msg);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}

			private Intent addMessageToIntent(Intent intent, UMessage msg) {
				if (intent == null || msg == null || msg.extra == null)
					return intent;

				for (Entry<String, String> entry : msg.extra.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					if (key != null)
						if (key.endsWith("id") || key.endsWith("Id")) {
							long id = 0;
							try {
								id = Long.parseLong(value);
							} catch (Exception e) {
							}
							intent.putExtra(key, id);
						} else {
							intent.putExtra(key, value);
						}
				}
				return intent;
			}

		};
		// 使用自定义的NotificationHandler，来结合友盟统计处理消息通知
		// 参考http://bbs.umeng.com/thread-11112-1-1.html
		// CustomNotificationHandler notificationClickHandler = new
		// CustomNotificationHandler();
		mPushAgent.setNotificationClickHandler(notificationClickHandler);

		// 初始化畅言sdk
		Config config = new Config();
		config.ui.toolbar_bg = getResources().getColor(R.color.toolbar_bg);
		config.ui.toolbar_border = getResources().getColor(R.color.toolbar_border);
//		config.ui.toolbar_btn = getResources().getColor(R.color.toolbar_btn);
		config.ui.list_title = getResources().getColor(R.color.list_title);
		config.ui.after_clk = getResources().getColor(R.color.after_clk);
		config.ui.before_clk = getResources().getColor(R.color.before_clk);
		config.ui.edit_cmt_bg = Color.WHITE;
		config.comment.showScore = true;
		config.comment.uploadFiles = false;
		config.comment.anonymous_token = null;
		config.login.SSOLogin = true;
		config.login.QQ = false;
		config.login.SINA = false;
		config.login.SOHU = false;
		config.login.SSO_Assets_ICon = "ic_launcher.png";
		config.login.loginActivityClass = LoginActivity_.class;
		try {
			CyanSdk.register(this, "cysiSoHPW","b3d19fe046c6e4e0c931361aabc7d34a", "http://www.87873.cn",config);
		} catch (CyanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static MyApplication getInstance() {
		return application;
	}
}
