package com.xtone.game87873.section.game.adapter;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SlideAdapter extends PagerAdapter {

	private List<ImageView> slideViews;

	public SlideAdapter(List<ImageView> slideViews) {

		this.slideViews = slideViews;
	}


	@Override
	public int getCount() {
		return slideViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		((ViewPager) container).addView(slideViews.get(position), 0);
//		if (position < mBanners.size()) {
//			VolleyUtils.setURLImage(mQueue, slideViews.get(position), mBanners
//					.get(position).getBannerUrl(),
//					R.drawable.pic_banner_loading,
//					R.drawable.pic_banner_loading);
//		}
		return slideViews.get(position);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(slideViews.get(position
				% slideViews.size()));
	}

}
