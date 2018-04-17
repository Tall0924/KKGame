package com.xtone.game87873.general.utils;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.umeng.analytics.MobclickAgent;
import com.xtone.game87873.R;
import com.xtone.game87873.contants.UmengEvent;
import com.xtone.game87873.general.utils.volley.VolleyUtils;
import com.xtone.game87873.section.entity.HomeBanner;
import com.xtone.game87873.section.game.AppDetailActivity;
import com.xtone.game87873.section.game.AppDetailActivity_;
import com.xtone.game87873.section.game.ClassifyDetailActivity;
import com.xtone.game87873.section.game.ClassifyDetailActivity_;
import com.xtone.game87873.section.game.SubjectDetailActivity;
import com.xtone.game87873.section.game.SubjectDetailActivity_;
import com.xtone.game87873.section.gift.GiftDetailActivity_;

/**
 * 设置幻灯片效果的工具类
 * 
 * @author ywj
 * @version v1.0
 * @copyright 2010-2015
 */
public class SlideUtils {
	public static final String TYPE_GAME = "game";
	public static final String TYPE_SUBJECT = "topic";
	public static final String TYPE_CLASSIFY = "type";
	public static final String TYPE_TAG = "tag";
	public static final String TYPE_GIFT = "libao";

	/**
	 * 没有图片时加载一张默认图片
	 * 
	 * @param context
	 * @param pointGroup
	 * @param list
	 * @param points
	 */
	public static void initViewPager(Context context, LinearLayout pointGroup,
			List<ImageView> list, List<View> points) {
		ImageView imageView = new ImageView(context);
		// ImageView imageView2 = new ImageView(context);
		imageView.setScaleType(ScaleType.FIT_XY);
		// LayoutParams param = new LayoutParams(
		// LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// imageView.setBackgroundColor(context.getResources().getColor(R.color.black));
		// imageView.setLayoutParams(param);
		imageView.setImageResource(R.drawable.pic_banner_loading);
		// imageView2.setImageResource(R.drawable.pic_index_slide);
		// AsyncImageLoader.setAsynImages(imageView, "");
		list.add(imageView);
		// list.add(imageView2);
		// for (int j = 0; j < list.size(); j++) {
		// ImageView point = new ImageView(context);
		// if (j == 0) {
		// point.setBackgroundResource(R.drawable.dot_p);
		// } else {
		// point.setBackgroundResource(R.drawable.dot_n);
		// }
		// point.setScaleType(ScaleType.FIT_XY);
		// android.widget.LinearLayout.LayoutParams params = new
		// android.widget.LinearLayout.LayoutParams(
		// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// params.setMargins(5, 5, 5, 5);
		// point.setLayoutParams(params);
		// points.add(point);
		// pointGroup.addView(point);
		// }

	}

	/**
	 * 添加图片到幻灯片中
	 * 
	 * @param context
	 * @param pointGroup
	 * @param list
	 * @param points
	 */
	public static void setImageView(final Context context,
			LinearLayout pointGroup, List<ImageView> list, List<View> points,
			List<HomeBanner> banners, int netWidth, int netHeight) {
		RequestQueue queue = VolleyUtils.getRequestQueue(context);
		if (banners.size() == 0) {
			return;
		}
		list.clear();
		points.clear();
		pointGroup.removeAllViews();
		int width = CommonUtils.getScreenWidth(context);
		int height = CommonUtils.getFitHeightWithSize(context, netWidth,
				netHeight);
		if (banners.size() > 0) {
			ImageView imageView = createView(context, queue, banners, width,
					height, banners.size() - 1);
			list.add(imageView);
			if (banners.size() > 1) {
				for (int i = 0; i < banners.size(); i++) {
					ImageView image = createView(context, queue, banners,
							width, height, i);
					list.add(image);
				}
				ImageView image = createView(context, queue, banners, width,
						height, 0);
				list.add(image);
			}
		}

		if (banners.size() > 1) {
			for (int j = 0; j < banners.size(); j++) {
				ImageView point = new ImageView(context);
				// if (j == 0) {
				// point.setBackgroundResource(R.drawable.dot_p);
				// } else {
				point.setBackgroundResource(R.drawable.dot_n);
				// }
				point.setScaleType(ScaleType.FIT_XY);
				android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(5, 5, 5, 5);
				point.setLayoutParams(params);
				points.add(point);
				pointGroup.addView(point);
			}
		}

	}

