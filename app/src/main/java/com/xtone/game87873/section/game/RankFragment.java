package com.xtone.game87873.section.game;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.xtone.game87873.R;
import com.xtone.game87873.general.base.BaseFragment;

/**
 * 首页排行页
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-3 下午3:09:05
 */
@EFragment(R.layout.fragment_rank)
public class RankFragment extends BaseFragment {
	private BaseFragment[] rankFragments = new BaseFragment[2];
	private List<ImageView> lines;
	@ViewById
	RadioGroup rgRank;
	@ViewById
	RadioButton rbDanji, rbWangyou;
	@ViewById
	ViewPager vpRank;
	@ViewById
	ImageView iv1, iv2;

	@Override
	public void initView() {
		int item = vpRank.getCurrentItem();
		if (rankFragments[item] != null) {
			rankFragments[item].initView();
		}
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
		vpRank.setAdapter(new RankPagerAdapter(getFragmentManager()));
		// vpRank.setOffscreenPageLimit(2);
		rgRank.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
				case R.id.rbDanji:
					vpRank.setCurrentItem(1);
					break;
				case R.id.rbWangyou:
					vpRank.setCurrentItem(0);
					break;

				default:
					break;
				}
			}
		});
		vpRank.setOnPageChangeListener(new OnPageChangeListener() {

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
					rbWangyou.setChecked(true);
					break;
				case 1:
					rbDanji.setChecked(true);
					break;

				default:
					break;
				}
				if (rankFragments[arg0] != null) {
					rankFragments[arg0].initView();
				}
				for (int i = 0; i < lines.size(); i++) {
					lines.get(i).setVisibility(View.INVISIBLE);
				}
				lines.get(arg0).setVisibility(View.VISIBLE);
			}
		});
		rbWangyou.setChecked(true);
	}

	private class RankPagerAdapter extends FragmentPagerAdapter {

		public RankPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			switch (arg0) {
			case 0:
				if (rankFragments[0] == null) {
					RankListFragment frag = new RankListFragment_();
					frag.setType(RankListFragment.TYPE_WANGYOU);
					rankFragments[0] = frag;
				}
				break;
			case 1:
				if (rankFragments[1] == null) {
					RankListFragment frag = new RankListFragment_();
					frag.setType(RankListFragment.TYPE_DANJI);
					rankFragments[1] = frag;
				}
				break;
			// case 2:
			// if (rankFragments[2] == null) {
			// RankListFragment frag = new RankListFragment_();
			// frag.setType(RankListFragment.TYPE_WANGYOU);
			// rankFragments[2] = frag;
			// }
			// break;
			}
			return rankFragments[arg0];
		}

		@Override
		public int getCount() {
			return rankFragments.length;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		int item = vpRank.getCurrentItem();
		if (rankFragments[item] != null) {
			rankFragments[item].onResume();
		}
	}
}
