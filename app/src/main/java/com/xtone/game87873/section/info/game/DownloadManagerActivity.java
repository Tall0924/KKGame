package com.xtone.game87873.section.info.game;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.UserActions;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.base.SwipeBackFragmentActivity;
import com.xtone.game87873.general.download.UpadteAndInstalledUtils;
import com.xtone.game87873.general.download.UpadteAndInstalledUtils.MyCallBack;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.section.HomeActivity;

/**
 * 我的游戏
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-7 下午1:35:13
 */
@EActivity(R.layout.activity_download_manager)
public class DownloadManagerActivity extends SwipeBackFragmentActivity {

	private DownloadReceiveBroadCast receiveBroadCast,installedReceiver;
	private BaseFragment[] fragments = new BaseFragment[2];
	@ViewById
	RadioGroup rgNav;
	@ViewById
	RadioButton navUpdate, navDownload;
	@ViewById
	TextView tvTitle;
	@ViewById
	ViewPager vpContent;
	@ViewById
	ImageView ivDownload;
	@ViewById
	ImageView iv1, iv2;
	private List<ImageView> lines;

	@Click(R.id.ivReturn)
	void toReturn() {
		finish();
	}

	@Click(R.id.ivDownload)
	void toDownloadPage() {

	}

	@AfterViews
	void afterViews() {
		lines = new ArrayList<ImageView>();
		lines.add(iv1);
		lines.add(iv2);
		for (int i = 0; i < lines.size(); i++) {
			lines.get(i).setVisibility(View.INVISIBLE);
		}
		lines.get(0).setVisibility(View.VISIBLE);
		tvTitle.setText(R.string.download_manager);
		ivDownload.setVisibility(View.GONE);
		vpContent.setAdapter(new GamePagerAdapter(getSupportFragmentManager()));
		rgNav.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
				case R.id.navDownload:
					vpContent.setCurrentItem(0);
					break;
				case R.id.navUpdate:
					vpContent.setCurrentItem(1);
					break;

				default:
					break;
				}
			}
		});
		vpContent.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					navDownload.setChecked(true);
					break;
				case 1:
					navUpdate.setChecked(true);
					break;

				default:
					break;
				}
				if (fragments[arg0] != null) {
					fragments[arg0].initView();
				}
				for (int i = 0; i < lines.size(); i++) {
					lines.get(i).setVisibility(View.INVISIBLE);
				}
				lines.get(arg0).setVisibility(View.VISIBLE);
			}
		});
		navDownload.setChecked(true);

		new UpadteAndInstalledUtils(this, new MyCallBack() {

			@Override
			public void doUpdateCallBack() {
				int cur = vpContent.getCurrentItem();
				if (fragments[cur] != null) {
					fragments[cur].initView();
				}

			}

			@Override
			public void doInstalledCallBack() {
				int cur = vpContent.getCurrentItem();
				if (fragments[cur] != null) {
					fragments[cur].initView();
				}

			}
		}).getUpdateList();
		// 注册广播接收
		receiveBroadCast = new DownloadReceiveBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(UserActions.ACTION_DOWNLOAD_CHANGE); // 只有持有相同的action的接受者才能接收此广播
		registerReceiver(receiveBroadCast, filter);
		
		installedReceiver = new DownloadReceiveBroadCast();
		IntentFilter filter1 = new IntentFilter();
		filter1.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter1.addDataScheme("package");
		registerReceiver(installedReceiver, filter1);
	}

	private class DownloadReceiveBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(UserActions.ACTION_DOWNLOAD_CHANGE)) {
				int cur = vpContent.getCurrentItem();
				if (fragments[cur] != null) {
					fragments[cur].initView();
				}
			} else if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
				new UpadteAndInstalledUtils(DownloadManagerActivity.this, new MyCallBack() {

					@Override
					public void doUpdateCallBack() {
						int cur = vpContent.getCurrentItem();
						if (fragments[cur] != null) {
							fragments[cur].initView();
						}

					}

					@Override
					public void doInstalledCallBack() {
						int cur = vpContent.getCurrentItem();
						if (fragments[cur] != null) {
							fragments[cur].initView();
						}

					}
				}).getUpdateList();
			}
		}

	}

	private class GamePagerAdapter extends FragmentPagerAdapter {

		public GamePagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int arg0) {
			switch (arg0) {
			case 0:
				if (fragments[0] == null) {
					fragments[0] = new DownloadGameFragment_();
				}
				break;
			case 1:
				if (fragments[1] == null) {
					fragments[1] = new UpdateGameFragment_();
				}
				break;
			}
			return fragments[arg0];
		}

		@Override
		public int getCount() {
			return fragments.length;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(receiveBroadCast!=null){
		unregisterReceiver(receiveBroadCast);
		}
		if(installedReceiver!=null){
			unregisterReceiver(installedReceiver);
		}
	}
}