	private static ImageView createView(final Context context,
			RequestQueue queue, List<HomeBanner> banners, int width,
			int height, int i) {
		ImageView imageView = new ImageView(context);
		imageView.setScaleType(ScaleType.FIT_XY);
		// AsyncImageLoader.setAsynImages(imageView, ApiConfig.getImageUrl(
		// urls.get(i), ImageSizeConfig.PRODUCT_DETAILS));
		final HomeBanner banner = banners.get(i);
		VolleyUtils.setURLImage(context, imageView, banner.getBannerUrl(),
				R.drawable.pic_banner_loading, R.drawable.pic_banner_loading,
				width, height);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (TYPE_GAME.equals(banner.getType())) {
					Intent intent = new Intent(context,
							AppDetailActivity_.class);
					intent.putExtra(AppDetailActivity.GAME_NAME,
							banner.getTitle());
					intent.putExtra(AppDetailActivity.GAME_ID, banner.getId());
					context.startActivity(intent);

				} else if (TYPE_TAG.equals(banner.getType())) {
					Intent intent = new Intent(context,
							ClassifyDetailActivity_.class);
					intent.putExtra(ClassifyDetailActivity.TAG_NAME,
							banner.getTitle());
					intent.putExtra(ClassifyDetailActivity.TAG_ID,
							banner.getId());
					context.startActivity(intent);
				} else if (TYPE_GIFT.equals(banner.getType())) {
					Intent intent = new Intent(context,
							GiftDetailActivity_.class);
					intent.putExtra("giftId", banner.getId());
					context.startActivity(intent);
				} else if (TYPE_SUBJECT.equals(banner.getType())) {
					Intent intent = new Intent(context,
							SubjectDetailActivity_.class);
					intent.putExtra(SubjectDetailActivity.SUBJECT_ID,
							banner.getId());
					intent.putExtra(SubjectDetailActivity.SUBJECT_TITLE,
							banner.getTitle());
					context.startActivity(intent);
				} else if (TYPE_CLASSIFY.equals(banner.getType())) {
					Intent intent = new Intent(context,
							ClassifyDetailActivity_.class);
					intent.putExtra(ClassifyDetailActivity.TYPE_NAME,
							banner.getTitle());
					intent.putExtra(ClassifyDetailActivity.TYPE_ID,
							banner.getId());
					context.startActivity(intent);
				}
				// 友盟计数统计
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("banner_id", banner.getId() + "");
				map.put("banner_title", banner.getTitle());
				map.put("banner_type", banner.getType());
				MobclickAgent.onEvent(context, UmengEvent.CLICK_BANNER, map);
			}
		});
		return imageView;
	}

	/**
	 * 添加图片到幻灯片中
	 * 
	 * @param context
	 * @param pointGroup
	 * @param list
	 * @param points
	 */
	public static void setImageViewUrl(Context context, RequestQueue queue,
			LinearLayout pointGroup, List<View> list, List<View> points,
			List<String> banners) {
		if (banners.size() == 0) {
			return;
		}
		list.clear();
		points.clear();
		pointGroup.removeAllViews();

		for (int i = 0; i < banners.size(); i++) {
			ImageView imageView = new ImageView(context);
			imageView.setScaleType(ScaleType.FIT_XY);
			// AsyncImageLoader.setAsynImages(imageView, ApiConfig.getImageUrl(
			// urls.get(i), ImageSizeConfig.PRODUCT_DETAILS));
			VolleyUtils.setURLImage(context, imageView, banners.get(i),
					R.drawable.pic_intro, R.drawable.pic_index_slide);
			list.add(imageView);
		}
		if (banners.size() > 1) {
			for (int j = 0; j < list.size(); j++) {
				ImageView point = new ImageView(context);
				// if (j == 0) {
				// point.setBackgroundResource(R.drawable.dot_p);
				// } else {
				point.setBackgroundResource(R.drawable.dot_n);
				// }
				point.setScaleType(ScaleType.FIT_XY);
				android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(5, 5, 5, 5);
				point.setLayoutParams(params);
				points.add(point);
				pointGroup.addView(point);
			}
		}

	}
}
