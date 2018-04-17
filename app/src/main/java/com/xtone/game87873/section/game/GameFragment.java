package com.xtone.game87873.section.game;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.xtone.game87873.contants.UserActions;
import com.xtone.game87873.general.base.BaseFragment;
import com.xtone.game87873.general.download.DownloadInfo;
import com.xtone.game87873.general.download.DownloadManager;
import com.xtone.game87873.general.download.DownloadService;
import com.xtone.game87873.general.utils.NetworkUtils;
import com.xtone.game87873.general.utils.ToastUtils;
import com.xtone.game87873.general.widget.BadgeView;
import com.xtone.game87873.section.StartUtils;

@EFragment(R.layout.fragment_game)
public class GameFragment extends BaseFragment {

	public static GameFragment mInstance;
	private BaseFragment[] indexFragments = new BaseFragment[5];
	private IndexPagerAdapter mAdapter;
	private List<ImageView> lines;
	@ViewById
	RadioGroup rgNav;
	@ViewById
	RadioButton navRecommend, navClassify, navSubject, navRank, navInfo;
	@ViewById
	ViewPager vpContent;
	@ViewById
	ImageView iv1, iv2, iv3, iv4, iv5, ivDownload;

	private BadgeView badge;
	private DownloadManager downloadManager;
	private List<DownloadInfo> downloadingList;
	private BroadcastReceiver receiveBroadCast;

	// 避免点击时触发底下页面的监听
	@Click(R.id.llOuter)
	void clickOuter() {

	}
	
	@Click(R.id.iv_scan)
	void scanClick() {
		StartUtils.startCaptureActivity(getActivity());
	}

	@Click(R.id.tvSearch)
	void toSearchPage() {
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			ToastUtils.toastShow(getActivity(), "网络不可用！");
			return;
		}
		Intent intent = new Intent(getActivity(), SearchActivity_.class);
		startActivity(intent);
	}

	@Click(R.id.ivDownload)
	void toDownloadPage() {
		StartUtils.startMyGame(getActivity());
	}

	@Override
	public void initView() {

	}

	@AfterViews
	void afterViews() {
		// 角标显示当前下载中的游戏有几个
		badge = new BadgeView(getActivity(), ivDownload);
		downloadManager = DownloadService.getDownloadManager(getActivity());
		downloadingList = downloadManager.getDownloadingInfoList();
		if (downloadingList.size() > 0) {
			badge.setText(downloadingList.size() + "");
			badge.show();
		} else {
			badge.hide();
		}
		// 注册广播接收
		receiveBroadCast = new BadgeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(UserActions.ACTION_DOWNLOAD_CHANGE); // 只有持有相同的action的接受者才能接收此广播
		getActivity().registerReceiver(receiveBroadCast, filter);

		mInstance = this;
		if (mAdapter == null) {
			mAdapter = new IndexPagerAdapter(getFragmentManager());
		}
		lines = new ArrayList<ImageView>();
		lines.add(iv1);
		lines.add(iv2);
		lines.add(iv3);
		lines.add(iv4);
//		lines.add(iv5);
		iv5.setVisibility(View.GONE);
		for (int i = 0; i < lines.size(); i++) {
			lines.get(i).setVisibility(View.INVISIBLE);
		}
		lines.get(0).setVisibility(View.VISIBLE);
		vpContent.setAdapter(mAdapter);
		vpContent.setOffscreenPageLimit(4);
		rgNav.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				switch (arg1) {
				case R.id.navRecommend:
					vpContent.setCurrentItem(0);
					break;
				case R.id.navClassify:
					vpContent.setCurrentItem(1);
					break;
				case R.id.navSubject:
					vpContent.setCurrentItem(2);
					break;
				case R.id.navRank:
					vpContent.setCurrentItem(3);
					break;
				case R.id.navInfo:
					vpContent.setCurrentItem(4);
					break;

				default:
					break;
				}
			}
		});
		vpContent.setOnPageChangeListener(new OnPageChangeListener() {


			public void onPageScrollStateChanged(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					navRecommend.setChecked(true);
					break;
				case 1:
					navClassify.setChecked(true);
					break;
				case 2:
					navSubject.setChecked(true);
					break;
				case 3:
					navRank.setChecked(true);
					break;
				case 4:
					navInfo.setChecked(true);
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
		navRecommend.setChecked(true);
	}

	/**
	 * 广播接收器，改变角标的显示
	 * 
	 */
	class BadgeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (downloadingList.size() > 0) {
				badge.setText(downloadingList.size() + "");
				badge.show();
			} else {
				badge.hide();
			}
		}

	}

	private class IndexPagerAdapter extends FragmentPagerAdapter {

		public IndexPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			switch (arg0) {
			case 0:
				if (indexFragments[0] == null) {
					indexFragments[0] = new RecommendFragment_();
				}
				break;
			case 1:
				if (indexFragments[1] == null) {
					indexFragments[1] = new ClassifyFragment_();
				}
				break;
			case 2:
				if (indexFragments[2] == null) {
					indexFragments[2] = new SubjectFragment_();
				}
				break;
			case 3:
				if (indexFragments[3] == null) {
					indexFragments[3] = new RankFragment_();
				}
				break;
			case 4:
				if (indexFragments[4] == null) {
					indexFragments[4] = new InformationFragment_();
				}
				break;
			}
			return indexFragments[arg0];
		}

		@Override
		public int getCount() {
			return indexFragments.length-1;
		}

	}

	public void setCurrentItem(int i) {
		if (vpContent != null && vpContent.getChildCount() > i) {
			vpContent.setCurrentItem(i);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(receiveBroadCast);
	}
}
