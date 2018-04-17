package com.xtone.game87873.general.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xtone.game87873.MyApplication;

/**
 * ImageLoaderUtils.java
 * 
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-12-16 上午11:22:25
 */
public class ImageLoaderUtils {
	/**
	 * 加载网络图片
	 * 
	 * @param imageView
	 * @param imageUrl
	 * @param defaultImageRes
	 * @param failedImageRes
	 */
	public static void loadImg(ImageView imageView, String imageUrl,
			int defaultImageRes, int failedImageRes) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(defaultImageRes)
				.showImageOnFail(failedImageRes).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();

		ImageLoader.getInstance().displayImage(imageUrl, imageView, options);
	}
	public static void loadImgWithConner(ImageView imageView, String imageUrl,
			int defaultImageRes, int failedImageRes) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(defaultImageRes)
		.showImageOnFail(failedImageRes).cacheInMemory(true).displayer(new RoundedBitmapDisplayer(DensityUtil.dip2px(MyApplication.getInstance(), 15)))
		.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		
		ImageLoader.getInstance().displayImage(imageUrl, imageView, options);
	}
}
