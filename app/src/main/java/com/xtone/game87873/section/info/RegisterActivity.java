package com.xtone.game87873.section.info;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

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
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.umeng.analytics.MobclickAgent;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.UserActions;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.base.SwipeBackFragmentActivity;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.section.HomeActivity;

@EActivity(R.layout.activity_register)
public class RegisterActivity extends SwipeBackFragmentActivity {

	@ViewById
	ImageView iv_headLeft;
	@ViewById
	TextView tv_headTitle;
	@ViewById
	ViewPager vpContent;
	@ViewById
	ImageView iv1, iv2;
	@ViewById
	RadioGroup rg_register;
	@ViewById
	RadioButton rbtn_mobileRegister, rbtn_userNameRegister;
	private BaseFragment[] indexFragments = new BaseFragment[2];
	private List<ImageView> lines;
	private MyFragmentPagerAdapter pageAdapter;

	@AfterViews
	void afterViews() {
		tv_headTitle.setText(R.string.register_page);
		lines = new ArrayList<ImageView>();
		lines.add(iv1);
		lines.add(iv2);
		for (int i = 0; i < lines.size(); i++) {
			lines.get(i).setVisibility(View.INVISIBLE);
		}
		lines.get(0).setVisibility(View.VISIBLE);
		pageAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
		vpContent.setAdapter(pageAdapter);
		rg_register.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
				case R.id.rbtn_mobileRegister:
					vpContent.setCurrentItem(0);
					break;
				case R.id.rbtn_userNameRegister:
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
					rbtn_mobileRegister.setChecked(true);
					break;
				case 1:
					rbtn_userNameRegister.setChecked(true);
					break;

				default:
					break;
				}
				if (indexFragments[arg0] != null) {
					indexFragments[arg0].initView();
				}
				for (int i = 0; i < lines.size(); i++) {
					lines.get(i).setVisibility(View.INVISIBLE);
				}
				lines.get(arg0).setVisibility(View.VISIBLE);
			}
		});
		rbtn_mobileRegister.setChecked(true);
	}

	@Click(R.id.iv_headLeft)
	void backClick() {
		finish();
	}

	class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			switch (arg0) {
			case 0:
				if (indexFragments[0] == null) {
					indexFragments[0] = new RegisterByMobileFragment_();
				}
				break;
			case 1:
				if (indexFragments[1] == null) {
					indexFragments[1] = new RegisterByUserNameFragment_();
				}
				break;
			}
			return indexFragments[arg0];
		}

		@Override
		public int getCount() {
			return indexFragments.length;
		}
	}

	public void setCurrentItem(int i) {
		if (vpContent != null && vpContent.getChildCount() > i) {
			vpContent.setCurrentItem(i);
		}
	}


}
