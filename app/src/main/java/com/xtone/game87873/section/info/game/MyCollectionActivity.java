package com.xtone.game87873.section.info.game;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

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
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.base.SwipeBackFragmentActivity;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.section.HomeActivity;

/**
 * 我的收藏
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-7 下午1:35:13
 */
@EActivity(R.layout.activity_collection)
public class MyCollectionActivity extends SwipeBackFragmentActivity {

	private BaseFragment[] collectionFragments = new BaseFragment[2];
	public static boolean isEdit = false;
	private List<ImageView> lines;
	@ViewById
	RadioGroup rgNav;
	@ViewById
	RadioButton navGame, navInformation;
	@ViewById
	TextView tvTitle;
	@ViewById
	ViewPager vpContent;
	@ViewById
	ImageView ivDownload;
	@ViewById
	ImageView iv1, iv2;

	@Click(R.id.ivReturn)
	void toReturn() {
		finish();
	}

	@Click(R.id.ivDownload)
	void toDownloadPage() {
		if (isEdit) {
			isEdit = false;
			ivDownload.setImageResource(R.drawable.btn_title_edit);
		} else {
			isEdit = true;
			ivDownload.setImageResource(R.drawable.btn_head_finish);
		}
		((CollectGameFragment)collectionFragments[0]).clearChoosed();
		((CollectInformationFragment)collectionFragments[1]).clearChoosed();
		int cur = vpContent.getCurrentItem();
		collectionFragments[cur].initView();
	}

	@AfterViews
	void afterViews() {
		isEdit = false;
		lines = new ArrayList<ImageView>();
		lines.add(iv1);
		lines.add(iv2);
		for (int i = 0; i < lines.size(); i++) {
			lines.get(i).setVisibility(View.INVISIBLE);
		}
		lines.get(0).setVisibility(View.VISIBLE);
		tvTitle.setText(R.string.wodeshoucang);
		ivDownload.setImageResource(R.drawable.btn_title_edit);
		vpContent.setAdapter(new CollectionPagerAdapter(
				getSupportFragmentManager()));
		rgNav.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
				case R.id.navGame:
					vpContent.setCurrentItem(0);
					break;
				case R.id.navInformation:
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
					navGame.setChecked(true);
					break;
				case 1:
					navInformation.setChecked(true);
					break;

				default:
					break;
				}
				if (collectionFragments[arg0] != null) {
					collectionFragments[arg0].initView();
				}
				for (int i = 0; i < lines.size(); i++) {
					lines.get(i).setVisibility(View.INVISIBLE);
				}
				lines.get(arg0).setVisibility(View.VISIBLE);
			}
		});
		navGame.setChecked(true);
	}

	private class CollectionPagerAdapter extends FragmentPagerAdapter {

		public CollectionPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int arg0) {
			switch (arg0) {
			case 0:
				if (collectionFragments[0] == null) {
					collectionFragments[0] = new CollectGameFragment_();
				}
				break;
			case 1:
				if (collectionFragments[1] == null) {
					collectionFragments[1] = new CollectInformationFragment_();
				}
				break;
			}
			return collectionFragments[arg0];
		}

		@Override
		public int getCount() {
			return collectionFragments.length;
		}
	}
	
}
