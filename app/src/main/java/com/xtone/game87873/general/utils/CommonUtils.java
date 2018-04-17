package com.xtone.game87873.general.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.WindowManager;

public class CommonUtils {

	/**
	 * 获取屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		// WindowManager wm = this.getWindowManager();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		@SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
		return width;
	}

	/**
	 * 获取屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		@SuppressWarnings("deprecation")
		int height = wm.getDefaultDisplay().getHeight();
		return height;
	}

	/**
	 * 获取适应屏幕的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getFitHeight(Context context, int imgRes) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		@SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), imgRes, options);
		int height = (int) (1.0 * width / options.outWidth * options.outHeight);
		return height;
	}

	/**
	 * 获取适应屏幕的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getFitHeightWithSize(Context context, int width,
			int height) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		@SuppressWarnings("deprecation")
		int screenWidth = wm.getDefaultDisplay().getWidth();
		return (int) (1.0 * screenWidth / width * height);
	}

	public static int getHeightWithSize(Context context, int width, int height,
			int neewWidth) {
		return (int) (1.0 * neewWidth / width * height);
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

}
