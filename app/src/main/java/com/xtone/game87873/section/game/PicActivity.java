package com.xtone.game87873.section.game;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.xtone.game87873.R;
import com.xtone.game87873.general.base.BaseActivity;
import com.xtone.game87873.general.utils.CommonUtils;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.section.game.adapter.SlideAdapter;

/**
 * PicActivity.java
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-7-23 下午4:51:24
 */
@EActivity(R.layout.activity_pic_show)
public class PicActivity extends BaseActivity {

	@ViewById
	ViewPager vpSlide;
	@ViewById
	LinearLayout llDots;
	public static final String CURRENT_POSITION = "current_position";
	public List<ImageView> views;
	public static List<String> picUrls;
	private List<ImageView> dots;
	private SlideAdapter myAdapter;
	private int cur;

	@AfterViews
	void afterViews() {
		cur = getIntent().getIntExtra(CURRENT_POSITION, 0);
		int width = CommonUtils.getScreenWidth(this);
		int height = CommonUtils.getScreenHeight(this);
		if (picUrls != null && picUrls.size() > 0) {
			views = new ArrayList<ImageView>();
			for (int i = 0; i < picUrls.size(); i++) {
				ImageView imageView = new ImageView(this);
				imageView.setScaleType(ScaleType.FIT_CENTER);
				VolleyUtils.setURLImage(this, imageView, picUrls.get(i),
						R.drawable.pic_big, R.drawable.pic_big, width, height);
				views.add(imageView);
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						finish();

					}
				});
			}
			dots = new ArrayList<ImageView>();
			int len = views.size();
			for (int i = 0; i < len; i++) {
				ImageView point = new ImageView(this);
				if (i == 0) {
					point.setBackgroundResource(R.drawable.dot_p);
				} else {
					point.setBackgroundResource(R.drawable.dot_n);
				}
				point.setScaleType(ScaleType.FIT_XY);
				android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(5, 5, 5, 5);
				point.setLayoutParams(params);
				dots.add(point);
				llDots.addView(point);
			}
			myAdapter = new SlideAdapter(views);
			vpSlide.setAdapter(myAdapter);
			vpSlide.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int position) {
					for (ImageView v : dots) {
						v.setBackgroundResource(R.drawable.dot_n);
					}
					dots.get(position).setBackgroundResource(R.drawable.dot_p);
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {

				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
				}
			});
		}
		vpSlide.setCurrentItem(cur);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		views.clear();
	}

}
